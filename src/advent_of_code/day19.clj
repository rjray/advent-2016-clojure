(ns advent-of-code.day19
  (:require [advent-of-code.utils :as u]))

(defn- setup
  "Set up the game with `n` players"
  [n]
  (vec (take n (map #(hash-map :elf %, :gifts 1) (iterate inc 1)))))

(defn- play
  "Play the game"
  [state]
  (loop [state state]
    (let [num (count state)]
      (if (= num 1)
        (first state)
        (let [state' (reduce (fn [s' n] (assoc-in s' [n :gifts] 0))
                             state
                             (range 1 num 2))]
          (recur (vec (filter #(pos? (:gifts %))
                              (if (odd? num)
                                (assoc-in state' [0 :gifts] 0)
                                state')))))))))

(defn part-1
  "Day 19 Part 1"
  [input]
  (->> input
       u/to-lines
       first
       parse-long
       setup
       play
       :elf))

;; Had something fairly complex here, never worked. Adapted solution from
;; https://www.reddit.com/r/adventofcode/comments/5j4lp1/2016_day_19_solutions/dbdf50n/
(defn- play2
  "Play the game with part 2 rules"
  [target]
  (loop [i 1]
    (cond
      (< (* i 3) target) (recur (* i 3))
      :else              (- target i))))

(defn part-2
  "Day 19 Part 2"
  [input]
  (->> input
       u/to-lines
       first
       parse-long
       play2))
