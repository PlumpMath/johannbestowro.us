(ns dream.root
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [hiccups.core :refer [html]])
  (:require [goog.events :as events]
            [cljs.core.async :as async :refer [>! <! put! chan sliding-buffer pub sub]]
            [om.core :as om :include-macros true]
            [cljs.reader :as reader]
            [cljs.reader :as edn]
            [goog.dom :as gdom]
            [goog.dom.classes :as class]
            [dream.routes :as routes]
            [dream.x :as x]
            [om-websocket.core :refer [om-websocket]]

            [dream.blog :as blog]
            [goog.debug :as debug]
            [om.dom :as kom :include-macros true]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom :include-macros true]))


(if (exists? js/console)
  (enable-console-print!)
  (set-print-fn! js/print))



; change
(declare app-container
         app-state)


(enable-console-print!)


(defn transition [length type]
  {:transition (str "all " length " " type)})


(defn translator [scale x y deg]
  {:-webkit-transform (str " translate(" x "px," y "px) "
                           "rotate(" deg "deg) "
                           "scale(" scale ")"
                           )})

(defcomponent nav-el [data owner]
  (display-name [_] "nav-el")
  (render-state [_ {:keys [selected clicksort]}]
                (let [navc (om/get-shared owner :nav-tokens)
                      webc (om/get-shared owner :websocket->)
                      onclick (fn [event]
                                (.preventDefault event)
                                (put! navc data)) ]
                  (dom/div {:href (str data)
                            :class (if (= data selected)
                                     "block selected"
                                     "block well-named")}
                           (dom/a {:href (str data)
                                   :style {:display "inline-block"}
                                   :on-click #(onclick %)} data

                                   )))))

(defcomponent nav [{:keys [route] :as data} owner]
  (init-state [_] {:sortk (map name (keys data))
                   :artfn (fn [int]
                            (let [sortk (map name (keys (data :axis)))
                                  f-key   (first sortk)
                                  lgth (count ((data :axis) f-key))]
                              (mod int lgth)))
                   :artidx 0
                   
                   })
  (will-update [_ _ _]
               (let [sortk (om/get-state owner :sortk)
                     artfn (fn [int]
                             (let [sortk (map name (keys (data :axis)))
                                   f-key (first sortk)
                                   lgth (count ((data :axis) f-key))]
                               (mod int lgth)))
                     node (om/get-node owner)]

                 (om/set-state! owner :artfn artfn)
                 ))
  (render-state [_ {:keys [sortk artfn artidx clicksort view]}]
                (let [navc (om/get-shared owner :nav-tokens)]
                  
                  (dom/div {:class "column flex"}
 
                           (om/build routes/clear-route view {:opts {:parent  #(dom/div {:style {:position "absolute"
                                                                                                 :top 0}} %)} })
                           (om/build-all nav-el sortk {:init-state {:clicksort clicksort}
                                                       :state {:selected (first sortk)}})
                           (dom/div {:class "column flex"
                                     :style {:position "absolute"
                                             :bottom 0
                                             :left 0}}
                                    (dom/a {:class "fa fa-twitter-square socials" :href "https://twitter.com/opinionsonline"} "")
                                    (dom/a {:class "fa fa-youtube-square socials" :href "http://www.youtube.com/neurosisnow"} ""))))))



(defcomponent app-view [{:keys [route] :as data} owner]
  (render-state [_ {:keys [trans]}]

                (dom/div {:class "flex-start full perspect"
                          :style {:outline "none"}
                          :tabIndex "1"}
                         (dom/div {:class "flex"

                                   :style {:height "100%"
                                           :zIndex "9"}
                                   :ref "nav"}
                                  (om/build nav (data :routes) {:state {:view (route :view)
                                                                        }}  ))
                         
                         (om/build routes/router data {:opts {:page-views {"blog" blog/blog}}}))))


(defn out-coord [chan data owner websock]
  (let [sockout #(.send websock [% %2])]
    (go-loop [[key val :as sig] (<! chan)]
             (case key
               :sig (sockout key ""))
             (recur (<! chan)))))

(defn in-coord [chan data owner]
  (go-loop [signal (<! chan)]
           (print signal)
           ))


(defcomponent world [data owner]
  (render-state [_ _]
                (om/build om-websocket data {:opts {:connection-str (str "ws://" js/location.host "/ws")
                                                    :in-coord in-coord
                                                    :out-coord out-coord
                                                    :child app-view}})
                ))



(defn render
  "Renders the app to the DOM.
  Can safely be called repeatedly to rerender the app."
  [app-container]
  (let [transactions (chan)
        transactions-pub (pub transactions :tag)] ; look at this, johann
    (om/root
     world
     app-state
     {:target app-container
      :tx-listen #(put! transactions %)
      :shared {:nav-tokens (chan)
               :transactions transactions
               :websocket-> (chan)
               :transroute (chan)
               :transactions-pub transactions-pub}})))

(defcomponent fuck [data owner]
  (render-state [_ _]
                (dom/h1 "fuck")
                ))


(defn ^:export render-to-string
  "Takes an app state as EDN and returns the HTML for that state.
  It can be invoked from JS as `omelette.view.render_to_string(edn)`."
  [state-edn]
  (->> state-edn
       reader/read-string
       (om/build app-view)
       kom/render-to-str))


(defn ^:export init [app state]
  (let [edn-node (gdom/getElement state)
        edn-text (.-textContent edn-node)
        edn (edn/read-string edn-text)
        target-node (gdom/getElement app)

        ]
    (set! app-state (atom edn))
    (render target-node)))
