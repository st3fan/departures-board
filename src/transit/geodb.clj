(ns transit.geodb
  (:require
   [transit.geo :as geo]))

;; This is a very simplistic geo database. Right now it just does a linear scan
;; of objects. In the future I would like to create a real spatial index.

(defn make-db
  "Create a new geo database"
  [grid-size]
  (ref []))

(defn add-object
  "Add an object to the index with a specified position and value"
  [db object]
  (dosync
   (alter db conj {:position {:latitude (:latitude object) :longitude (:longitude object)}
                   :object object})))

(defn find-objects
  "Find objects at a specific position and radius"
  [db position radius]
  (dosync
   (for [record @db :let [distance (geo/haversine-distance position (:position record))] :when (<= distance radius)]
     {:position (:position record) :object (:object record) :distance distance})))
