(ns johann.utils.system
  (:require [johann.utils.config :refer [config]]
            [#+clj  com.stuartsierra.component
             #+cljs quile.component
             :as component :refer [system-map]]
            #+clj [tangrammer.component.co-dependency :as co-dependency]
            #+clj  [schema.core :as s]
            #+cljs [schema.core :as s :include-macros true]
            #+clj [milesian.identity :as identity]
            [ib5k.component.ctr :as ctr]
            [ib5k.component.using-schema :refer [system-using-schema]]
            [plumbing.core :refer [map-vals]]))

;; taken from https://github.com/milesian/BigBang/blob/master/src/milesian/bigbang.clj
(defn expand
  [system-map {:keys [before-start after-start]}]
  (let [on-start-sequence (apply conj before-start (cons [component/start] after-start))
        start (fn [c & args]
                (apply (->> on-start-sequence
                            (mapv (fn [[f & args]]
                                    #(apply f (conj args %))))
                            reverse
                            (apply comp))
                       (conj args c)))]
    (component/update-system system-map (keys system-map) start)))

(s/defn new-system
  [components :- {s/Keyword {:cmp s/Any
                             :using (s/either [s/Any]
                                              {s/Any s/Any})}}]
  (let [system (->> components
                    (map-vals :cmp)
                    (apply concat)
                    (apply system-map))
        using (->> components
                   (map-vals :using)
                   (remove (comp nil? second))
                   (into {}))]
    (system-using-schema system using)))

#+clj
(defn start [system]
  (let [system-atom (atom system)]
    (expand system {:before-start [[identity/add-meta-key system]
                                   [co-dependency/assoc-co-dependencies system-atom]
                                   [ctr/validate-class]]
                    :after-start [[co-dependency/update-atom-system system-atom]
                                  [ctr/validate-class]]})))

#+cljs
(defn start [system]
  (expand system {:before-start [[ctr/validate-class]]
                  :after-start [[ctr/validate-class]]}))
