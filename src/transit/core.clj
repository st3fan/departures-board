(ns transit.core
  (:use
   compojure.core
   ring.util.response
   [ring.middleware.format-response :only [wrap-restful-response]]
   ring.adapter.jetty)
  (:require
   [clojure.string :as string]
   [compojure.handler :as handler]
   [compojure.route :as route]
   [transit.nextbus :as nextbus])
  (:import
   java.lang.management.ManagementFactory
   java.lang.management.RuntimeMXBean))

;; Defaults

(def DEFAULT-RADIUS 0.125)
(def MAX-RADIUS 1.0)

;; Our database with stops, routes, directions

(defonce ttc (ref nil))

;; Helpers to parse request parameters

(defn position-from-params [params]
  {:latitude (-> params :latitude Double/parseDouble)
   :longitude (-> params :longitude Double/parseDouble)})

(defn radius-from-params [params]
  (if (contains? params :radius)
    (min (-> params :radius Double/parseDouble) MAX-RADIUS)
    DEFAULT-RADIUS))

(defn stops-from-params [params]
  (string/split (:stops params) #","))

;;

(defn wrap-dir-index [handler]
  (fn [req]
    (handler
     (update-in req [:uri] #(if (= "/" %) "/index.html" %)))))

;;

(defn init []
  (dosync
   (ref-set ttc (nextbus/load-agency "ttc"))))

;; The routes and app handler

(defroutes api-routes
  (GET "/api/status" []
       (let [rt (Runtime/getRuntime)]
         (response {:uptime (.getUptime (ManagementFactory/getRuntimeMXBean))
                    :memory {:total (.totalMemory rt)
                             :free (.freeMemory rt)
                             :max (.maxMemory rt)
                             :used (- (.totalMemory rt) (.freeMemory rt))}})))
  (GET "/api/stops" {params :params}
       (let [position (position-from-params params) radius (radius-from-params params)]
         (response {:stops (nextbus/find-stops @ttc position radius)
                    :position position})))
  (GET "/api/predictions-for-position" {params :params}
       (let [position (position-from-params params) radius (radius-from-params params)]
         (response {:predictions (nextbus/predictions @ttc (nextbus/find-stops @ttc position radius))
                    :position position})))
  (GET "/api/predictions-for-stops" {params :params}
       (let [stops (stops-from-params params)]
         (prn stops)
         (response {:predictions (nextbus/predictions @ttc stops)
                    :stops stops})))
  (route/resources "/")
  (route/not-found "Not found"))

(def app
  (-> (handler/api api-routes)
      (wrap-restful-response)
      (wrap-dir-index)))
