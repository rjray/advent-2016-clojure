(ns advent-of-code.day16
  (:require [advent-of-code.utils :as u]))

(defn- expand
  "Expand `data` according to the modified dragon curve algorithm"
  [data]
  (concat data '(0) (map #(- 1 %) (reverse data))))

(defn- fill-to
  "Repeatedly expand `data` until it is at least `length` long."
  [length data]
  (loop [data data]
    (if (<= length (count data))
      (take length data)
      (recur (expand data)))))

(defn- checksum
  "Calculate the checksum of `data`. Return as a string."
  [data]
  (let [chkmap {'(0 0) 1, '(0 1) 0, '(1 0) 0, '(1 1) 1}]
    (loop [chksum data]
      (if (odd? (count chksum))
        (apply str chksum)
        (recur (for [pair (partition 2 chksum)]
                 (chkmap pair)))))))

(defn part-1
  "Day 16 Part 1"
  [input & [length]]
  (let [length (or length 272)]
    (->> input
         u/to-lines
         first
         (map {\1 1, \0 0})
         (fill-to length)
         checksum)))

(defn part-2
  "Day 16 Part 2"
  [input & [length]]
  (let [length (or length 35651584)]
    (->> input
         u/to-lines
         first
         (map {\1 1, \0 0})
         (fill-to length)
         checksum)))
