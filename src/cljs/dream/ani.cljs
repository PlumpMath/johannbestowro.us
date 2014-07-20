(ns dream.ani
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [hiccups.core :refer [html]])
  (:require [goog.events :as events]
            [cljs.core.async :as async :refer [>! <! put! chan sliding-buffer pub sub]]
            [om.core :as om :include-macros true]
            [cljs.reader :as reader]
            [cljs.reader :as edn]
            [goog.dom :as gdom]
            [goog.dom.classes :as class]
            [goog.debug :as debug]
            [om-tools.core :refer-macros [defcomponent]]
            [om-tools.dom :as dom :include-macros true]))

; hope to fix the mobile safari? if iphone, reverse props list before
; making a js array? gah.



(when-not (exists? js/navigator)
  (set! js/navigator #js {})
  )
;
(defn group [elems]
  (let [ jselems  (clj->js (into [] (map (fn [{:keys [node props time]}]
                                           (print props)
                                           
                                           
                                           (let [
                                                  ;props (if mob-safari? (into [] (reverse props))   props)
                                                 jsprops (clj->js props)]
                                             (print jsprops)
                                             (js/Animation. node jsprops time)))
                                         elems)))
         group (js/AnimationGroup. jselems)]

    (.play (aget js/document "timeline") group)))


(defn create-animation [node props time]
  (let [jsprops (clj->js  props)
        timing-dict (clj->js {:duration .5 :iterations 8})
        ani (js/Animation. node jsprops time)]

   (print jsprops)
  (.play
   (aget js/document "timeline") ani)))


(defn add-class [node class]
  (class/toggle node class))

(defn toggle-class [node owner class timeout]
  (class/toggle node class)
  (js/setTimeout #(class/toggle node class) timeout)
  (om/set-state! owner :trans true))
