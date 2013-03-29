(ns transit.nextbus-test
  (:use
    clojure.test
    transit.nextbus)
  (:require
   [transit.nextbus :as nextbus]))

(deftest test-load-agency
  (binding [nextbus/NEXTBUS-DATA-DIR "test/transit/nextbus-data"]
    (testing "Load the (prepared) nextbus data"
      (let [ttc (nextbus/load-agency "ttc")]
        (is (not (nil? ttc)))
        (is (= 2 (count (:routes ttc))))
        (is (= 237 (count (:stops ttc))))))))

(deftest test-find-stops
  (binding [nextbus/NEXTBUS-DATA-DIR "test/transit/nextbus-data"]
    (testing "Find stops near first fifth st"
      (let [ttc (nextbus/load-agency "ttc") fifth-street {:latitude 43.60023 :longitude -79.50257}]
        (is (= 2 (count (nextbus/find-stops ttc fifth-street 0.2))))
        (is (= 6 (count (nextbus/find-stops ttc fifth-street 0.25))))))))
