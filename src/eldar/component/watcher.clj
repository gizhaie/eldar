(ns eldar.component.watcher
  (:require [com.stuartsierra.component :as component]
            [eldar.component.storm :as storm]
            [clojure.tools.logging :as log]
            [clojure.edn :as edn]
            [chime :refer [chime-ch]]
            [clj-time.core :as t]
            [clj-time.periodic :refer [periodic-seq]]
            [clojure.core.async :as a :refer [go-loop <! timeout alts!]])
  (:import [org.mapdb DB DBMaker]))

(deftype EDNSeralizer []
  org.mapdb.Serializer
  (fixedSize [_] -1)
  (serialize [_ out obj]
    (.writeUTF out (pr-str obj)))
  (deserialize [_ in available]
    (edn/read-string (.readUTF in)))
  ;; MapDB expects serializers to be serializable.
  java.io.Serializable)

(defrecord Watcher [watcher-conf]
  component/Lifecycle
  (start [this] ;; TODO start monitoring threads/core-async upon reading db file
    (cond-> this
      ;; associating :db
      (not (:db this)) 
      (assoc :db (.. (DBMaker/newFileDB (java.io.File. (:db-location watcher-conf)))
                     closeOnJvmShutdown
                     compressionEnable
                     make))
      ;; associating :watchers
      (not (:watchers this)) 
      (#(assoc % :watchers (.. (:db %)
                               (createTreeMap "watchers")
                               (valueSerializer (EDNSeralizer.))
                               (makeOrGet))))))
  (stop [this] 
    (.close (:db this))
    (-> this
        (dissoc :watchers)
        (dissoc :db))))

(defn watcher-component [watcher-conf]
  (->Watcher watcher-conf))

(defn mk-watcher-task [watcher-spec] ;; TODO use chime-ch, alts!+timeout inside a go-loop
  (:freq watcher-spec))

(defn status [sys]
  (map (fn [topology] 
         (let [topo-id (:id topology)
               monitoring (get (-> sys :watcher :watchers) (keyword topo-id))] 
           {:name (:name topology)
            :id topo-id
            :monitored? (not (nil? monitoring))
            :monitoring (if (nil? monitoring) [] monitoring)}))
       (storm/list-topologies (-> sys :storm-api :domain))))

(defn register [watcher-compo {:keys [topo-id watchers]}]
  (.put (:watchers watcher-compo) (keyword topo-id) watchers)
  (.commit (:db watcher-compo))
  {:status :ok :errors []})
