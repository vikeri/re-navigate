(ns navigator-cljs.db
  (:require [schema.core :as s :include-macros true]))


(def NavigationRoute
  {:key s/Keyword
   :title s/Str})

(def NavigationState
 {:index s/Int
  :routes [NavigationRoute]
  :key s/Keyword})

;; schema of app-db
(def schema {:nav NavigationState})

;; initial state of app-db
(def app-db {:nav {:index    0
                   :key      :home
                   :routes [{:key :first-route
                             :title "First route"}]}})
