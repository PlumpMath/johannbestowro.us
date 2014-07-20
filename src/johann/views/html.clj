(ns johann.views.html
  (:require [hiccup.page :as html]
            [hiccup.element :as element]
            ;;[johann.notes :as notes]
   ))

(defn head-boiler [title]
  [:head [:title title]
   [:meta {:name "viewport"
           :http-equiv "Content-type"
           :content "width=device-width, initial-scale=1.0"}]

   (html/include-js "//cdnjs.cloudflare.com/ajax/libs/fastclick/0.6.11/fastclick.min.js")
         (element/javascript-tag "window.addEventListener('load', function() {
                                 FastClick.attach(document.body);
                                 }, false);")]
  )

(defn venture [css]
  (html/html5
   (conj (head-boiler "be motivated to give me 50 dollars")
         [:style css]
         (html/include-js "//cdnjs.cloudflare.com/ajax/libs/react/0.9.0/react.min.js")
         (html/include-css "//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css")
         )
   [:body
    [:nav
     [:div.plugs
      [:a.fa.fa-twitter-square {:href "https://twitter.com/opinionsonline"}]
      #_(element/link-to  "follow me on twitter")
      [:a.fa.fa-youtube-square {:href "http://www.youtube.com/neurosisnow"}]
      #_(element/link-to "http://www.youtube.com/neurosisnow?sub_confirmation=1" "subscribe to Neurosis Now")]
     ]
    [:div#container.full]
    (html/include-js "http://fb.me/react-0.8.0.js")
    (html/include-js "/javascripts/carousel/out/goog/base.js")
    (html/include-js "/javascripts/carousel.js")
    (element/javascript-tag "goog.require('johann.carousel')")]))


(defn main [css]
  (html/html5

   (conj (head-boiler "be motivated to give me 50 dollars")
         [:style css]
         (html/include-js "//cdnjs.cloudflare.com/ajax/libs/react/0.9.0/react.min.js")
         (html/include-css "//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css")
         )
   [:body
    [:nav (element/link-to "/on" "essays")
     (element/link-to "/is/funny" "another link")
     ]
    [:div#venture ;;rename this
     (html/include-js "/javascripts/dream/dream.js")
     (element/javascript-tag "goog.require('johann.venture')")
     ]

    [:div.plugs
     [:a.fa.fa-twitter-square {:href "https://twitter.com/opinionsonline"}]
     [:a.fa.fa-youtube-square {:href "http://www.youtube.com/neurosisnow"}]

     ]
    ]
   )

  )

(defn dream [css edn]
  (html/html5
    (conj (head-boiler "dream dream dream")
          (html/include-css "//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css")
          [:style css])
   [:body.flex
    [:noscript "If you're seeing this then you're probably a search engine."]
    (html/include-js "/javascripts/web-animations.min.js")
    (html/include-js (str "/javascripts" (if (= (System/getenv "MODE") "DEV") "/dream/" "/") "dream.js"))
    [:div#app.full.flex "HEY"]
    ; Serialize app state so client can initialize without making an additional request.
    [:script#state {:type "application/edn"} (pr-str edn)]
    ; Initialize client and pass in IDs of the app HTML and app EDN elements.
    [:script {:type "text/javascript"} "dream.root.init('app', 'state')"]]))


(defn funny [css]
  (html/html5
   (conj (head-boiler "it's true")
         [:style css]
         )
   [:body
    [:div.greatPhoto {:style "background-size: cover"}
     [:content
      [:article "this is in progress"]]]]

   ))



(defn index [css]
  (html/html5
   (conj (head-boiler "things of that sort")

         (html/include-js "//cdnjs.cloudflare.com/ajax/libs/react/0.9.0/react.min.js")
         (html/include-css "//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css")

         [:style css]
         )
   [:body.full.flex
    [:div.flex.column.full
     [:div.bookend ""]
     [:div#container.full.flex


      ]


    (html/include-js "/javascripts/topics/out/goog/base.js")
    (html/include-js "/javascripts/topics.js")
    (element/javascript-tag "goog.require('johann.topics')")
    [:div.bookend ""]]
    ]
   ))

(defn essay [css post]
  (html/html5
   (conj (head-boiler "things of that sort")

         (html/include-js "//cdnjs.cloudflare.com/ajax/libs/react/0.9.0/react.min.js")
         (html/include-css "//netdna.bootstrapcdn.com/font-awesome/4.0.3/css/font-awesome.css")
         [:style css]
         )
   [:body.full.flex
    [:div.topics.flex.column.fixed
     [:div.bookend ""]


    [:div#container.full.flex


     ]



    [:div.bookend ""]]
    [:content.full.flex.column.scroll
     [:header.flex (:title post)]
     [:article (:content post)]]
    ]
    (html/include-js "/javascripts/topics/out/goog/base.js")
    (html/include-js "/javascripts/topics.js")
    (element/javascript-tag "goog.require('johann.topics')")
   ))


