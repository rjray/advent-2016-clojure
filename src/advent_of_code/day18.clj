(ns advent-of-code.day18
  (:require [advent-of-code.utils :as u]))

(defn- tile-type
  "Determine tile-type based on the triple of true/false values"
  [triple]
  (case (vec triple)
    ([false false true]
     [true false false]
     [false true true]
     [true true false]) \^
    \.))

(defn- gen-row
  "Generate the next row from the given row"
  [row]
  (map tile-type (partition 3 1 (concat '(true)
                                        (map {\. true, \^ false} row)
                                        '(true)))))

(defn part-1
  "Day 18 Part 1"
  [input & [num]]
  (let [num (or num 40)]
    (->> input
         u/to-lines
         first
         seq
         (iterate gen-row)
         (take num)
         flatten
         frequencies)))

(defn part-2
  "Day 18 Part 2"
  [input & [num]]
  (let [num (or num 400000)]
    (->> input
         u/to-lines
         first
         seq
         (iterate gen-row)
         (take num)
         flatten
         frequencies)))
