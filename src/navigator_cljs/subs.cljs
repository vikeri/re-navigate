(ns navigator-cljs.subs
  (:require-macros [reagent.ratom :refer [reaction]])
  (:require [re-frame.core :refer [reg-sub-raw subscribe]]))

(reg-sub-raw
  :nav/index
  (fn [db _]
    (reaction
      (get-in @db [:nav :index]))))

(reg-sub-raw
  :nav/state
  (fn [db _]
    (reaction
      (get @db :nav))))
