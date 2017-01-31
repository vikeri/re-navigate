(ns re-navigate.events
  (:require
   [re-frame.core :refer [reg-event-db after reg-event-fx]]
   [clojure.spec :as s]
   [re-navigate.db :as db :refer [app-db]]))

;; -- Interceptors ------------------------------------------------------------
;;
;; See https://github.com/Day8/re-frame/blob/master/docs/Interceptors.md
;;
(defn check-and-throw
  "Throw an exception if db doesn't have a valid spec."
  [spec db [event]]
  (when-not (s/valid? spec db)
    (let [explain-data (s/explain-data spec db)]
      (throw (ex-info (str "Spec check after " event " failed: " explain-data) explain-data)))))

(def validate-spec
  (if goog.DEBUG
    (after (partial check-and-throw ::db/app-db))
    []))

(defn dec-to-zero
  "Same as dec if not zero"
  [arg]
  (if (< 0 arg)
    (dec arg)
    arg))

;; -- Handlers --------------------------------------------------------------

(reg-event-db
 :initialize-db
 validate-spec
 (fn [_ _]
   app-db))

(reg-event-db
 :nav/navigate
 validate-spec
 (fn [db [_ value]]
   (-> db
       (update-in [:nav/state :nav.state/routes] #(conj % value))
       (update-in [:nav/state :nav.state/index] inc))))

(defn nav-val->route
  [nav-val]
  (let [route-name (.-routeName nav-val)
        params (.-params nav-val)]
    (merge #:nav.route{:routeName (keyword route-name)
                       :key       (->> route-name (str "scene_") keyword)}
           (if params {:nav.route/params params}))))

(reg-event-db
  :nav/back
  validate-spec
  (fn [db _]
    (-> db
        (update-in [:nav/state :nav.state/index] dec-to-zero)
        (update-in [:nav/state :nav.state/routes] pop))))

(reg-event-db
  :nav/reset
  validate-spec
  (fn [db _]
    (let [first-route (-> db :nav/state :nav.state/routes first)]
      (-> db
          (assoc-in [:nav/state :nav.state/routes] [first-route])
          (assoc-in [:nav/state :nav.state/index] 0)))))

(reg-event-fx
  :nav/js
  validate-spec
  (fn [{:keys [db]} [_ nav-val]]
    (js/console.log (js->clj nav-val))
    {:dispatch (case (.-type nav-val)
                 "Back" [:nav/back]
                 "Navigate" [:nav/navigate (nav-val->route nav-val)])
     :db       db}))
