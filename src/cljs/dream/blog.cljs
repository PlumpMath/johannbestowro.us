(ns dream.blog
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [hiccups.core :refer [html]])
  (:require  [om-tools.core :refer-macros [defcomponent]]
             [om-tools.dom :as dom :include-macros true]
             [goog.debug :as debug]
             [dream.ani :as ani]
             [hiccups.runtime :as hiccupsrt]
             [om.core :as om :include-macros true]
             [cljs.core.async :as async :refer [>! <! put! chan sliding-buffer pub sub close! timeout]]))






(defn resort [topics topic]
  (conj (filter #(not= % topic) topics) topic))

(defn idxer [chan owner [key val :as idx]]
  (put! chan idx)
  (om/set-state! owner :idx val))


(defcomponent essay [data owner]
    (will-update [_ nprops {:keys [tophov] :as nstate}]
               (let [oprops (om/get-props owner)]
                 (when (not= nprops oprops)
                   (ani/group [{:node (om/get-node owner "title") :props [
                                                                          {:transform "translate3d(0, -100%, 0)"}
                                                                          {:transform "translate3d(0, 0, 0)"}]
                                :time 500}

                               {:node (om/get-node owner "content") :props [{:opacity 0}
                                                                             {:opacity 1}] :time 1000}])
                   ))
               
               #_(if tophov
                 (ani/create-animation (om/get-node owner) [{:transform "translate3d(0, 0, -500px)" :opacity .5 :background-color "black"}] 250 )
                 (ani/create-animation (om/get-node owner) [{:transform "translate3d(0, 0, 0px)" :opacity 1 :background-color "pink"}] 250 )
                 )
               )
  (render-state [_ {:keys [c]}]
               (dom/div {:ref "modal"
                                       :class "figure flex column back"
                                       :style {:width "90%"
                                               :height "100%"

                                               :background "#DDD6E8"}
                                       }
                                      (let  [padding {:padding "3%"}
                                             padify #(assoc % :style padding)]
                                        (dom/div {:style {
                                                          :width "90%"
                                                          :background-color "white"
                                                          :font-size "8.0vh"}
                                                  :class "flex"}



                                                
                                                 (dom/div {:ref "title"
                                                           :style {:font-sze "5vh"}} (data :title))
                                                 (dom/div  (padify {:class "fa fa-long-arrow-right" :on-click #(put! c [:flip])}))))
                                      (dom/div {:style {:height "70%"
                                                        :overflowY "scroll"
                                                        :width "80%"
                                                        :padding "5%"}
                                                :ref "content"
                                                :dangerouslySetInnerHTML {:__html (html (data :content))}
                                                }))))







(defcomponent esselement [data owner]
  (render-state [_ {:keys [c]}]
                
                
                (dom/div {:class "hover"
                          :style {:margin-left "5.5%"}
                          :on-click #(put! c [:idxflip [(data :topic) (data :idx)]])} (str  (data :title)))))

(defcomponent topic [[topic posts] owner]
  (did-update [_ props state]
              (ani/create-animation (om/get-node owner) [{:opacity 0} {:opacity 1}] 35)
              )
  (will-update [_ nprops nstate]
               (let [oprops (om/get-props owner)]
                 (when (not= oprops nprops)
                   (ani/create-animation (om/get-node owner) [{:opacity 1} {:opacity 0}] 25)

                   ))

               

               
               )
  (render-state [_ {:keys [c]}]
                (dom/div {:style {:font-size "5vh"}}
                         (dom/span {
                                    :style {:font-size "10vh"}} topic)
                         (om/build-all esselement posts {:init-state {:c c}})
                         )))


;; ok i need some sort of process that gets fed a sequence of numbers
;; and sets the state of a component for every put. so the number seq
;; exists outside the go-block, putting values on the go-block, and


(defn random-thing [chan]
  (loop [num 0]
    (print num)
    (if (> num 100)
        num
        (recur (do
                 (put! chan num)
                 (inc num))))))


(defn rand-process [chan owner]
  (let [valfn #(om/set-state! owner :opacity %)]
    (go-loop [value (<! chan)]
             (<! (timeout 16))
             (valfn value)
             (recur (<! chan)))))

;word


(defcomponent blog [data owner {:keys [clear-route]}]
  (init-state [_]
              (let [[top posts] (first (seq ((data :routes) :blog)))  ]
                {:c (chan)
                 :topidx 0
                 :idx [top -1 ]
                 })      
              )  
  (will-mount [_]
              (let [c (om/get-state owner :c)
                    [top ess :as ctx] (om/get-state owner :context)]
                (case (count ctx)
                  2 (doto owner 
                      (om/set-state! :idx ctx)
                      (om/set-state! :flip true))
                  (print "Well then")
                  )
                (let [rand-chan (chan)]
                  (rand-process rand-chan owner)
                  (random-thing rand-chan))
                
                (go-loop [ [token val] (<! c)]
                         (case token
                           :idx (om/set-state! owner :idx val)
                           :flip (om/update-state! owner :flip not)
                           :idxflip (doto owner
                                      (om/set-state! :idx val)
                                      (om/update-state! :flip not)
                                      )
                           )

                         (recur (<! c)))))


  (render-state [_ {:keys [idx c flip topidx opacity]}]
                (let [blog ((data :routes)  :blog)
                      blogseq (seq  ((data :routes) :blog))
                      blogcount (count blogseq)]
                  (print "render" opacity)
                  (dom/section {:class "full neutral"
                                :style {:transform-style "preserve-3d"
                                        :opacity (/ opacity 100)}}
                   
                               (dom/div {:id "card"
                                         :class (if flip "full trans flipped" "full trans")
                                         } ; this gets flipped
                                        (dom/div {:class "figure front full flex"}
                                                 (dom/div
                                                  
                                                  (if (> blogcount 1) (dom/button {:on-click #(om/update-state! owner :topidx (fn [pc]  (mod (inc  pc) blogcount)))}))
                                      
                                                  (om/build topic (nth blogseq topidx)
                                                           
                                                            {:init-state {:c c}})))
                                
                                  
                                        (om/build essay (when-let [[key ess] idx]   (nth (reverse (blog key)) ess {:content ""}))
                                                  {:init-state {:c c} })
                                        )))))






                  

                                        
