(ns advent-of-code.day06
  (:require [advent-of-code.utils :as u]))

(defn- find-most-freq
  "Find the most-frequent key in the given map"
  [m]
  (sort-by last m))

(defn part-1
  "Day 06 Part 1"
  [input]
  (->> input
       u/to-lines
       u/transpose
       (map frequencies)
       (map (partial sort-by last))
       (map reverse)
       (map ffirst)
       (apply str)))

(defn part-2
  "Day 06 Part 2"
  [input]
  (->> input
       u/to-lines
       u/transpose
       (map frequencies)
       (map (partial sort-by last))
       (map ffirst)
       (apply str)))
