(ns advent-of-code.day12
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- convert
  "Take the sequence of tokens and create code representation"
  [[op & args]]
  (let [ins  {:op (keyword op)}
        args (map #(if (re-matches #"-?\d+" %)
                     (parse-long %)
                     (keyword %)) args)]
    (case op
      ("inc" "dec") (assoc ins :target (first args))
      ("cpy" "jnz") (-> ins
                        (assoc :left (first args))
                        (assoc :right (last args))))))

(defn- to-code
  "Convert the lines to sequences of code instructions"
  [lines]
  (mapv convert (map #(str/split % #" ") lines)))

(defn- value
  "Return either the integer constant or the content of the register"
  [val reg]
  (if (integer? val) val (reg val)))

(defn- exec
  "Execute one program instruction"
  [ins pc reg]
  (case (:op ins)
    :inc (list (inc pc) (update reg (:target ins) inc))
    :dec (list (inc pc) (update reg (:target ins) dec))
    :cpy (list (inc pc) (assoc reg (:right ins) (value (:left ins) reg)))
    :jnz (let [val (value (:left ins) reg)
               off (if (zero? val) 1 (:right ins))]
           (list (+ pc off) reg))))

(defn- run
  "Run `program` until the PC goes past the end of memory"
  [program & [c]]
  (let [max-pc (count program)]
    (loop [pc 0, registers {:a 0, :b 0, :c (or c 0), :d 0}]
      (if (>= pc max-pc)
        registers
        (let [[pc' reg'] (exec (program pc) pc registers)]
          (recur pc' reg'))))))

(defn part-1
  "Day 12 Part 1"
  [input]
  (-> input
      u/to-lines
      to-code
      run
      :a))

(defn part-2
  "Day 12 Part 2"
  [input]
  (-> input
      u/to-lines
      to-code
      (run 1)
      :a))
