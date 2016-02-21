(ns eldar.component.storm
  (:require [clojure.data.json :as json]
            [cheshire.core :refer [parse-string]]
            [clj-http.client :as client]
            [com.stuartsierra.component :as component]))

(defrecord Storm [client-conf]
  component/Lifecycle
  (start [this] 
    (if (:domain this)
      this
      (assoc this :domain (:domain client-conf))))
  (stop [this] 
    (dissoc this :domain)))

(defn storm-component [client-conf]
  (->Storm client-conf))

(defn list-topologies [domain]
  (-> (client/get (str domain "/api/v1/topology/summary") {:accept :json})
      :body
      (parse-string true)
      :topologies))
