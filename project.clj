(defproject johann "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :dependencies [[org.clojure/clojure "1.7.0-alpha5"]
                 [compojure "1.1.6"]
                 [clojurenote "0.4.0"]
                 [ring "1.1.8"]
                 [clj-time "0.6.0"]
                 [org.clojure/core.memoize "0.5.6"]
                 [org.clojure/core.async "0.1.298.0-2a82a1-alpha"]
                 [prismatic/om-tools "0.2.2"]
                 [http-kit "2.1.13"]

                 [org.clojure/clojurescript "0.0-2227"]
                 [garden "1.1.5"]
                 [om "0.6.4"]
                 [om-websocket "0.1.1" :exclusions [org.meclojure/clojure]]
                 [hiccup "1.0.5"]
                 [fogus/ring-edn "0.2.0"]
                 [hiccup-bridge "1.0.0-SNAPSHOT"]
                 [hiccups "0.3.0"]

                 ;; logging
                 [ch.qos.logback/logback-classic "1.0.7" :exclusions [org.slf4j/slf4j-api]]
                 [com.taoensso/timbre "3.3.1"]
                 [org.slf4j/jcl-over-slf4j "1.7.2"]
                 [org.slf4j/jul-to-slf4j "1.7.2"]
                 [org.slf4j/log4j-over-slf4j "1.7.2"]
                 [org.clojure/tools.logging "0.3.1"]

                 [me.raynes/fs "1.4.6"]

                 ;; component stuff
                 [com.stuartsierra/component "0.2.2"]
                 [juxt.modular/maker "0.5.0"]
                 [juxt.modular/wire-up "0.5.0"]
                 [juxt.modular/template "0.6.0"]
                 [tangrammer/co-dependency "0.1.5"]
                 [juxt.modular/bidi "0.5.4"]
                 [juxt.modular/http-kit "0.5.1"]
                 [quile/component-cljs "0.2.2"]

                 [ring/ring-defaults "0.1.4"]
                 
                 ]
  :plugins [
            [lein-cljsbuild "1.0.3"]
            ]
  :main johann.handler
  :source-paths ["src" "dev"]
  :cljsbuild {:builds
              [
               {:id "dream"
                :source-paths ["src/cljs/dream"]
                :compiler{


                          :preamble ["react/react.js"]
                          :output-to "resources/public/javascripts/dream/dream.js"
                          :output-dir "resources/public/javascripts/dream/"
                          :source-map "resources/public/javascripts/dream/dream.js.map"
                          :optimizations :whitespace


                          }}

               {:id "dreamrelease"
                :source-paths ["src/cljs/dream"]
                :compiler{

                          :preamble ["react/react.min.js"
                                        ;"resources/public/javascripts/web-animations.js"

                                     ]
                          :externs   [
                                        ;"resources/public/javascripts/web-animations.js"
                                      "react/externs/react.js"
                                      ]
                          :libs ["resources/public/javascripts/web-animations.js"]

                          :output-to "resources/public/javascripts/dream.js"
                          :output-dir "resources/public/javascripts/"
                          :source-map "resources/public/javascripts/dream.js.map"
                          :optimizations :advanced
                          :pretty-print false


                          }}



               {:id "devmenu"
                :source-paths ["src/cljs/menu"]
                :compiler{
                          :output-to "resources/public/javascripts/menu.js"
                          :output-dir "resources/public/javascripts/menu/out"
                          :source-map true
                          :optimizations :none


                          }}

               {:id "devcarousel"
                :source-paths ["src/cljs/carousel"]
                :compiler{
                          :output-to "resources/public/javascripts/carousel.js"
                          :output-dir "resources/public/javascripts/carousel/out"
                          :source-map true
                          :optimizations :none


                          }}

               {:id "devtop"
                :source-paths ["src/cljs/topics"]
                :compiler{
                          :output-to "resources/public/javascripts/topics.js"
                          :output-dir "resources/public/javascripts/topics/out"
                          :source-map true
                          :optimizations :none


                          }}

               {:id "topics"
                :source-paths ["src/cljs/topics"]
                :compiler{
                          :output-to "resources/public/javascripts/topics.js"
                          :optimizations :advanced
                          :pretty-print false
                          :externs ["react/externs/react.js"]


                          }}

               {:id "cube"
                :source-paths ["src/cljs/cube"]
                :compiler{

                          :preamble ["react/react.min.js"]
                          :output-to "resources/public/javascripts/cube/cube.js"
                          :output-dir "resources/public/javascripts/cube/out"
                          :optimizations :whitespace


                          }}

               {:id "cuberelease"
                :source-paths ["src/cljs/cube"]
                :compiler{
                          :output-to "resources/public/javascripts/cube.js"
                          :optimizations :advanced
                          :pretty-print false
                          :externs ["react/externs/react.js"]


                          }}

               {:id "releasemenu"
                :source-paths ["src/cljs/menu"]
                :compiler {
                           :output-to "resources/public/javascripts/menu.js"
                           :optimizations :advanced
                           :pretty-print false
                           :externs ["react/externs/react.js"]

                           }}
               ]




              }

  )
