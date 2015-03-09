(ns user
  (:require
   [clojure.tools.namespace.repl :refer [disable-reload! refresh]]
   [com.stuartsierra.component :as component]
   [johann.system :refer [new-production-system]]))

(def system nil)

(defn- stop-system [s]
  (when s (component/stop s)))

(defn init []
  (alter-var-root #'system (fn [_] (new-production-system)))
  :ok)

(defn start []
  (alter-var-root #'system (fn [system]
                             (try
                               (component/start system)
                               (catch Exception e
                                 (when-let [s (:system (ex-data e))]
                                   (component/stop s))
                                 (throw e)))))
  :started)

(defn stop []
  (alter-var-root #'system stop-system)
  :stopped)

(defn go []
  (init)
  (start))

(defn clear []
  (alter-var-root #'system #(do (stop-system %) nil))
  :ok)

(defn reset []
  (clear)
  (refresh :after 'user/go))
