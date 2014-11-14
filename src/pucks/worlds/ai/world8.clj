
;; A pucks world.

(ns pucks.worlds.ai.world8
  (:use [pucks core globals]
        [pucks.agents stone vent gate zapper nursery user donor shooter]))

(defn diff
  "Returns the absolute value of the difference between the two arguments."
  [n m]
  (Math/abs (- n m)))

(defn rand+-
  "Returns a positive or negative random number with magnitude less than n."
  [n]
  (- (rand (* 2 n)) n))

(defn ventbox
  "Returns a sequence of pucks that form a box of stones with a vent in the
   middle and a gate on each side."
  [[center-x center-y]]
  (concat
    ;; stones
    [(merge (stone) {:position [(- center-x 50)(- center-y 50)]})
     (merge (stone) {:position [(- center-x 50)(+ center-y 50)]})
     (merge (stone) {:position [(+ center-x 50)(- center-y 50)]})
     (merge (stone) {:position [(+ center-x 50)(+ center-y 50)]})]
    ;; gates
    [(merge (gate) {:position [(- center-x 50) center-y]})
     (merge (gate) {:position [(+ center-x 50) center-y]})
     (merge (gate) {:position [center-x (- center-y 50)]})
     (merge (gate) {:position [center-x (+ center-y 50)]})]
    ;; vent
    [(merge (vent) {:position [center-x center-y]})]))

(defn agents []
  (concat 
    ;; walls
    ;; horizontal
    (apply concat
           (for [y (range 0 1581 200)]
             (let [start (rand-int 1600)
                   stop (+ start 100 (rand-int 600))]
               (for [x (range start stop 20)]
                 (merge (stone) {:position [x y]})))))   
    ;; vertical
    (apply concat
           (for [x (range 0 1581 200)]
             (let [start (rand-int 1600)
                   stop (+ start 100 (rand-int 300))]
               (for [y (range start stop 20)]
                 (merge (stone) {:position [x y]})))))
    ;; other pucks
    (apply concat
           (mapv #(if (:ventbox %)
                    (ventbox (:position %))
                    [%])
                 (mapv (fn [p loc]
                         (merge p {:position loc}))
                       (concat [(nursery user)]
                               (repeatedly 4 #(do {:ventbox true}))
                               (repeatedly 8 zapper)
                               (repeatedly 1 #(nursery shooter))
                               (repeatedly 1 (fn [] 
                                               (nursery 
                                                 #(merge (donor :key) 
                                                         {:color [0 0 255]
                                                          :velocity [(rand+- 5) (rand+- 5)]}))))
                               (repeatedly 1 (fn [] 
                                               (nursery 
                                                 (fn [] 
                                                   (let [keep-off-map [:neighbors :overlaps :sensed 
                                                                       :inventory :memory :draw-function 
                                                                       :proposal-function :spawn-function]]
                                                     (merge (donor 
                                                              {:map (mapv #(apply dissoc (cons % keep-off-map))
                                                                          @all-agents)}) 
                                                            {:color [255 96 255]
                                                             :core-color [255 96 255]
                                                             :velocity [(rand+- 5) (rand+- 5)]})))))))
                       (shuffle (for [x (range 100 1501 200)
                                      y (range 100 1501 200)]
                                  [x y])))))))

(defn settings []
  {:screen-size 1600
   :scale 0.5
   :single-thread-mode false
   :max-velocity 40
   :torpedo-energy 0.02})

;(run-pucks (agents) (settings))