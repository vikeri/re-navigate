(ns navigator-cljs.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [navigator-cljs.handlers]
            [navigator-cljs.subs]))

(set! js/window.React (js/require "react-native"))

(def app-registry (.-AppRegistry js/React))
(def text (r/adapt-react-class (.-Text js/React)))
(def view (r/adapt-react-class (.-View js/React)))
(def image (r/adapt-react-class (.-Image js/React)))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight js/React)))
(def card-stack (r/adapt-react-class (.-CardStack (.-NavigationExperimental js/React))))

(.log js/console card-stack)

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
  (.alert (.-Alert js/React) title))

(defn scene [_]
  (r/reactify-component
    [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
     [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "Hello"]
     [image {:source logo-img
             :style  {:width 80 :height 80 :margin-bottom 30}}]
     [touchable-highlight {:style    {:background-color "#999" :padding 10 :border-radius 5}
                           :on-press #(alert "HELLO!")}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]]))

(defn app-root []
  (let [greeting (subscribe [:get-greeting])]
    (fn []
      [card-stack {:on-navigate      #(.alert (.-Alert js/React) "nav")
                   :navigation-state {:index    0
                                      :key      :home
                                      :children [{:key :first-route}]}
                   :render-scene     scene}])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "NavigatorCljs" #(r/reactify-component app-root)))
