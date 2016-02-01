(ns pucks.agents.bit
  (:use quil.core
        [pucks globals util vec2D]
        [pucks.agents generic active]))

(defn bit-proposals
  [p]
  {})

(defn bit
  "Returns a random bit puck."
  []
  (merge (active)
         {:bit true
          :photosynthesizes true
          :proposal-function bit-proposals
          :radius 5}))
