(ns re-navigate.db
  (:require [clojure.spec :as s]))

;; spec of app-db
;; Fetched from: https://github.com/react-community/react-navigation/blob/c37ad8a0a924d13f3897bc72fbda52aac76904b6/src/TypeDefinition.js
(s/def :nav.route/key keyword?)
(s/def :nav.route/routeName keyword?)
(s/def :nav.route/path keyword?)
(s/def :nav.route/param (s/or :str string? :num number?))
(s/def :nav.route/params (s/map-of keyword? :nav.route/param))
(s/def :nav/route (s/keys :req [:nav.route/key :nav.route/routeName]
                          :opt [:nav.route/path :nav.route/params]))
(s/def :nav.state/routes (s/coll-of :nav/route :kind vector?))
(s/def :nav.state/index integer?)
(s/def :nav/state (s/keys :req [:nav.state/index :nav.state/routes]))
(s/def ::app-db
  (s/keys :req [:nav/state]))

;; initial state of app-db
(def app-db {:nav/state #:nav.state{:index 0
                                    :routes [#:nav.route{:key :Init :routeName :Home}]}})
