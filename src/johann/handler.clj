(ns johann.handler
  (:use compojure.core
        ring.util.response)
  (:require [compojure.handler :as handler]
            [compojure.route :as route]
            [johann.notes :as notes]
            [johann.async :as async]
            [johann.views.complector :as views]
            [clojure.core.async :refer [chan <! >! put! close! go-loop]]
            [org.httpkit.server :as http-kit]
            [ring.util.response :as resp]
            [ring.middleware.reload :as reload]
            [ring.middleware.edn :refer [wrap-edn-params]]
            [clojure.java.io :as io])
  (:import [javax.script
            Invocable
            ScriptEngineManager]))


(defn nashie [edn]
  (let [js (doto (.getEngineByName (ScriptEngineManager.) "nashorn")
                                        ; React requires either "window" or "global" to be defined.
                 (.eval "var global = this")
                 (.eval (-> "public/javascripts/dream/dream.js"
                            io/resource
                            io/reader)))
            view (.eval js "dream.root")
            render-to-string (fn [edn]
                               (.invokeMethod
                                ^Invocable js
                                view
                                "render_to_string"
                                (-> edn
                                    list
                                    object-array)))]
    
    (render-to-string (str edn))))

(defn uuid [] (str (java.util.UUID/randomUUID)))

(defn title->idx [title]
  (let [post (first (filter #(= (% :title) title) (notes/note-cache-prod)))]
    (:idx post "not found")))

(defn assign-uuid [app]
  (fn [{session :session :as req}]
    
    (if-not (session :uuid)
      (do
        (println "this is the case, johann")
        (app (assoc req :session {:uuid (uuid)})))
      (app req))))

;

(defroutes app-routes
  (GET "/" [] (views/dream-page  {:route {:view nil
                                                :context []

                                                } :routes {:blog notes/topic-sort}}))
  (GET "/on/:topic/:title" [topic title] (views/dream-page  {:route {:view "blog"
                                                                     :context [topic (title->idx title) ]

                                                                     } :routes {:blog notes/topic-sort}}))
  (GET "/ws" [] async/async-handler)
  (route/resources "/" {:root "public"})
  (route/not-found "Not Found"))

(def app
  (->
   app-routes
   assign-uuid
   handler/site
    reload/wrap-reload
   wrap-edn-params
   ))

(defn -main
  ([port]
    (let [Port (Integer/parseInt port)]
      (println "server running w/ port" port)
      (print "mode is " (System/getenv "MODE"))
      (http-kit/run-server app {:port Port})))
  ([] ()
   (let [strPort (System/getenv "PORT")
         Port (Integer/parseInt strPort)]
     (println "we're running on:" sort)
     (http-kit/run-server app {:port Port})) ))
















