(ns eldar.endpoint.ui
  (:require [compojure.api.sweet :refer :all]
            [compojure.route :as route]
            [ring.util.http-response :refer :all]
            [clojure.tools.logging :as log]))

(def public "eldar/public")
(def templates "eldar/endpoint/ui/templates") ;; TODO inject through user.clj

(defn ignore-trailing-slash
  "Modifies the request uri before calling the handler.
  Removes a single trailing slash from the end of the uri if present."
  [handler]
  (fn [request]
    (let [uri (:uri request)]
      (handler (assoc request :uri (if (and (not (= "/" uri)) (.endsWith uri "/"))
                                     (subs uri 0 (dec (count uri)))
                                     uri))))))

(defn ui-endpoint [sys]
  (-> (api
       (context "/eldar" []
                (GET "/ui" []
                     :no-doc true
                     (-> (resource-response "index.html" {:root public})
                         (content-type "text/html")))
                (route/resources "/" {:root public})
                (route/resources "/ui/" {:root public})))
      (ignore-trailing-slash)))
