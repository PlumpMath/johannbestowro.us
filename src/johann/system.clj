(ns johann.system
  "Components and their dependency relationships"
  (:refer-clojure :exclude (read))
  (:require
   [clojure.java.io :as io]
   [me.raynes.fs :as fs]
   [clojure.string :as str]
   [clojure.tools.logging :as log]
   [clojure.tools.reader :refer (read)]
   [clojure.tools.reader.reader-types :refer (indexing-push-back-reader)]
   [clojure.core.async :refer (chan close!)]
   [com.stuartsierra.component :refer (Lifecycle system-map system-using using)]
   [tangrammer.component.co-dependency :refer (co-using)]
   [taoensso.encore :refer (merge-deep)]
   [modular.maker :refer (make)]
   [modular.bidi :refer (new-router new-static-resource-service)]
   [modular.ring :refer (new-web-request-handler-head)]
   [modular.http-kit :refer (new-webserver)]
   #_[cmpclimb.components.cljs-render :refer (new-browser-cljs-renderer)]
   #_[cmpclimb.components.html :refer (new-cljs-app)]
   [johann.website :refer (new-website new-middleware new-static-file-service)]))

;; ========== Config ==========

(defn ^:private read-file
  [f]
  (read
   ;; This indexing-push-back-reader gives better information if the
   ;; file is misconfigured.
   (indexing-push-back-reader
    (java.io.PushbackReader. (io/reader f)))))

(defn ^:private config-from
  [f]
  (if (.exists f)
    (read-file f)
    {}))

(defn ^:private user-config
  []
  (config-from (io/file (System/getProperty "user.home") ".cmpclimb.edn")))

(defn ^:private config-from-classpath
  []
  (if-let [res (io/resource "./cmpclimb.edn")]
    (config-from (io/file res))
    {}))

(defn config
  "Return a map of the static configuration used in the component
  constructors."
  []
  (merge-deep (config-from-classpath)
              (user-config)))

(defn root-path [config path]
  (str (:root config) path))

;; ========== Components ==========

(extend-type clojure.core.async.impl.channels.ManyToManyChannel
  Lifecycle
  (start [this]
    this)
  (stop [this]
    (close! this)))

(defn http-listener-components [system config]
  (assoc system
    :http-listener (-> (make new-webserver config
                             :port 3000)
                       (using [:request-handler]))
    :webhead (make new-web-request-handler-head config)
    :middleware (make new-middleware config)))

(defn modular-bidi-router-components [system config]
  (assoc system
    :webrouter (-> (make new-router config)
                   (using [:website :public-resources]))))

(defn website-components [system config]
  (assoc system
    :website (make new-website config
                   :ip "0.0.0.0"
                   :port 3000
                   {:transition-duration [:instagram :transition-duration]} 2000)))

(defn public-resources-components [system config]
  (assoc system
    :public-resources
    (-> (make new-static-resource-service config
              :uri-context ""
              :resource-prefix "public")
        (using []))))

#_(defn cljs-components [system config]
  (assoc system
    :cljs-app (make new-cljs-app config
                    :title "Ipad Mosaic"
                    :environment :development
                    :stylesheets ["/css/bootstrap.min.css"
                                  "/css/style.css"]
                    :js ["/js/react-0.12.2.js"]
                    :main "ipad_mosaic.web.main")
    :cljs-renderer (make new-browser-cljs-renderer config)))




;; ========== System ==========

(defn new-system-map
  [config]
  (apply system-map
         (apply concat
                (-> {}
                    (http-listener-components config)
                    (modular-bidi-router-components config)
                    (website-components config)
                    (public-resources-components config)
                    #_(cljs-components config)))))

(defn new-dependency-map
  []
  {;;:cljs-app {:cljs-renderer :cljs-renderer}
   :http-listener {:request-handler :webhead}
   
   :webhead {:request-handler :webrouter
             :middleware :middleware}
   :webrouter {:website :website
               :public-resources :public-resources}})

(defn new-co-dependency-map
  []
  {})

(defn new-production-system
  "Create the production system"
  []
  (-> (new-system-map (config))
      (system-using (new-dependency-map))))
