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
  (fn [db [_ [value route-name]]]
    (-> db
        (update-in [:nav/stack-state
                    (keyword "nav.routeName" route-name)
                    :nav.state/routes]
                   #(conj % value))
        (update-in [:nav/stack-state
                    (keyword "nav.routeName" route-name)
                    :nav.state/index]
                   inc))))

(defn nav-val->route
  [nav-val route-name]
  (let [route-name (.-routeName nav-val)
        params     (.-params nav-val)]
    [(merge #:nav.route{:routeName (keyword route-name)
                        :key       (->> route-name (str "scene_") keyword)}
            (if params {:nav.route/params params})) route-name]))

(defn tab-val->route
  [nav-val]
  (let [route-name (.-routeName nav-val)
        params     (.-params nav-val)]
    (merge #:nav.route{:routeName (keyword route-name)
                       :key       (->> route-name (str "scene_") keyword)}
           (if params {:nav.route/params params}))))

(reg-event-db
  :nav/back
  validate-spec
  (fn [db [_ route-name]]
    (let [route-key (keyword "nav.routeName" route-name)]
      (-> db
          (update-in [:nav/stack-state route-key :nav.state/index] dec-to-zero)
          (update-in [:nav/stack-state route-key :nav.state/routes] pop)))))

(reg-event-db
  :nav/reset
  validate-spec
  (fn [db [_ route-name]]
    (let [route-key (keyword "nav.routeName" route-name)
          first-route (-> db :nav/stack-state route-key :nav.state/routes first)]
      (-> db
          (assoc-in [:nav/stack-state route-key :nav.state/routes] [first-route])
          (assoc-in [:nav/stack-state route-key :nav.state/index] 0)))))

(defn position
  [pred coll]
  (first
    (keep-indexed (fn [idx x]
                    (when (pred x)
                      idx))
                  coll)))

(reg-event-db
  :nav/set-tab
  validate-spec
  (fn [db [_ tab]]
    (let [old-idx (get-in db [:nav/tab-state :nav.state/index])
          idx     (position #(do
                               (= tab (name (:nav.route/routeName %))))
                            (get-in db [:nav/tab-state :nav.state/routes]))]
      (js/console.log (js/Date.) "SETTING TAB " tab idx old-idx)
      (assoc-in db [:nav/tab-state :nav.state/index] idx))))

(reg-event-db
  :nav/set
  validate-spec
  (fn [db [_ nav]]
    (js/console.log "GOT NAV" nav)
    (assoc-in db [:nav/tab-state :nav.state/index] (.-index nav))))

(reg-event-fx
  :nav/js-tab
  validate-spec
  (fn [{:keys [db]} [_ tab-val]]
    (js/console.log "JS TAB NAV" (js->clj tab-val))
    {:dispatch (case (.-type tab-val)
                 "Back" [:nav/back]
                 "Navigate" [:nav/set #:nav.state{:index ()} (.-routeName tab-val)])
     :db       db}))


(reg-event-fx
  :nav/js
  validate-spec
  (fn [{:keys [db]} [_ [nav-val route-name]]]
    (js/console.log [nav-val route-name])
    (js/console.log "JS NAV" (js->clj nav-val))
    {:dispatch (case (.-type nav-val)
                 "Back" [:nav/back route-name]
                 "Navigate" [:nav/navigate (nav-val->route nav-val route-name)])
     :db       db}))
