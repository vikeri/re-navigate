(ns re-navigate.ios.core
  (:require [reagent.core :as r :refer [atom]]
            [re-frame.core :refer [subscribe dispatch dispatch-sync]]
            [re-navigate.events]
            [clojure.data :as d]
            [re-navigate.subs]))
(js* "/* @flow */")

(def ReactNative (js/require "react-native"))

(def app-registry (.-AppRegistry ReactNative))
(def text (r/adapt-react-class (.-Text ReactNative)))
(def view (r/adapt-react-class (.-View ReactNative)))
(def image (r/adapt-react-class (.-Image ReactNative)))
(def react-navigation (js/require "react-navigation"))
(def add-navigation-helpers (.-addNavigationHelpers react-navigation))
(def stack-navigator (.-StackNavigator react-navigation))
(def tab-navigator (.-TabNavigator react-navigation))
(def touchable-highlight (r/adapt-react-class (.-TouchableHighlight ReactNative)))

(def logo-img (js/require "./images/cljs.png"))

(defn random-color
  []
  (js* "'#'+('00000'+(Math.random()*(1<<24)|0).toString(16)).slice(-6)"))

(defn alert [title]
  (.alert (.-Alert ReactNative) title))

(def style
  {
   :title       {:font-size   30
                 :font-weight "100"
                 :margin      20
                 :text-align  "center"}
   :button      {:background-color "#999"
                 :padding          10
                 :margin-bottom    20
                 :border-radius    5}
   :button-text {:color       "white"
                 :text-align  "center"
                 :font-weight "bold"}
   })

(defn resd [props]
  (let [number (-> props (get "params") (get "number"))]
    [view {:style {:align-items      "center"
                   :justify-content  "center"
                   :flex             1
                   :background-color (random-color)}}
     [view {:style {:background-color "rgba(256,256,256,0.5)"
                    :margin-bottom    20}}
      [text {:style (style :title)} "Card number " number]]
     [touchable-highlight
      {:style    (style :button)
       :on-press #(dispatch
                    [:nav/navigate
                     #:nav.route {:key       (keyword (str number))
                                  :routeName :Card
                                  :params    {:number (inc number)}}])}
      [text {:style (style :button-text)} "Next"]]
     [touchable-highlight {:on-press #(dispatch [:nav/reset])
                           :style    (style :button)}
      [text {:style (style :button-text)} "RESET"]]]))

(defn app-root [{:keys [navigation]}]
  [view {:style {:flex-direction   "column"
                 :flex             1
                 :padding          40
                 :align-items      "center"
                 :background-color (random-color)}}
   [text {:style (style :title)} "Hejsan"]
   [image {:source logo-img
           :style  {:width 80 :height 80 :margin-bottom 30}}]
   [touchable-highlight {:style    (style :button)
                         :on-press #(dispatch
                                      [:nav/navigate
                                       #:nav.route {:key       :0
                                                    :routeName :Card
                                                    :params    {:number 1}}])}
    [text {:style (style :button-text)} "press me"]]])


(defn nav-wrapper [component title]
  (let [comp (r/reactify-component
               (fn [{:keys [navigation]}]
                 [component (-> navigation .-state js->clj)]))]
    (aset comp "navigationOptions" #js {"title" title})
    comp))


(def resd-comp (nav-wrapper resd #(str "Card "
                                       (aget % "state" "params" "number"))))

(def app-root-comp (nav-wrapper app-root "Welcome"))

(def router {:Home {:screen app-root-comp}
             :Card {:screen resd-comp}})

(def sn (r/adapt-react-class (stack-navigator (clj->js router))))

(defn start [] (let [nav-state (subscribe [:nav/state])]
                 (fn []
                   [sn {:navigation (add-navigation-helpers
                                      (clj->js
                                        {"dispatch" #(dispatch [:nav/js %])
                                         "state"    (clj->js @nav-state)}))}])))

(defn init []
  (dispatch-sync [:initialize-db])
  (.registerComponent app-registry "ReNavigate" #(r/reactify-component start)))
