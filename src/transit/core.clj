(ns transit.core
  (:use
   compojure.core
   ring.util.response
   [ring.middleware.format-response :only [wrap-restful-response]]
   ring.adapter.jetty)
  (:require
   [compojure.handler :as handler]
   [compojure.route :as route]
   [transit.nextbus :as nextbus]
   [transit.geodb :as geodb]))

;;

(def DEFAULT-RADIUS 0.125)
(def MAX-RADIUS 1.0)

;;

(defn index-agency
  "Index an agency into geo db"
  [agency db]
  (doseq [stop (vals (:stops agency))]
    (geodb/add-object db stop)))

;;

(defonce ttc (nextbus/load-agency "ttc"))
(defonce db (geodb/make-db 0.1))

(defn init []
  (index-agency ttc db))

;;

(defn find-stops
  [agency position radius]
  (map #(-> % :object :tag)
       (geodb/find-objects db position radius)))

(defn position-from-params [params]
  {:latitude (-> params :latitude Double/parseDouble)
   :longitude (-> params :longitude Double/parseDouble)})

(defn radius-from-params [params]
  (if (contains? params :radius)
    (min (-> params :radius Double/parseDouble) MAX-RADIUS)
    DEFAULT-RADIUS))

;;

(defroutes api-routes
  (GET "/api/stops" {params :params}
       (let [position (position-from-params params) radius (radius-from-params params)]
         (response {:stops (find-stops ttc position radius)
                    :position position})))
  (GET "/api/predictions" {params :params}
       (let [position (position-from-params params) radius (radius-from-params params)]
         (response {:predictions (nextbus/predictions ttc (find-stops ttc position radius))
                    :position position})))
  (route/resources "/")
  (route/not-found "Not found"))

(def app
  (-> (handler/api api-routes)
      (wrap-restful-response)))
