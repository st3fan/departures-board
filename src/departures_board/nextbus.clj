(ns departures-board.nextbus
  (:use
   clojure.xml
   [clj-xpath.core :only [$x $x:tag $x:text $x:attrs $x:attrs* $x:node]])
  (:require
   [clj-http.client :as http]
   [me.raynes.fs :as fs]
   [departures-board.geodb :as geodb]))

(def ^:dynamic NEXTBUS-API "http://webservices.umoiq.com/service/publicXMLFeed")
(def ^:dynamic NEXTBUS-DATA-DIR "~/.departures-board")
(def GEODB-GRID-SIZE "0.1")

;; Indexing code

(defn index-agency
  "Index an agency into geo db"
  [agency db]
  (doseq [stop (vals (:stops agency))]
    (geodb/add-object db stop)))

;; Loading and parsing of NextBus data

(defn- load-route-list-xml
  [agency-tag]
  (let [path (fs/expand-home (str NEXTBUS-DATA-DIR "/" agency-tag "-routes.xml"))]
    (if (fs/exists? path)
      (do
        (println "Loading route list from" path)
        (slurp path))
      (do
        (println "Loading route list from api.nextbus.com for " agency-tag)
        (let [response (http/get NEXTBUS-API {:query-params {:command "routeList" :a agency-tag}})]
         (if (= 200 (:status response))
           (let [body (:body response)]
             (spit path body)
             body)))))))

(defn- load-route-config-xml
  [agency-tag route-tag]
  (let [path (fs/expand-home (str NEXTBUS-DATA-DIR "/" agency-tag "-" route-tag ".xml"))]
    (if (fs/exists? path)
      (do
        (println "Loading route config for" route-tag "from" path)
        (slurp path))
      (do
        (println "Loading route config for" route-tag "from api.nextbus.com")
        (let [response (http/get NEXTBUS-API {:query-params {:command "routeConfig" :a agency-tag :r route-tag}})]
         (if (= 200 (:status response))
           (let [body (:body response)]
             (spit path body)
             body)))))))

(defn- parse-route-stops
  "Parse a route config and return a map containing all the stops by tag"
  [route-config-xml]
  (zipmap
   (map #(-> % :attrs :tag)
        ($x "/body/route/stop" route-config-xml))
   (map #(hash-map :tag (-> % :attrs :tag)
                   :title (-> % :attrs :title)
                   :longitude (-> % :attrs :lon Double/parseDouble)
                   :latitude (-> % :attrs :lat Double/parseDouble))
        ($x "/body/route/stop" route-config-xml))))

(defn- parse-route
  "Parse a route config and return a map containing route tag, title and a list of stop tags for the route"
  [route-config-xml]
  (let [route  (first ($x "/body/route" route-config-xml))]
    (let [stops ($x "stop" (:node route))]
      {:tag (-> route :attrs :tag)
       :title (-> route :attrs :title)
       :stop-tags (vec (map #(-> % :attrs :tag) stops))})))

(defn- load-predictions-xml
  [agency-tag multi-stops]
  (let [response (http/get NEXTBUS-API {:query-params {:command "predictionsForMultiStops" :a agency-tag :stops (seq multi-stops)}})]
    (if (= 200 (:status response))
      (:body response))))

;; <prediction epochTime="1363994509521" seconds="544" minutes="9" isDeparture="false" branch="501"
;;     dirTag="501_1_501" vehicle="4202" block="501_8_80" tripTag="19387615" />

(defn parse-predictions
  [direction-node]
  (map #(hash-map :seconds (-> % :attrs :seconds Integer/parseInt) :minutes (-> % :attrs :minutes Integer/parseInt))
       ($x "prediction" direction-node)))

;; <direction title="East - 501 Queen towards Neville Park">

(defn parse-directions
  [predictions-node]
  (map #(hash-map :title (-> % :attrs :title) :predictions (parse-predictions (:node %)))
       ($x "direction" predictions-node)))

;; <predictions agencyTitle= "Toronto Transit Commission" routeTitle= "501-Queen" routeTag= "501"
;;     stopTitle= "Lake Shore Blvd West At Fifth St" stopTag= "4223">

(defn parse-predictions-xml
  [agency predictions-xml]
  (loop [predictions []  xml-predictions ($x "/body/predictions" predictions-xml)]
    (if (empty? xml-predictions)
      predictions
      (let [p (first xml-predictions)]
        (recur
         (conj predictions {:route {:title (-> p :attrs :routeTitle) :tag (-> p :attrs :routeTag)}
                            :stop {:title (-> p :attrs :stopTitle) :tag (-> p :attrs :stopTag)}
                            :directions (parse-directions (:node p))})
         (rest xml-predictions))))))

(defn routes-for-stop
  "Return the route tags for the specified stop"
  [agency stop-tag]
  (for [route (vals (:routes agency)) :when (not (nil? (some #{stop-tag} (:stop-tags route))))]
    (:tag route)))

(defn combine-stop-and-routes
  [stop-tag route-tags]
  (map #(str % "|"  stop-tag) route-tags))

(defn multi-stops-from-stops
  "Return a list of unique multi-stops (route|stop) for the specified stops"
  [agency stop-tags]
  (loop [multi-stops #{} stop-tags stop-tags]
    (if (empty? stop-tags)
      multi-stops
      (recur
       (apply merge multi-stops (combine-stop-and-routes (first stop-tags) (routes-for-stop agency (first stop-tags))))
       (rest stop-tags)))))

(defn remove-empty-predictions
  "Remove predictions with no directions"
  [predictions]
  (filter #(not (empty? (:directions %))) predictions))

(defn index-stops
  "Create a geodb and index a list of stops"
  [stops]
  (let [db (geodb/make-db GEODB-GRID-SIZE)]
    (doseq [stop (vals stops)]
      (geodb/add-object db stop))
    (println "Indexed" (count stops) "stops")
    db))

;; Public API

(defn load-agency
  "Load all route configs for the specified agency. Returns a map containing stops and routes"
  [agency-tag]
  (fs/mkdirs (fs/expand-home NEXTBUS-DATA-DIR))
  (let [route-list-xml (load-route-list-xml agency-tag)]
    (loop [stops {} routes {} route-tags ($x:attrs* "//route" route-list-xml :tag)]
      (if (empty? route-tags)
        {:tag agency-tag :stops stops :routes routes :geodb (index-stops stops)}
        (let [route-config-xml (load-route-config-xml agency-tag (first route-tags))]
          (let [route-stops (parse-route-stops route-config-xml)
                route (parse-route route-config-xml)]
            (recur
             (apply merge stops route-stops) (assoc routes (-> route :tag) route)
             (rest route-tags))))))))

(defn find-stops
  [agency position radius]
  (map #(-> % :object :tag)
       (geodb/find-objects (:geodb agency) position radius)))

(defn predictions
  "Return predictions for the agency and stops"
  [agency stop-tags]
  (let [multi-stops (multi-stops-from-stops agency stop-tags)]
    (let [predictions-xml (load-predictions-xml (:tag agency) multi-stops)]
      (remove-empty-predictions (parse-predictions-xml agency predictions-xml)))))
