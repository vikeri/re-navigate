(ns navigator-cljs.subs
  (:require [re-frame.core :refer [reg-sub subscribe]]))

(reg-sub
  :nav/index
  (fn [db _]
    (get-in db [:nav :index])))

(reg-sub
  :nav/state
  (fn [db _]
    (get db :nav)))
