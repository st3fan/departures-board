(ns departures-board.geo-test
  (:use
   clojure.test
   departures-board.index))

(def fifth-street {:position {:latitude 43.60023 :longitude -79.50257}
                   :radius 0.2
                   :stops []})

(def queen-and-spadina {:position {:latitude 43.648744 :longitude -79.396335}
                        :radius 0.2
                        :stops []})

(def dundas-square {:position {:latitude 43.656088 :longitude -79.380166}
                    :radius 0.2
                    :stops []})

(def college-and-spadina {:position {:latitude 43.657951 :longitude -79.400047}
                          :radius 0.2
                          :stops []})

(def queen-and-yonge {:position {:latitude 43.652373 :longitude -79.37919}
                      :radius 0.2
                      :stops []})

(deftest stops-near-fifth-street
  (testing "Stops near Fifth Street"
    (let [stops (transit.index/find-)])))
