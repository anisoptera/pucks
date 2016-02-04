(ns pucks.agents.bit
  (:use quil.core
        [pucks globals util vec2D]
        [pucks.agents generic active push1]))

(defn bit-proposals
  [p]
  {})

(defn bit
  "Returns a random bit puck."
  []
  (merge (push1)
         {:bit true
          :photosynthesizes true
          :radius 10}))
