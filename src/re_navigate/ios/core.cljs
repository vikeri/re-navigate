(ns re-navigate.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-navigate.events]
            [re-navigate.subs]))

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def stack-navigator (.-StackNavigator (js/require "react-navigation")))
(def tab-navigator (.-TabNavigator (js/require "react-navigation")))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))


(defn resd []
  [view [text "Hej"] [text "Hej"]])

(defn app-root [{:keys [navigation]}]
  (js/console.log (.-navigate navigation))
  (let [navigate (.-navigate navigation)]
    [view {:style {:flex-direction "column" :margin 40 :align-items "center"}}
     [text {:style {:font-size 30 :font-weight "100" :margin-bottom 20 :text-align "center"}} "Hejsan"]
     [image {:source logo-img
             :style  {:width 80 :height 80 :margin-bottom 30}}]
     [touchable-highlight {:style    {:background-color "#999" :padding 10 :border-radius 5}
                           :on-press #(navigate "Chat" #js{:user "Lucy"})}
      [text {:style {:color "white" :text-align "center" :font-weight "bold"}} "press me"]]]))


(def app-root-comp (let [comp (r/reactify-component app-root)]
                     (aset comp "navigationOptions" #js {"title" "Welcome Man"})
                     comp))



(def start (r/adapt-react-class (stack-navigator (clj->js {:Home {:screen app-root-comp}
                                                           :Chat {:screen (r/reactify-component resd)}}))))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "ReNavigate" #(r/reactify-component start)))
