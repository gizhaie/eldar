(ns mock
  (:require [compojure.api.sweet :refer :all]
            [ring.util.http-response :refer :all]
            [clojure.tools.logging :as log]))

(defn mock-endpoint [sys]
  (api
   (context "/mock" []
            (GET "/api/v1/topology/summary" []
                 (ok {:topologies 
                      [{:id "WordCount3-1-1402960825",
                        :name "WordCount3",
                        :status "ACTIVE",
                        :uptime "6m 5s",
                        :tasksTotal 28,
                        :workersTotal 3,
                        :executorsTotal 28}
                       ]})))))

