(ns dev
  (:require [dev-components.visualization :refer (new-system-visualizer)]
            [johann.systems.server :refer (new-production-system)]
            [johann.utils.config :refer [config]]
            [johann.utils.maker :refer [make]]))

(defn visualization [system config]
  (assoc system
         :visualization (make new-system-visualizer config
                              :output-dir "./viz"
                              :options {:dpi 100}
                              :system system)))

(defn new-development-system
  []
  (let [config (config)]
    (-> (new-production-system)
        (visualization config))))
