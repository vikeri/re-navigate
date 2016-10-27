(ns navigator-cljs.handlers
  (:require
    [re-frame.core :refer [reg-event-db after]]
    [cljs.spec :as s]
    [navigator-cljs.db :as db :refer [app-db]]))

;; -- Helpers --------------------------------------------------------------

(defn dec-to-zero
  "Same as dec if not zero"
  [arg]
  (if (< 0 arg)
    (dec arg)
    arg))

(defn current-route-key
  "Gets the current route key"
  [db]
  (let [idx (get-in db [:nav :index])]
    (get-in db [:nav :routes idx :key])))

;; -- Middleware ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/wiki/Using-Handler-Middleware
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check failed: " explain-data) explain-data)))))

(def validate-spec-mw
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
  :initialize-db
  validate-spec-mw
  (fn [_ _]
    app-db))

(reg-event-db
  :nav/push
  validate-spec-mw
  (fn [db [_ value]]
    (if-not (= (current-route-key db) (:key value))
      (-> db
          (update-in [:nav :index] inc)
          (update-in [:nav :routes] #(conj % value)))
      db)))

(reg-event-db
  :nav/pop
  validate-spec-mw
  (fn [db [_ _]]
    (-> db
        (update-in [:nav :index] dec-to-zero)
        (update-in [:nav :routes] pop))))

(reg-event-db
  :nav/home
  validate-spec-mw
  (fn [db [_ _]]
    (-> db
        (assoc-in [:nav :index] 0)
        (assoc-in [:nav :routes] (vector (get-in db [:nav :routes 0]))))))
