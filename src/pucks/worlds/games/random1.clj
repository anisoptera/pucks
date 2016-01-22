;; First pass at a randomly-laid-out world.
(ns pucks.worlds.games.random1
  (:use [pucks core globals]
        [pucks.agents vent nursery zapper bondevolver]))

(defn settings []
  {:screen-size 9000
   :scale 0.1
   :single-thread-mode false
   :nursery-threshold 20
   :nursery-force-chance 1000
   :max-velocity 60
   :sensor-range 200})

(defn star []
  (merge (vent)
         {:radius 75
          :color [255 200 60]}))

(defn get-random-position []
  [(rand-int (:screen-size (settings))) (rand-int (:screen-size (settings)))])

(defn agents []
  (vec (concat
        (repeatedly 200 #(assoc (star) :position (get-random-position)))
        [(assoc (nursery bondevolver) :position (get-random-position))])))

(run-pucks (agents) (settings))

