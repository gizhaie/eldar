(ns cljs.user
  (:require [figwheel.client :as figwheel]
            ;; forcing figwheel loading by simple require
            [eldar.endpoint.ui]
            [re-frame.db :refer [app-db]]))

(js/console.info "Starting in development mode")

(enable-console-print!)

(figwheel/start {:websocket-url "ws://localhost:3449/figwheel-ws"})

