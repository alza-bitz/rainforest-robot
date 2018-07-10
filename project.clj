(defproject rainforest-robot "0.1.0-SNAPSHOT"
  :description "Rainforest Robot Assignment"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]]
  :main rainforest-robot.core
  :profiles {:dev {:resource-paths ["test-resources"]}
             :uberjar {:aot :all}})
