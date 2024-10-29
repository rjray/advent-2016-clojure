(ns advent-of-code.day22
  (:require [advent-of-code.utils :as u]
            [clojure.math.combinatorics :as comb]))

(defn- to-df-map
  "Make the map structure of the df data"
  [data]
  (reduce (fn [acc [x y size used avail]]
            (assoc acc [x y] {:size size, :used used, :avail avail}))
          {}
          data))

(defn- all-pairs
  "Generate a seq of all (directed) pairs of keys from `df-map`"
  [df-map]
  (mapcat #(list % (reverse %)) (comb/combinations (keys df-map) 2)))

(defn- viable
  "Determine if the pair is viable"
  [df-map [dfA dfB]]
  (and (pos? (:used (df-map dfA)))
       (<= (:used (df-map dfA)) (:avail (df-map dfB)))))

(defn- find-viable-pairs
  "Find the viable pairs in `df-map`"
  [df-map]
  (let [viable? (partial viable df-map)]
    (filter viable? (all-pairs df-map))))

(defn part-1
  "Day 22 Part 1"
  [input]
  (->> input
       u/to-lines
       (drop 2)
       (map u/parse-out-longs)
       to-df-map
       find-viable-pairs
       count))

(defn- draw-map
  "Take the given data and create the 'map'"
  [df-map]
  (let [max-x  (apply max (map first (keys df-map)))
        max-y  (apply max (map last (keys df-map)))
        me     [0 0]
        goal   [max-x 0]
        pairs  (find-viable-pairs df-map)
        zero   (last (first pairs))
        _ (prn zero)
        usable (set (map first pairs))]
    (partition (inc max-x)
               (for [y (range (inc max-y))
                     x (range (inc max-x))
                     :let [node [x y]]]
                 (cond
                   (= me node)   \O
                   (= goal node) \G
                   (= zero node) \_
                   (usable node) \.
                   :else         \#)))))

(defn part-2
  "Day 22 Part 2"
  [input]
  (->> input
       u/to-lines
       (drop 2)
       (map u/parse-out-longs)
       to-df-map
       draw-map
       u/display))
