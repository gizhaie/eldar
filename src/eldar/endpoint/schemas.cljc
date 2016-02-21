(ns eldar.endpoint.schemas
  (:require [schema.core :as s #?@(:cljs [:include-macros true])]))

(s/defschema Watcher
  {:name s/Str
   :type (s/enum :bolts :spouts)
   :check s/Str ;; valids are: "any" "all" "bolt1,bolt2" etc
   :metric s/Str
   :comparator (s/enum :< :<= :> :>=)
   :threshold s/Num
   :freq s/Num
   :mailto s/Str
   :subject s/Str
   :max-sent s/Num
   :cooldown s/Num})

(s/defschema WatchersRegistration
  {:topo-id s/Str
   :watchers [Watcher]})

(s/defschema RegistrationResp
  {:status (s/enum :ok :ng)
   :errors [{:field s/Str :msg s/Str}]})

(s/defschema TopoStatus
  {:name s/Str 
   :id s/Str 
   :monitored? s/Bool 
   :monitoring [Watcher]})
