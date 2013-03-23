(ns transit.geo)

(def earth-radius 6371.0)

(defn haversine-distance
  [p1 p2]
  "Calculate the Haversine distance between p1 and p2"
  (let [{lat1 :latitude lon1 :longitude} p1 {lat2 :latitude lon2 :longitude} p2]
    (let [dlat (Math/toRadians (- lat2 lat1))
          dlon (Math/toRadians (- lon2 lon1))
             a (+ (* (Math/sin (/ dlat 2))
                     (Math/sin (/ dlat 2)))
                  (* (Math/cos (Math/toRadians lat1))
                     (Math/cos (Math/toRadians lat2))
                     (Math/sin (/ dlon 2))
                     (Math/sin (/ dlon 2))))
             c (* 2 (Math/atan2 (Math/sqrt a) (Math/sqrt (- 1 a))))]
      (* earth-radius c))))
