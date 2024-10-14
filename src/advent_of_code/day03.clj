(ns advent-of-code.day03
  (:require [advent-of-code.utils :as u]))

(defn- is-possible
  "Predicate to indicate if the three values can be a valid triangle"
  [[a b c]]
  ;; I could maybe do some trickery with partitions or selections, but there
  ;; are only three cases here.
  (and (> (+ a b) c)
       (> (+ a c) b)
       (> (+ b c) a)))

(defn part-1
  "Day 03 Part 1"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       (filter is-possible)
       count))

(defn- transform
  "Take the full data set and read by columns, per puzzle spec"
  [lines]
  (apply concat (map u/transpose (partition 3 lines))))

(defn part-2
  "Day 03 Part 2"
  [input]
  (->> input
       u/to-lines
       (map u/parse-out-longs)
       transform
       (filter is-possible)
       count))
