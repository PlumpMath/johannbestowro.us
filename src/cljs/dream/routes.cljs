(ns dream.routes
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [>! <! put! chan sliding-buffer pub sub]]
            [om.core :as om :include-macros true]
            [goog.events :as events]
            [goog.debug :as debug]
            [dream.ani :as ani]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom :include-macros true])
  (:import goog.history.EventType
           goog.history.Html5History))

(enable-console-print!)

(when-not (exists? js/setTimeout)
  (set! js/setTimeout (fn [fun time]  time)))


(defn update-title! [{:keys [new-state]}]
  (let [{:keys [route]} new-state]
      (set! js/document.title (route :view))))

(defn update-history! [owner {:keys [new-state old-state]}]
  (let [history (om/get-state owner :history)
        {:keys [route]} new-state]
    (if-not (= old-state new-state)
      
      (if (route :view)
        (.setToken history (route :view))
        (.setToken history "/"))
      (.replaceToken history (route :view)))))

(defn- stop-router!
  "Takes an Om component with a history object.
  Disables the history object."
  [owner]
  (let [history (om/get-state owner :history)]
    ; Remove goog.events listeners from history object.
    (events/removeAll history)
    ; Disable history object.
    (.setEnabled history false)))

(defn start-history! [data owner]
  (let [history (doto (Html5History.)
                  (.setUseFragment false)
                  (.setPathPrefix "")
                  (.setEnabled true))
        nav-tokens-chan (om/get-shared owner :nav-tokens)]
    (events/listen history EventType.NAVIGATE
                        (fn [event]
                          (when (.-isNavigation event)
                            (put! nav-tokens-chan (.-token event)))))
    (om/set-state! owner :history history)))


;; take two things
;; check if there are any nils
;; if so return what idx was nil
;; codify oprop or nprop by position in tuple

;; other way



(defn start-tx-loop!
  "listens for nav related transactions"
  [owner]
  (let [transactions-pub-chan (om/get-shared owner :transactions-pub)
        txs (sub transactions-pub-chan :nav (chan))]
    (go-loop [tx (<! txs)]
             (update-title! tx)
             (update-history! owner tx)
             (recur (<! txs)))))

(defcomponent clear-route [data owner {:keys [parent]}]
  (init-state [_]
              {:show false}
              )
  (will-update [_ nprops nstate]
               (let [oprops (om/get-props owner)
                     node (om/get-node owner)]

                 (print oprops nprops)
                 (when  (or (nil? oprops) (= oprops "/"))
                   (om/update-state! owner :show not))
                 
                 (when (or (nil? nprops) (= nprops "/"))
                   (om/update-state! owner :show not)
                   )
                 ))
  (render-state [_ {:keys [show]}]
                (let [trou (om/get-shared owner :transroute)
                      onclick (fn [e] (put! trou :clear))]
                  ((or parent identity)
                   (dom/span {
                              :class (if show "trans fa fa-caret-up" "trans fa fa-caret-up hide")
                               
                              :style {
                                        ;:background-color "purple"
                                      :font-size "20vh"}
                              :on-click #(onclick %)})))))

(defn build-page [data {views :page-views} owner]
  (let [{:keys [view context]} (data :route)]
    
    
    
    (if (and (not= view "/")  view) ;; safari defaults route to "/"
        
      (om/build (views view) data {:init-state {:context context}
                                   :opts {:clear-route clear-route}})
      (dom/span "")
      )))

(defn troute-loop [data owner]
  (let [trou (om/get-shared owner :transroute)
        navtok (om/get-shared owner :nav-tokens)]
    (go-loop [key (<! trou)]
             (case key
               :clear (do
                        
                         (ani/create-animation (om/get-node owner) [{:transform "translate3d(0,0,0)" :opacity 1}  {:transform  "translate3d(0, -300%, 0)" :opacity 0}] 350)
                          (js/setTimeout #(put! navtok :clear) 250)))
             (recur (<! trou)))))

(defn start-nav-loop! [{:keys [route]} owner]
  (let [nav-tokens-chan (om/get-shared owner :nav-tokens)]
    (go-loop [token(<! nav-tokens-chan)]
             (case token
               :clear (om/update! route :view nil :nav)
               (om/update! route :view token :nav))
             (recur (<! nav-tokens-chan))
             )))











(defn start-router! [data owner]
  (start-history! data owner)
  (start-nav-loop! data owner))

(defcomponent router [data owner opts]
  (display-name [_] "router")
  (will-mount [_]
              (when (om/get-shared owner :transroute)   
                (troute-loop data owner)
                (start-tx-loop! owner)))
  (render [_] (build-page data opts owner))
  (did-mount [_] (start-router! data owner))
  (will-unmount [_] (stop-router! owner)))
