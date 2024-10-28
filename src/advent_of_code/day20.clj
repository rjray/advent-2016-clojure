(ns advent-of-code.day20
  (:require [advent-of-code.utils :as u]))

(defn- in?
  "Test if the given number is within the given range"
  [n rng]
  (and (>= n (first rng))
       (<= n (last rng))))

(defn- find-lowest
  "Find the lowest value that is outside of all the ranges given"
  [ranges]
  (loop [addr 0]
    (let [pred (partial in? addr)]
      (if-let [rng (first (filter pred ranges))]
        (recur (inc (last rng)))
        addr))))

(defn part-1
  "Day 20 Part 1"
  [input]
  (->> input
       u/to-lines
       u/parse-ranges
       (sort-by first)
       find-lowest))

(defn- find-all-count
  "Find count of all addresses outside of the given ranges"
  [ranges]
  (loop [addr 0, acc 0]
    (cond
      (= addr 4294967296) acc
      :else
      (let [pred (partial in? addr)]
        (if-let [rng (first (filter pred ranges))]
          (recur (inc (last rng)) acc)
          (recur (inc addr) (inc acc)))))))

(defn part-2
  "Day 20 Part 2"
  [input]
  (->> input
       u/to-lines
       u/parse-ranges
       (sort-by first)
       find-all-count))
