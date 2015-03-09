(ns johann.website
  (:require
   [clojure.tools.logging :as log]
   [com.stuartsierra.component :as component]
   [plumbing.core :refer :all :exclude [update]]
   [schema.core :as s]
   [clojure.pprint :refer (pprint)]
   [modular.ring :refer (WebRequestHandler WebRequestMiddleware)]
   [modular.bidi :refer (WebService as-request-handler)]
   [ring.util.response :as resp]
   [ring.util.request :as req]
   [ring.middleware.defaults :refer (wrap-defaults site-defaults)]
   [bidi.bidi :refer (path-for compile-route)]
   [bidi.ring :refer (redirect files)]
   [johann.notes :as notes]
   [johann.views.complector :as views]))

(def htmlify (fn->
              (resp/content-type "text/html")))

(defn title->idx [title]
  (let [post (first (filter #(= (% :title) title)
                            (notes/note-cache-prod)))]
    (:idx post "not found")))

(defrecord StaticFileService [uri-context dir]
  WebService
  (request-handlers [_] {})
  (routes [_]
    [uri-context (files {:dir dir})])
  (uri-context [_] "/"))

(def new-static-file-service-schema
  {:uri-context s/Str
   :dir s/Str})

(defn new-static-file-service [& {:as opts}]
  (->> opts
       (merge {})
       (s/validate new-static-file-service-schema)
       (map->StaticFileService)))

(defrecord Middleware []
  WebRequestMiddleware
  (request-middleware [_]
    (fn [handler]
      (let []
        (-> handler
            (wrap-defaults
             (-> site-defaults
                 (dissoc :static))))))))

(defn new-middleware
  [& {:as opts}]
  (->> (map->Middleware {})
       (<- (component/using []))))

(defrecord Website [html-template ip port transition-duration]
  WebService
  (request-handlers [this]
    {::home (fn [req]
              (let [state {:route {:view nil
                                   :context []}
                           :routes {:blog notes/topic-sort}}
                    dream (views/dream-page state)]
                (htmlify (resp/response dream))))
     ::post (fn [req]
              (let [title (get-in req [:route-params :title])
                    topic (get-in req [:route-params :topic])
                    state  {:route {:view "blog"
                                    :context [topic (title->idx title)]}
                            :routes {:blog notes/topic-sort}}
                    dream (views/dream-page state)]
                (htmlify (resp/response dream))))})

  (routes [_] (compile-route ["/" {"" ::home
                                   ["on/" :topic :title] ::post}]))

  (uri-context [_] "")

  WebRequestHandler
  (request-handler [this] (as-request-handler this)))

(def new-website-schema
  {:ip s/Str
   :port s/Int
   :transition-duration s/Int})

(defn new-website
  [& {:as opts}]
  (->> opts
       (merge {})
       (s/validate new-website-schema)
       map->Website))
