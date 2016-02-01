;; For development of the "star" entity type.
(ns pucks.worlds.dev.star1
  (:use [pucks globals util]
        [pucks.agents star nursery bondevolver]))

(defn settings []
  {:screen-size 1000
   :scale 1.0
   :single-thread-mode false
   :nursery-threshold 20
   :max-velocity 40
   :sensor-range 25
   :checkpoint-every 2000
   :pause-on-start true
   :neighborhood-size 500})

(defn get-random-position []
  [(rand-int (:screen-size (settings))) (rand-int (:screen-size (settings)))])

(defn agents []
  (vec (concat
        (star 500 500 250)
        [(assoc (nursery bondevolver) :position (get-random-position))])))

;(run-pucks (agents) (settings))
