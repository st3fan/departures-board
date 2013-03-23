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
  [agency position]
  (map #(-> % :object :tag)
       (geodb/find-objects db position 0.2)))

(defn position-from-params [params]
  {:latitude (-> params :latitude Double/parseDouble)
   :longitude (-> params :longitude Double/parseDouble)})

;;

(defroutes api-routes
  (GET "/api/stops" {params :params}
       (let [position (position-from-params params)]
         (response {:stops (find-stops ttc position)
                    :position position})))
  (GET "/api/predictions" {params :params}
       (let [position (position-from-params params)]
         (response {:predictions (nextbus/predictions ttc (find-stops ttc position))
                    :position position})))
  (route/not-found "Not found"))

(def app
  (-> (handler/api api-routes)
      (wrap-restful-response)))
