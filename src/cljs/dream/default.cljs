(ns dream.default
  (:require-macros [cljs.core.async.macros :refer [go go-loop]])
  (:require [cljs.core.async :as async :refer [>! <! put! chan sliding-buffer timeout pub sub]]
            [om.core :as om :include-macros true]
            [goog.events :as events]
            [goog.debug :as debug]
            [dream.ani :as ani]
            
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom :include-macros true]))



(defn make-cell [owner ref  x y]
  (let [node (om/get-node owner ref)
        ctx  (.getContext node "2d") ]
    (go (while true
          (set! (.-fillStyle ctx) (str "rgb(" (rand-int 255)    ","  (rand-int 255)  "," (rand-int 255) ")" ))
          (.fillRect ctx x y 10 10)
          (<! (timeout (rand-int 1000)))))))

(defn make-scene [owner rows cols ref]
  (dotimes [x cols]
    (dotimes [y rows]
      (make-cell owner ref (* 10 x) (* 10 y)))))
;




;ork




(defcomponent default [data owner opts]
  (init-state [_]
              {:canvchan (chan)})
  (did-mount [_]
             #_(make-scene owner 5 5 "canvas"))
  (render-state [_ {:keys [mouse-pos canvchan]}]
                (let [x (mod (get mouse-pos 0 255) 255)
                      y (mod (get mouse-pos 1 255) 255)
                      z (- x y)
                      canvchan (om/get-state owner :canvchan)
                      rgbstr (str "rgb(" x "," y ", " z ")")]
                  (dom/section {:class "full flex"
                                :background-color "gray"
                                }
                               
                               
                               (comment (dom/canvas {:ref "canvas"
                                                     :class ""
                                        ;:style {:background rgbstr}     
                                        ;:on-mouse-move #(om/set-state! owner :mouse-pos (yank-mouse-loc %))
                                        ;:on-mouse-move #(draw! owner canvchan %)
                                                     }
                                                    
                                                    )
                                        (dom/article 
                                         (dom/ul (dom/li "My current orienting inquiries:"
                                                         (dom/ul
                                                          (dom/li "What is the relationship between inherents and coherence?")
                                                          (dom/li "What is the relationship between grief and novelty?"))))
                                         
                                         )))
                  ))
)
