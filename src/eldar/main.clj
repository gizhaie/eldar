(ns eldar.main
  (:gen-class)
  (:require [clojure.tools.logging :as log] 
            [com.stuartsierra.component :as component]
            [eldar.system :refer [new-system]]))

(defn -main [& args]
  (let [system (new-system)]
    (log/info "Starting HTTP server on port" (-> system :http :port))
    (try
      (component/start system)
      (catch Exception e (str "Exception at startup: " (.getMessage e))))))
