(defproject eldar "0.1.0-SNAPSHOT"
  :description "A monitoring/alerting tool for Storm topologies"
  :url "https://github.com/lerouxrgd/eldar"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/clojurescript "1.7.228"]
                 ;; components
                 [com.stuartsierra/component "0.3.1"]
                 [duct "0.5.8"]
                 [clj-http "2.0.0"]
                 [cheshire "5.5.0"]
                 [org.mapdb/mapdb "1.0.6"]
                 [jarohen/chime "0.1.9"]
                 ;; config
                 [environ "1.0.1"]
                 [meta-merge "0.1.1"]
                 ;; logging (logback)
                 [ch.qos.logback/logback-classic "1.1.3"]
                 [ch.qos.logback/logback-core "1.1.3"]
                 [org.slf4j/slf4j-api "1.7.13"]
                 [org.codehaus.janino/janino "2.7.8"]
                 [org.clojure/tools.logging "0.3.1"]
                 ;; webapp (ring)
                 [ring "1.4.0"]
                 [ring/ring-defaults "0.1.5"]
                 [ring.middleware.logger "0.5.0" :exclusions [org.slf4j/slf4j-log4j12]]
                 [ring.middleware.conditional "0.2.0"]
                 [ring-jetty-component "0.3.0"]
                 ;; routing (compojure)
                 [compojure "1.4.0"]
                 [metosin/compojure-api "1.0.0"]
                 ;; ui
                 [re-frame "0.6.0"]
                 [cljs-ajax "0.5.3"]]
  :plugins [[lein-gen "0.2.2"]
            [lein-environ "1.0.1"]
            [lein-cljsbuild "1.1.0"]
            [hiccup-bridge "1.0.1"]]
  :generators [[duct/generators "0.4.5"]]
  :duct {:ns-prefix eldar}
  :main ^:skip-aot eldar.main
  :target-path "target/%s/"
  :resource-paths ["resources" "target/cljsbuild"]
  :prep-tasks [["cljsbuild" "once"] ["compile"]]
  :cljsbuild
  {:builds
   {:main {:jar true
           :source-paths ["src"]
           :compiler {:output-to "target/cljsbuild/eldar/public/js/main.js"
                      :optimizations :advanced}}}}
  :aliases {"gen"   ["generate"]
            "setup" ["do" ["generate" "locals"]]}
  :profiles
  {:dev {:source-paths ["dev"]
         :repl-options {:init-ns user
                        :nrepl-middleware [cemerick.piggieback/wrap-cljs-repl]}
         :env {:eldar-config "config/config_dev.edn"}
         :dependencies [[org.clojure/tools.namespace "0.2.11"]
                        [reloaded.repl "0.2.1"]
                        [duct/figwheel-component "0.3.1"]
                        [figwheel "0.5.0-6"]
                        [criterium "0.4.3"]
                        [eftest "0.1.0"]
                        [kerodon "0.7.0"]]}
   :repl {:resource-paths ^:replace ["resources" "target/figwheel"]
          :prep-tasks     ^:replace [["compile"]]}
   :uberjar {:aot :all}})
