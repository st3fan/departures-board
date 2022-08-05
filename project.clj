(defproject departures-board "0.1.0-SNAPSHOT"
  :description "Departures Board"
  :url "https://github.com/st3fan/departures-board"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.10.3"]
                 [org.clojure/data.zip "1.0.0"]
                 [ring/ring-core "1.9.5"]
                 [ring/ring-jetty-adapter "1.9.5"]
                 [compojure "1.7.0"]
                 [ring-middleware-format "0.7.5"]
                 [clj-http "3.12.3"]
                 [com.github.kyleburton/clj-xpath "1.4.12"]
                 [me.raynes/fs "1.4.6"]]
  :plugins [[lein-ring "0.12.6"]]
  :profiles {:dev {:dependencies [[ring/ring-devel "1.9.5"]
                                  [criterium "0.4.6"]]}}
  :ring {:handler departures-board.core/app :init departures-board.core/init}
  :main departures-board.core
  :jvm-opts ["-Xms64m" "-Xmx128m" "-server"])
