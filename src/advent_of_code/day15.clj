(ns advent-of-code.day15
  (:require [advent-of-code.utils :as u]))

(defn- build-system
  "Take the sets of numbers and build up the system of discs"
  [num-lists]
  (map #(zipmap [:disc :numpos :curpos]
                (vector (first %)
                        (second %)
                        (last %)))
       num-lists))

(defn- match-up?
  "Predicate to see if the system will match up the 0-positions from time `t`"
  [sys t]
  (every? zero? (reduce (fn [acc {:keys [disc numpos curpos]}]
                          (cons (rem (+ t disc curpos) numpos) acc))
                        ()
                        sys)))

(defn- find-time
  "Find the first start-time that will work for the given system"
  [sys]
  (filter (partial match-up? sys) (range)))

(defn part-1
  "Day 15 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       build-system
       find-time
       first))

(defn part-2
  "Day 15 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       build-system
       reverse
       (cons {:disc 7, :numpos 11, :curpos 0})
       reverse
       find-time
       first))
