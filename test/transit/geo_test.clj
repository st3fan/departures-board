(ns transit.geo-test
  (:use
   clojure.test
   transit.geo))

(deftest haversine-test
  (testing "Distance between Warsaw and Amsterdam"
    (let [warsaw {:latitude 52.23 :longitude 21.010833}
          amsterdam {:latitude 52.373056, :longitude 4.892222}
          distance 1093.8527983603317]
      (is (= distance (haversine-distance warsaw amsterdam)))
      (is (= distance (haversine-distance amsterdam warsaw))))))

(deftest haversine-test-same-points
  (testing "Distance between the same points should be zero"
    (let [warsaw {:latitude 52.23 :longitude 21.010833}]
      (is (= 0.0 (haversine-distance warsaw warsaw)))
      (is (= 0.0 (haversine-distance warsaw warsaw))))))
