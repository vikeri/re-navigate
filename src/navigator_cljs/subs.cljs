(ns navigator-cljs.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [register-sub subscribe]]))

(register-sub
  :get-greeting
  (fn [db _]
    (reaction
      (get @db :greeting))))

(register-sub
  :nav/index
  (fn [db _]
    (reaction
      (get-in @db [:nav :index]))))

(register-sub
  :nav/state
  (fn [db _]
    (reaction
      (get @db :nav))))

(register-sub
  :nav/current
  (fn [db _]
    (let [index (subscribe [:nav/index])]
      (reaction
        (get-in @db [:nav
                     :children
                     @index])))))