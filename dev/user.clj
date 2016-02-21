(ns user
  (:require [clojure.repl :refer :all]
            [clojure.pprint :refer [pprint]]
            [clojure.tools.namespace.repl :refer [refresh]]
            [com.stuartsierra.component :as component]
            [eftest.runner :as eftest]
            [reloaded.repl :refer [system init start stop go reset clear]]
            [duct.component.figwheel :as figwheel]
            [duct.component.endpoint :refer [endpoint-component]]
            [eldar.system :as sys]
            [mock :refer [mock-endpoint]]))

(ns-unmap *ns* 'test)
(defn test []
  (eftest/run-tests (eftest/find-tests "test") {:multithread? false}))

(def dev-config
  {:figwheel
   {:css-dirs ["resources/eldar/public/css"]
    :builds   [{:source-paths ["src" "dev"]
                :build-options
                {:optimizations :none
                 :main "cljs.user"
                 :asset-path "js"
                 :output-to  "target/figwheel/eldar/public/js/main.js"
                 :output-dir "target/figwheel/eldar/public/js"
                 :source-map true
                 :source-map-path "js"}}]}})

(defn cljs-repl []
  (figwheel/cljs-repl (:figwheel system)))

(defn new-system []
  (let [system-map (into (sys/new-system-map)
                         {:figwheel (figwheel/server (:figwheel dev-config))
                          :mock-rest (endpoint-component mock-endpoint)})
        system-deps (update-in sys/system-deps [:app] conj :mock-rest)]
    (sys/new-system system-map system-deps)))

(reloaded.repl/set-init! new-system)
