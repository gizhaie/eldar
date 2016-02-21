(ns eldar.system
  (:require ;; config
            [clojure.edn :as edn]
            [environ.core :refer [env]]
            [meta-merge.core :refer [meta-merge]]
            ;; logging
            [clojure.tools.logging :refer [debug info warn error]]
            ;; ring middlewares
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [duct.middleware.not-found :refer [wrap-not-found]]
            [ring.middleware.logger :refer [wrap-with-logger]]
            [ring.middleware.conditional :refer [if-url-doesnt-match]]
            ;; components
            [com.stuartsierra.component :as component]
            [ring.component.jetty :refer [jetty-server]]
            [duct.component.handler :refer [handler-component]]
            [duct.component.endpoint :refer [endpoint-component]]
            [eldar.component.storm :refer [storm-component]]
            [eldar.component.watcher :refer [watcher-component]]
            ;; endpoints
            [eldar.endpoint.ui :refer [ui-endpoint]]
            [eldar.endpoint.api :refer [api-endpoint]]))

(defn wrap-logging [handler] 
  (if-url-doesnt-match 
   handler
   #"(.*)?\/(js|lib|conf|css|img|fonts|images|swagger)\/?(.*)"
   (fn [h] 
     (wrap-with-logger 
      h :debug #(debug %) :info #(info %) :warn #(warn %) :error #(error %)))))

(defn mk-config []
  (meta-merge
   ;; base config
   {:app {:middleware [[wrap-not-found :not-found]
                       [wrap-defaults :defaults]
                       [wrap-logging]]
          :defaults (meta-merge api-defaults {})
          :not-found "Resource Not Found"}}
   ;; custom config
   (-> (env :eldar-config)
       slurp
       edn/read-string)))

(defn new-system-map []
  (let [config (mk-config)]
    (component/system-map
     :storm-api (storm-component (:storm-api config))
     :watcher (watcher-component (:watcher config))
     :api (endpoint-component api-endpoint)
     :ui (endpoint-component ui-endpoint)
     :app (handler-component (:app config))
     :http (jetty-server (:http config)))))

(def system-deps
  {:http [:app]
   :app [:ui :api]
   :ui []
   :api [:storm-api :watcher]
   :watcher [:storm-api]
   :storm-api []})

(defn new-system
  ([]
   (new-system (new-system-map) system-deps))
  ([system-map system-deps]
   (-> system-map (component/system-using system-deps))))

