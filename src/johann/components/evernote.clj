(ns johann.components.evernote
  (:import (com.evernote.edam.notestore NoteMetadata NoteStore NoteFilter NotesMetadataResultSpec)
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

(def get-guid (memfn ^NoteMetadata getGuid))

(defn tagguid->tagname [[guid] tags]
   (let [{:keys [name]} (first (filter #(= guid (% :guid)) tags))]
     name))

(defn get-posts [user notebook-uid]
  (->> (notes/basic-notes-for-notebook user
                                       notebook-uid)
       (sequence (comp (map get-guid)
                       (map (partial notes/get-note user))
                       (map bean)
                       (map #(select-keys % [:title :created :content :tagGuids]))))))

(defn get-tags [user notebook-uid]
  (->> (notes/get-all-tags-for-notebook user notebook-uid)
       (sequence (comp (map bean)
                       (map #(select-keys % [:guid :name]))))))

(defn make-blog-edn [tags posts]
  (->> posts
       (map-indexed (fn [idx {:keys [title created content tagGuids]}]
                      (hash-map
                       :topic (tagguid->tagname tagGuids tags)
                       :created (coerce/from-long created)
                       :title title
                       :content (-> content
                                    hicv/html->hiccup
                                    first
                                    (assoc 0 :div))
                       :idx idx)))))

(defn fetch-posts [user notebook-uid]
  (let [tags (get-tags user notebook-uid)
        posts (get-posts user notebook-uid)]
    (make-blog-edn tags posts)))

(defn post-from-title
   [title post-cache]
   (filter (comp (partial = title) :title) post-cache))

(s/defrecord EvernoteCache [context :- s/Str notebook-uid :- s/Str
                            access-token :- s/Str notestore-url :- s/Str]
  Lifecycle
  (start [this]
         (let [user {:notestore-url notestore-url
                     :access-token access-token}
               post-cache (memo/ttl #(fetch-posts user notebook-uid))]
           (assoc this :post-cache post-cache) ))
  (stop [this]
        this)
  RouteProvider
  (routes [this]
          (let [post-cache ((:post-cache this))]
            [context {["on/" :post-title] {:get (fnk [[:params post-title]]
                                                  (-> (response (post-from-title post-title post-cache))
                                                      (content-type "text/html")))}}])))

(def new-evernote-cache
  (-> map->EvernoteCache
      (ctr/wrap-class-validation EvernoteCache)
      (ctr/wrap-defaults {:context "/"})
      (ctr/wrap-kargs)))
