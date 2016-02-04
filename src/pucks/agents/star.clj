;; A star is a:
;;  - nursery (it creates 'bits', plankton-like creatures)
;;  - vent (it gives these creatures energy through 'photosynthesis')
;;  -- it also gives other creatures smaller amounts of energy, offsetting the cost of living partially for those within its range
;;  - zapper (it's hot, it hurts things that are too close to it)"
(ns pucks.agents.star
  (:use quil.core
        [pucks globals util vec2D]
        [pucks.agents generic bit vent zapper nursery]
        [clojure.string :only [starts-with?]]))

(defn- solid-radius
  "gets the solid part of a star"
  [radius]
  (/ radius 20))

(defn- danger-radius
  "gets the size of the hot part of a star"
  [radius]
  (/ radius 5))

(defn draw-star
  "Draw a star."
  [{:keys [position star-radius color rotation]}]
  (let [[x y] position
        [r g b] color
        [core-r core-g core-b] [255 255 255]
        core-diameter (* 2 (solid-radius star-radius))
        heated-diameter (* 2 (danger-radius star-radius))]
    (push-matrix)
    (translate x y)
    (rotate rotation)
    ;; membrane
    (fill r g b 100)
    (ellipse 0 0 (* star-radius 2) (* star-radius 2))
    ;; ~danger zone~
    (fill 255 0 0 196)
    (ellipse 0 0 heated-diameter heated-diameter)
    ;; core
    (fill core-r core-g core-b)
    (ellipse 0 0 core-diameter core-diameter)
    (pop-matrix)))


(defn get-random-spawn-point
  "Finds a 'safe' spawn point for the input star."
  [{:keys [radius]}]
  (let [min-spawn-dist (danger-radius radius)
        max-spawn-dist radius
        spawn-range (- max-spawn-dist min-spawn-dist)]
    (->> (repeatedly 2 #(rand-int (* 2 spawn-range)))
         (map #(- % spawn-range))
         (map #(if (< 0 %) (+ % min-spawn-dist) (- % min-spawn-dist)))
         (vec))))

(def photosynthesis-energy 0.01)

(defn star-vent-proposals
  "If a puck photosynthesizes, it gets lots of energy.
  Everyone else gets half off cost of living under the star's light."
  [p]
  {:transfer
   (into [] (->> (:overlaps p)
                 (filter #(and (< 0.02 (:energy %)) (> 1.0 (:energy %))))
                 (map (fn [o]
                        {:self (:id p)
                         :other (:id o)
                         :bid {:energy (if (:photosynthesizes o)
                                         photosynthesis-energy
                                         (/ 2 (:cost-of-living @pucks-settings)))}
                         :ask {}}))))
   })

(defn bits-for-nursery
  "Filters for bits created by a given nursery."
  [{:keys [id]}]
  (filter
   #(and (:bit %)
         (= id (:parent-nursery %)))
   @all-agents))

(defn star-nursery-proposals
  [{:keys [id] :as p}]
  (if (> 10 (count (bits-for-nursery p)))
    {:spawn [(assoc (bit)
                    :position (get-random-spawn-point p)
                    :parent-nursery id)]}
    {}))

(defn star
  "Create a baseline star."
  [x y radius]
  (let [id (gensym "star-")]
    [(merge (generic)
            {:id (symbol (str id "-nursery"))
             :solid false
             :nursery true
             :radius radius
             :position [x y]
             :proposal-function star-nursery-proposals
             :draw-function false-fn})
     (merge (zapper)
            {:id (symbol (str id "-zapper"))
             :solid false
             :position [x y]
             :radius (danger-radius radius)
             :draw-function false-fn})
     (merge (vent)
            {:id (symbol (str id "-vent"))
             :solid false
             :position [x y]
             :radius radius
             :proposal-function star-vent-proposals
             :draw-function false-fn})
     (merge (generic)
            {:id id
             :stone true
             :solid true
             :radius (solid-radius radius)
             :star-radius radius
             :position [x y]
             :color [200 20 200]
             :draw-function draw-star})]))
