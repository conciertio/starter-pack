(ns extensions.demo.lxd.extend
  (:require [babashka.curl :as curl]
            [cheshire.core :as json]
            [concierto.core :as core]
            [medley.core :as m]))

(def ^:private lxd-socket "/var/snap/lxd/common/lxd/unix.socket")

(defn- lxd-get
  "Query lxd socket."
  [url]
  (-> (curl/get (str "http://" url)
                {:raw-args ["--unix-socket" lxd-socket]})
      :body
      (json/parse-string true)))

(defn- get-concierto-machines
  "Find machines with concierto meta data."
  []
  (->> (lxd-get "/a/1.0/instances")
       :metadata
       (map #(lxd-get (str "/a" %1)))
       (filter #(some? (get-in %1 [:metadata
                                   :config
                                   :user.concierto.role])))))

(defn- get-metadata
  "Get the metadata added manually to the lxd instances."
  [machine]
  (let [meta (->> (select-keys (get-in machine [:metadata :config])
                               [:user.concierto.role
                                :user.concierto.cluster])
                  (m/map-keys (fn [k]
                                (case k
                                  :user.concierto.role :role
                                  :user.concierto.cluster :cluster))))]
    (assoc meta :name (get-in machine [:metadata :name]))))

(defn- get-state
  "Lxd holds the ip addresses and other runtime info in state url"
  [machine_name]
  (lxd-get (str "/a/1.0/instances/" machine_name "/state")))

(defn- get-ip4 [machine-name]
  (->> (get-in (get-state machine-name)
               [:metadata :network :eth0 :addresses])
       (filter #(= (:family %1) "inet"))
       (map :address)
       first))

(defn machine-list []
  (->>
   (get-concierto-machines)
   (map (fn [machine]
          (let [meta (get-metadata machine)
                machine-name (:name meta)]
            (assoc meta
                   :ip (get-ip4 machine-name)
                   :host machine-name))))
   (map #(m/map-kv-vals (fn [k v]
                          (if (= k :role)
                            (keyword v)
                            v)) %1))

   (map #(dissoc %1 :name))))

(defn- setup-script []
  (let [cur-path (core/path (core/app-dir) "extensions" "demo" "lxd")]
    (core/path cur-path "setup")))

(defn- create-cluster [args]
  (let [cluster-name (core/get-option args :cluster)]
    (println "Creating cluster" cluster-name)
    (core/cshell (str (setup-script) " create " cluster-name))))

(defn- create-base-image [_args]
  (core/cshell (str (setup-script) " create-base")))

(defn- clean-all [_args]
  (core/cshell (str (setup-script) " clean-all")))

(defn- clean-one [args]
  (core/cshell (str (setup-script) " clean-one" (first (:args args)))))


(defn dispatch-table []
  [{:cmds ["demo" "lxd" "cluster-base-image"] :fn #(create-base-image %1)
    :help "base Alpine with extras"}

   {:cmds ["demo" "lxd" "cluster-create"]
    :fn #(create-cluster %1)
    :args->opts [:cluster]
    :help "create a new cluster"}

   {:cmds ["demo" "lxd" "cluster-remove"] :fn clean-one
    :help "remove cluster instance"}

   {:cmds ["demo" "lxd" "cluster-remove-all"] :fn clean-all
    :help "remove all cluster instances"}])