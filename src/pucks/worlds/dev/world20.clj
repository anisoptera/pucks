(ns pucks.worlds.dev.world20
  (:use [pucks core globals]
        [pucks.agents vent nursery zapper bondevolver]))

(defn agents []
  (vec (concat 
         (for [x (range 100 3000 300)
               y (range 100 3000 300)]
           (assoc (vent) :position [x y]))
         [(assoc (nursery bondevolver) :position [600 600])])))

(defn settings []
  {:screen-size 3000
   :scale 0.3
   :single-thread-mode false
   :nursery-threshold 5
   :max-velocity 40})

(run-pucks (agents) (settings))
