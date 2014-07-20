(ns dream.x
  (:require-macros [cljs.core.async.macros :refer [go go-loop]]
                   [hiccups.core :refer [html]])
  (:require  [om-tools.core :refer-macros [defcomponent]]
             [om-tools.dom :as dom :include-macros true]
             [goog.debug :as debug]
             [hiccups.runtime :as hiccupsrt]
             [om.core :as om :include-macros true]
             [cljs.core.async :as async :refer [>! <! put! chan sliding-buffer pub sub]]))

(enable-console-print!)

(comment

  (defcomponent paragraph [data owner]
    (render-state [_ _ ]
                  (let [temp (html data)]
                    (dom/div {:dangerouslySetInnerHTML {:__html temp}}))))


  (defcomponent x [data owner]
    (render-state [_ {:keys [sortk artsel selected]}]

                  (let [articles (into [] ((data :axis) (data :route)))]
                    (dom/div {:class "flex"}
                             (om/build paragraph selected)
                             (dom/ul (map (fn [item] (dom/li {:on-click #(om/set-state! owner :selected (item :content))}
                                                            (item :title))) articles)))))))


