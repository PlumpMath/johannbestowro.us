(ns johann.components.evernote
  (:require [bidi.bidi :refer (RouteProvider)]
            [com.stuartsierra.component :refer (Lifecycle)]
            [ib5k.component.ctr :as ctr]
            [plumbing.core :refer :all]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(s/defrecord TestRoute [context :- s/Str]
  Lifecycle
  (start [this]
         this)
  (stop [this]
        this)
  RouteProvider
  (routes [this]
    (vector context {:get (fn [req]
                            (println req)
                            req)})))

(def new-test-route
  (-> map->TestRoute
      (ctr/wrap-class-validation TestRoute)
      (ctr/wrap-defaults {:context "/"})
      (ctr/wrap-kargs)))
