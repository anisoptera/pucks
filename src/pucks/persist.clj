(ns pucks.persist
  (:use [pucks.globals :as g]))

(def transient-keys [:neighbors :overlaps :sensed])

(defn validate-map
  "Validates that a map can be persisted across JVM instances."
  [x]
  (doseq [[k v] x]
    (let [serialized (with-out-str (print-dup v *out*))]
      (when (re-find #"fn_" serialized)
        (println (str "key " k " contains an anonymous fn " serialized))
        false))))

(defn persist-state
  "Persists global state into a string which can be loaded later."
  []
  (let [raw-agents @g/all-agents ;;todo: this would probably be better with refs
        iteration @g/iteration
        settings @g/pucks-settings
        agents (map #(apply dissoc % transient-keys) raw-agents)]
    (doseq [a agents]
      (validate-map a))
    (binding [*print-dup* true]
      (pr-str {:agents agents
               :iteration iteration
               :settings settings}))))

(defn load-state
  "Loads state from a string into globals."
  [state-str]
  (let [{:keys [agents iteration settings]} (read-string state-str)]
    (reset! g/all-agents agents)
    (reset! g/iteration iteration)
    (reset! g/pucks-settings settings) ;; TODO: haha what synchronization??
    ))
