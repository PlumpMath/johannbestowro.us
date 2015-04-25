(ns johann.components.evernote
  (:import (com.evernote.edam.notestore NoteStore NoteFilter NotesMetadataResultSpec)
           (com.evernote.edam.type Note Notebook NoteSortOrder))
  (:require [bidi.bidi :refer (RouteProvider)]
            [clj-time.coerce :as coerce]
            [clj-time.format :as format]
            [clojure.core.memoize :as memo]
            [clojure.edn :as edn]
            [clojurenote.enml :as enml]
            [clojurenote.notes :as notes]
            [clojurenote.users :as users]
            [com.stuartsierra.component :refer (Lifecycle)]
            [hiccup-bridge.core :as hicv]
            [ib5k.component.ctr :as ctr]
            [plumbing.core :refer :all]
            [ring.util.response :refer (response content-type)]
            [schema.core :as s]
            [taoensso.timbre :as log]))

(s/defrecord EvernoteCache [context :- s/Str notebook-uid :- s/Str access-token :- s/Str
                            notestore-url :- s/Str]
  Lifecycle
  (start [this]
         this)
  (stop [this]
        this)
  RouteProvider
  (routes [this]
          (vector context {:get (fn [req]
                                  (->> (notes/basic-notes-for-notebook
                                        {:notestore-url notestore-url
                                         :access-token access-token} notebook-uid)
                                       (map (fn [note]
                                              (.. note getGuid)))
                                       println)

                                  (response "america"))})))

(def new-evernote-cache
  (-> map->EvernoteCache
      (ctr/wrap-class-validation EvernoteCache)
      (ctr/wrap-defaults {:context "/"})
      (ctr/wrap-kargs)))
