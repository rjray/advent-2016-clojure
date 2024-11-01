(ns advent-of-code.day24
  (:require [advent-of-code.utils :as u]
            [clojure.math.combinatorics :as comb]))

(defn- make-field
  "Create the maze/field from the lines of the input"
  [lines]
  (let [y (count lines)
        x (count (first lines))]
    (u/create-field x y lines)))

(defn- find-points
  "Find the numbered points in the field"
  [field]
  (let [max-y  (count field)
        max-x  (count (first field))]
    (mapv last
          (sort-by first (for [y (range max-y), x (range max-x)
                               :let [ch (get-in field [y x])]
                               :when (not (or (= \. ch) (= \# ch)))]
                           [ch [y x]])))))

(def ^:private valid-moves [[-1 0] [0 1] [1 0] [0 -1]])

(defn- gen-moves
  "Generate the 4 possible move positions"
  [pos]
  (reduce (fn [ms mv] (cons (mapv + pos mv) ms))
          () valid-moves))

(defn- get-dist
  "Find the distance between `p1` and `p2` in `field` using BFS"
  [field [p1 p2]]
  (let [valid?      (fn [p] (not= (get-in field p) \#))
        start-state [p1 0]
        queue       (into clojure.lang.PersistentQueue/EMPTY [start-state])]
    (loop [queue queue, seen #{}]
      (let [node (peek queue), queue (pop queue)]
        (if (nil? node)
          ;; No path found
          0
          (let [[point steps] node]
            (cond
              (seen point) (recur queue seen)
              (= point p2) steps
              :else
              (let [moves (filter valid? (gen-moves point))]
                (recur (into queue (map #(vector % (inc steps)) moves))
                       (conj seen point))))))))))

(defn- find-weights
  "Get all the 'weights' for the edges, by doing BFS for each pair"
  [field points edges]
  (reduce (fn [weights edge]
            (if-let [dist (get-dist field (map points edge))]
              (assoc weights edge dist)
              weights))
          {}
          edges))

(defn- build-system
  "Use the field (map) to create the full system"
  [field]
  (let [points  (find-points field)
        edges   (map set (comb/combinations (range (count points)) 2))
        weights (find-weights field points edges)]
    {:points  points
     :edges   edges
     :weights weights}))

(defn- calc-cost
  "Calculate the cost of one permutation"
  [p weights]
  ;;(prn p (apply + (map weights (map set (partition 2 1 p)))))
  (apply + (map weights (map set (partition 2 1 p)))))

(defn- find-shortest-brute
  "Find the shortest total path with a brute-force approach, If `back` is not
  false, loop the path back to point 0."
  [{:keys [points weights]} & [back]]
  (let [cores (comb/permutations (range 1 (count points)))
        perms (if back
                (map #(concat [0] % [0]) cores)
                (map #(cons 0 %) cores))]
    (apply min (map #(calc-cost % weights) perms))))

(defn part-1
  "Day 24 Part 1"
  [input]
  (->> input
       u/to-lines
       make-field
       build-system
       find-shortest-brute))

(defn part-2
  "Day 24 Part 2"
  [input]
  (-> input
      u/to-lines
      make-field
      build-system
      (find-shortest-brute true)))
