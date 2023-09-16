(ns extensions.mycompany.disksize.extend
  (:require [concierto.core :as core]
            [clojure.string :as str]))

(defn- run [args machine-data _init]
  (let [ip (:ip machine-data)
        out (core/ssh-raw ip "df -h")]
    (->> (core/string-seq out)
         (map (fn [l]
                (when (str/index-of l (first (:args args)))
                  (str  l "\n"))))
         (filter some?)
         (apply str))))


(defn dispatch-table []
  [{:cmds ["mycompany" "disk-size"] :fn #(core/with-machines %1 run)
    :help "Find disk usage for selected devices"}])