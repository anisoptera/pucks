;; v1 of an agent with a PushGP brain
(ns pucks.agents.push1
  (:use quil.core
        [clojush random pushstate interpreter globals translate]
        [clojush.instructions common random-instructions]
        [pucks globals util vec2D]
        [pucks.agents generic]))

(defn push1-proposal-func
  "Run our genome's program."
  [{:keys [memory] :as p}]
  (let [program (translate-plush-genome-to-push-program memory {:max-points 100})
        state (make-push-state)
        final-state (run-push program state)
        float-stack (:float final-state)]
    (merge {:memory {:program program}}
           (when-let [rotation (first float-stack)]
             {:rotation rotation})
           (when-let [acceleration (nth float-stack 2 nil)]
             {:acceleration acceleration}))))

(def genome-instructions
  (concat (registered-for-stacks [:float])
          (list #(rand))
          '(float_rand)))

(defn push1
  "Create a push1 agent."
  []
  (merge (generic)
         {:memory {:genome (random-plush-genome 100 genome-instructions)}
          :proposal-function push1-proposal-func}))
