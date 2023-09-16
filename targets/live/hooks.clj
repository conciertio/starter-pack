(ns targets.live.hooks
  (:require [concierto.core :as core]))

(defn attr-cluster
  "Create dynamic attribute map 'clusters' and will add whatever is
   returned under the key cluster per cluster
   e.g. {:east {:db {} :api [] :west {:db {} :api []}"
  [cluster]
  (let [db (flatten (core/select-machines :role :db :cluster cluster))
        api (flatten (core/select-machines :role :api :cluster cluster))]
    {:db (:ip (first db))
     :api (map :ip api)
     :cluster-name cluster}))

(defn attr-machine
  "Merges return value into current machine values. "
  [_machine _cluster-data]
  {:added "dynamically"})


(defn on-deploy [args]
  (println "Deployed " args))