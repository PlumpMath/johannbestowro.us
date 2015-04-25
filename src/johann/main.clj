(ns johann.main
  (:gen-class))

(defn -main [& args]
  ;; We eval so that we don't AOT anything beyond this class
  (def systems (eval '(do (require '[taoensso.encore :refer [merge-deep]])
                          (require 'johann.systems.server)
                          (require 'modular.component.co-dependency)

                          (println "Starting example")
                          {:server
                           (-> (johann.systems.server/new-production-system)
                               johann.system/start)})))
  (println "System started"))
