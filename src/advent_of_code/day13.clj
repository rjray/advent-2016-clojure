(ns advent-of-code.day13
  (:require [advent-of-code.utils :as u]))

(defn- wall-fn
  "Function to determine if the given (y, x) is a wall or open"
  [seed [x y]]
  (or (neg? x)
      (neg? y)
      (let [val  (+ seed (* x x) (* 3 x) (* 2 x y) y (* y y))
            bits (frequencies (Integer/toString val 2))]
        (odd? (bits \1)))))

(def ^:private valid-moves [[-1 0] [0 1] [1 0] [0 -1]])

(defn- gen-moves
  "Generate the 4 possible move positions"
  [pos]
  (reduce (fn [ms mv] (cons (mapv + pos mv) ms))
          () valid-moves))

(defn- find-steps
  "Find the minimum steps from (1, 1) to the target (x, y)"
  [seed target]
  (let [wall? (memoize (partial wall-fn seed))
        open? (complement wall?)
        start [{:pos [1 1], :steps 0}]
        queue (into clojure.lang.PersistentQueue/EMPTY start)]
    (loop [queue queue, seen #{}]
      (let [node (peek queue), queue (pop queue)]
        (cond
          (nil? node)            "failed"
          (= (:pos node) target) (:steps node)
          :else
          (let [{pos   :pos
                 steps :steps} node
                viable-moves   (filter #(not (seen %))
                                       (filter open? (gen-moves pos)))
                actual-moves   (map #(hash-map :pos %, :steps (inc steps))
                                    viable-moves)]
            (recur (into queue actual-moves) (conj seen pos))))))))

(defn part-1
  "Day 13 Part 1"
  [input & [[x y]]]
  (let [x (or x 31), y (or y 39)]
    (-> input
        u/to-lines
        first
        parse-long
        (find-steps [x y]))))

(defn- find-locs
  "Find the number of visited (open) locations within `dist` steps"
  [seed & [dist]]
  (let [wall? (memoize (partial wall-fn seed))
        open? (complement wall?)
        start [{:pos [1 1], :steps 0}]
        queue (into clojure.lang.PersistentQueue/EMPTY start)]
    (loop [queue queue, seen #{}]
      (let [node (peek queue), queue (pop queue)]
        (cond
          (nil? node)            (count seen)
          (> (:steps node) dist) (recur queue seen)
          :else
          (let [{pos   :pos
                 steps :steps} node
                viable-moves   (filter #(not (seen %))
                                       (filter open? (gen-moves pos)))
                actual-moves   (map #(hash-map :pos %, :steps (inc steps))
                                    viable-moves)]
            (recur (into queue actual-moves) (conj seen pos))))))))

(defn part-2
  "Day 13 Part 2"
  [input & [dist]]
  (let [dist (or dist 50)]
    (-> input
        u/to-lines
        first
        parse-long
        (find-locs dist))))
