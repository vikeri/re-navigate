(ns re-navigate.subs
  (:require [re-frame.core :refer [reg-sub]]))

(reg-sub
  :nav/state
  (fn [db _]
    (:nav/state db)))