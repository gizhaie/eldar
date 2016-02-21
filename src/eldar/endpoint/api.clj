(ns eldar.endpoint.api
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [compojure.api.exception :refer [stringify-error]]
            [ring.util.http-response :refer :all]
            [clojure.tools.logging :as log]
            [schema.utils :as su]
            [eldar.endpoint.schemas :as es]
            [eldar.component.watcher :as watcher]))

(defn api-endpoint [sys]
  (api
   {:exceptions 
    {:handlers
     {:compojure.api.exception/default
      (fn [^Exception e _ _]
        (log/error e)
        (internal-server-error "Internal Server Error"))
      :compojure.api.exception/response-validation
      (fn [_ data request]
        (let [err (stringify-error (su/error-val data))]
          (log/error (str "Invalid Server Response " err))
          (log/debug (str "\n" (with-out-str (clojure.pprint/pprint request))))
          (internal-server-error "Internal Server Error")))
      :compojure.api.exception/request-validation
      (fn [_ data request]
        (let [err (stringify-error (su/error-val data))]
          (log/error (str "Invalid Request " err))
          (log/debug (str "\n" (with-out-str (clojure.pprint/pprint request))))
          (bad-request {:errors err})))}}}
   (swagger-routes 
    {:ui "/eldar/api-docs"
     :spec "/eldar/swagger2.json"
     :data {:info {:title "Eldar API" :version "0.1.0"}
            :tags [{:name "watchers" 
                    :description "list and register topologies watchers"}]}})
   (context 
    "/eldar" [] 
    :tags ["watchers"]
    (GET "/watcher/status" []
         :summary "lists all topologies monitoring status"
         :return [es/TopoStatus]
         (ok (watcher/status sys)))
    (POST "/watcher/register" []
          :summary "registers a list of watchers for a given topology"
          :body [registration es/WatchersRegistration]
          :return es/RegistrationResp
          (ok (watcher/register (-> sys :watcher) registration))))))
