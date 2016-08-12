(ns navigator-cljs.db
  (:require [cljs.spec :as s]))

(s/def ::index integer?)
(s/def ::key keyword?)
(s/def ::title string?)

(s/def ::route (s/keys :req-un [::key
                                ::title]))
(s/def ::routes (s/* ::route))

(s/def ::nav (s/keys :req-un [::index
                              ::key
                              ::routes]))

(s/def ::app-db (s/keys :req-un [::nav]))

;; initial state of app-db
(def app-db {:nav {:index    0
                   :key      :home
                   :routes [{:key :first-route
                             :title "First route"}]}})
