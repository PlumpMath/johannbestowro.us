(ns johann.views.complector
  (:require
            ;;[johann.notes :as notes]
            [johann.views.css :as css]
            [johann.views.html :as page]
            ))

(defn dream-page [edn]
  (page/dream css/dream edn))




