(ns johann.notes
  (:import
   (com.evernote.edam.notestore NoteStore NoteFilter NotesMetadataResultSpec)
   (com.evernote.edam.type Note Notebook NoteSortOrder))
  (:require
   [clojurenote.notes :as notes]
   [clojurenote.users :as users]
   [clojurenote.enml :as enml]
   [clojure.core.memoize :as memo]
   [clojure.edn :as edn]
   [clj-time.coerce :as coerce]
   [clj-time.format :as format]
   [hiccup-bridge.core :as hicv]))

(def credentials (clojure.core/read-string (slurp "resources/credentials.edn")))



;; need to plect from sandbox to production stuff
(def state-map {
   :dev {:notestore-url "https://sandbox.evernote.com/shard/s1/notestore"
         :access-token (credentials :sandbox)
         :notebook (credentials :sandbox-guid)}
   :prod {:notestore-url "https://www.evernote.com/shard/s97/notestore"
          :access-token (credentials :dev-token)
          :notebook (credentials :notebook-guid)}})

(defn tagguid->tagname [[guid] tags]
  (let [{:keys [name]} (first (filter #(= guid (% :guid)) tags))]
    name))

(defn get-notes [key]
  (let [ev-map (state-map key)
        notebook-guid (ev-map :notebook)
        user (select-keys (state-map key) [:notestore-url :access-token])
        tags (map bean (notes/get-all-tags-for-notebook user notebook-guid))
        filtered-tags (map #(select-keys % [:guid :name]) tags)
        notes (map bean (map #(notes/get-note user %)
                             (map #(.getGuid %) (notes/basic-notes-for-notebook user notebook-guid))))]
    (map-indexed (fn [idx {:keys [title created content tagGuids]}]
           (let [hiccon  (hicv/html->hiccup content)
                 enlrm (assoc-in (first hiccon) [0] :div)]
             {:topic (tagguid->tagname tagGuids filtered-tags)
              :created (coerce/from-long created)
              :title title
              :content enlrm
              :idx idx}))
         (map #(select-keys % [:title :created :content :tagGuids]) notes))))


(def note-cache-dev (memo/ttl #(get-notes :dev) :ttl/threshold 900000))
(def note-cache-prod (memo/ttl #(get-notes :prod) :ttl/threshold 900000))

(def topic-sort
  (let [topics (map #(select-keys % [:topic]) (note-cache-prod))
        topic-filt (flatten (distinct (map vals topics)))]
       (reduce
        (fn [reduce {:keys [topic] :as post}]
          (update-in reduce [topic] conj post))
        {} (map #(dissoc % :created) (note-cache-prod)))))






(defn post-from-title
    [title]
    (let [post (into {} (filter #(= title (:title %))  (note-cache-prod)))]
      post))



