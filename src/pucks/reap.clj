;; Reaper for pucks; converts dead agents to corpses and eventually removes them.

(ns pucks.reap
  (:use pucks.globals
        [pucks.agents.generic :only [empty-map-fn]]))

(defn reap
  "Converts agents with no remaining energy to corpses and eventually
   removes them from the world."
  []
  (swap! all-agents
         (fn [objs]
           (let [with-corpses
                 (mapv #(if (and (:mobile %) (not (pos? (:energy %))))
                          (merge % {:corpse true
                                    :death-step (:steps %)
                                    :mobile false
                                    :color [100 100 100]
                                    :proposal-function pucks.agents.generic/empty-map-fn})
                          %)
                       objs)]
             ;; eliminate corpses that are fully rotted
             (filterv #(not (and (:corpse %)
                                 (> (:steps %)
                                    (+ (:death-step %) 30))))
                      with-corpses)))))
