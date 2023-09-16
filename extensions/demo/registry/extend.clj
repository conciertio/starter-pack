(ns extensions.demo.registry.extend
  (:require [babashka.process :refer [shell]]
            [concierto.core :as core]))

(defn- create-registry [args]
  (let [engine (core/engine)
        access (:access (core/gather args))
        username (get-in access [:docker :registry :username])
        password (get-in access [:docker :registry :password])]

    (shell {:continue true} (format "%s rm -f registry" engine))
    (shell (format (str "%s run "
                        "-e REGISTRY_HTTP_USERNAME=%s "
                        "-e REGISTRY_HTTP_PASSWORD=$s "
                        "-p \"5000:5000\" "
                        "--name registry "
                        "-d "
                        "registry:2") engine username password))))

(defn dispatch-table []
  [{:cmds ["demo" "registry" "create"]
    :fn create-registry
    :help "create a local registry"}])