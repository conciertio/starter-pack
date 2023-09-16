(ns targets.local.hooks)

(defn attr-cluster
  [cluster]
  {:db "172.17.0.1"
   :api (list "172.17.0.1")
   :cluster-name cluster})

(defn attr-machine
  [machine _cluster-data]
  {:host-name (:host machine)})
