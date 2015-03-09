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
                 [om-websocket "0.1.1"]
                 [hiccup "1.0.5"]
                 [fogus/ring-edn "0.2.0"]
                 [hiccup-bridge "1.0.0-SNAPSHOT"]
                 [hiccups "0.3.0"]
                 

                 ;; component stuff
                 [com.stuartsierra/component "0.2.2"]
                 [juxt.modular/maker "0.5.0"]
                 [juxt.modular/wire-up "0.5.0"]
                 [juxt.modular/template "0.6.0"]
                 
                 ]
  :plugins [
            [lein-cljsbuild "1.0.3"]
            ]
  :main johann.handler

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
