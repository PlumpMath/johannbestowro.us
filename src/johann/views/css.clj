(ns johann.views.css
  (:require
            [garden.core :as garden]
            [garden.stylesheet :as ss]
            [garden.color :as color]
            [garden.def :as def]

   ))



(def/defkeyframes fade
  [:from {:opacity ".01"}]


  [:to {:opacity "1"}])

(def/defkeyframes run-in
  [:from {:transform "translateY(-20%)"}]


  [:to {:transform "translateY(0)"}])


(defn translator [x y z xdeg ydeg zdeg]

 {:transform (str " translate3D(" (or x 0) "em," (or y 0) "em, "  (or z 0) "em) "
                  "rotatex(" (or xdeg 0) "deg) "

                              "rotatey(" (or ydeg 0) "deg) "

                              "rotatez(" (or zdeg 0) "deg) "
                              )
 })




(defn size
  ([height width]
     {:width width :height height})
  ([size]
   {:width size :height size}))

(defn flex-box
  ([align justify flow]
    {
     :display #{:flex :-webkit-flex}
     :align-items align
     :-webkit-align-items align
     :justify-content justify
     :-webkit-justify-content justify
     :flex-flow flow
     :-webkit-flex-flow flow})
  ([align flow]
    {
     :display #{:flex :-webkit-flex}
     :align-items align
     :-webkit-align-items align
     :justify-content align
     :-webkit-justify-content align
     :flex-flow flow})
  ([align]
   {
    :display #{:flex :-webkit-flex}
    :align-items align
    :-webkit-align-items align
    :justify-content align
    :-webkit-justify-content align
    }
   ))


(def dream
  (garden/css {:vendors ["webkit" "moz" "o" "ms"]}
              fade

              ;card flip stuff
              [:.container ^:prefix {:width "260px"
                            :height "200px"
                            :position "relative"
                            :perspective "1000px"}]

              [:#card ^:prefix {:position "absolute"
                                :transform-style "preserve-3d"
                                :transform-origin "right center"
                                }]

              [:.bfv {:backface-visibility "hidden"}]
              [:.neutral ^:prefix {:transform "translate3d(0, 0, 0)"}]

              [:.figure ^:prefix {:display "block" :position "absolute" :backface-visibility "hidden"}]
              [:.flipped ^:prefix {:transform "translateX( -100% ) rotateY( -180deg )"}]
              ;

              [:html (size "100%")]
              [:body  (conj (size "100%")
                            {:font-family "arial"
                             :margin "0px"})]
              [:.selected ^:prefix {:background-color "#FFFFFF"
                                    :transition "all 0.4s ease-out"
                                    :font-size "4.5vh"
                                    }]
              [:.perspect ^:prefix {:perspective "1000"}]
              [:.selected2 {:background-color "#7A859C" }]
              [:.block  {:display "inline-block"
                         :transition "all 0.4s ease-out"
                         }]
              [:.trans {:transition "all 0.4s ease-in-out"}]
              [:.well-named ^:prefix {:transform "translate(15%, 0)"
                                      :visbility "hidden"
                                      }]
              [:.flex ^:prefix (flex-box "center")]
              [:.column ^:prefix {:flex-flow "column"}]
              [:.full (size "100%")]
              [:.hover [:&:hover {:background-color "#97ECFF"}]]
              [:.back ^:prefix {:transform "rotateY(180deg)"

                                }]
              [:.content ^:prefix {:transform-style "preserve-3d"
                                   :backface-visibility "hidden"
                                   }]
             
              [:.flex-start (flex-box "center" "flex-start" "row")]
              [:.boogey ^:prefix {:transform "translateY(-20%)"}]
              [:.dropDown ^:prefix {:transform "translateY(-50%)"
                                    :opacity "0"}]
              [:.tilt ^:prefix {:transform "rotate(-45deg)"}]

              [:.fade ^:prefix {:animation "fade 2s ease" }]
              [:.run-in ^:prefix {:animation "run-in .8s ease" }]
              [:.socials {:font-size "8.5vh"
                          :text-decoration "none"}]
              [:ul {:list-style "none"
                    :padding 0}]
              [:.hide ^:prefix {:transform "translate3d(-100%, 0, 0)"}] 
              ))














