(ns johann.async
  (:require [org.httpkit.server :as http-kit]))

(defn async-handler [req]
  (println (req :session))
  (http-kit/with-channel req channel
    (doto channel
      (http-kit/on-close (fn [status]
                           (println status)))
      (http-kit/on-receive (fn [sig]
                             (println sig))))))
