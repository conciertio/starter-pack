(ns targets.live.hooks
  (:require [concierto.core :as core]))

(defn attr-cluster
  " Create dynamic attribute map 'clusters' and will add whatever is returned
under the cluster key per cluster e.g.
{:east {:db {...} :api [...] :cluster-name :east}
 :west {:db {...} :api [...] :cluster-name :west} "
  [cluster]
  (let [db (flatten (core/select-machines :role :db :cluster cluster))
        api (flatten (core/select-machines :role :api :cluster cluster))]
    {:db (:ip (first db))
     :api (map :ip api)
     :cluster-name cluster}))

(def recno (atom 0))

; silly example showing addition of a recno field
(defn attr-machine
  " Merges return value into current machine values. "
  [_machine]
  {:recno (str (swap! recno inc))})


(defn on-deploy [args]
  (println " Deployed " args))