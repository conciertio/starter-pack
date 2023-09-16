(ns targets.live.machines
  (:require [extensions.demo.lxd.extend :as lxd]))


(defn source []
  (lxd/machine-list))