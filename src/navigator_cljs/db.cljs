(ns navigator-cljs.db
  (:require [schema.core :as s :include-macros true]))


(def NavigationState
  {:key s/Keyword
   :title s/Str
   s/Keyword s/Any})

(def NavigationParentState
  (dissoc
    (merge NavigationState
           {:index    s/Int
            :children [NavigationState]})
    :title))

;; schema of app-db
(def schema {:nav NavigationParentState})
;; initial state of app-db
(def app-db {:nav {:index    0
                   :key      :home
                   :children [{:key :first-route
                               :title "First route"}]}})
