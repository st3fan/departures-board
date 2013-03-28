(defproject transit "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "https://github.com/st3fan/clj-transit"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.zip "0.1.1"]
                 [ring/ring-core "1.1.8"]
                 [ring/ring-jetty-adapter "1.1.8"]
                 [compojure "1.1.5"]
                 [ring-middleware-format "0.2.4"]
                 [clj-http "0.6.4"]
                 [com.github.kyleburton/clj-xpath "1.4.1"]
                 [me.raynes/fs "1.4.0"]]
  :plugins [[lein-ring "0.8.3"]]
  :profiles {:dev {:dependencies [[ring/ring-devel "1.1.8"]
                                  [criterium "0.3.1"]]}}
  :ring {:handler transit.core/app :init transit.core/init}
  :jvm-opts ["-Xms64m" "-Xmx128m" "-server"])
