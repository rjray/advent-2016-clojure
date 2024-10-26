(ns advent-of-code.day17
  (:require [advent-of-code.utils :as u]))

(def ^:private move-map {"U" [-1 0], "D" [1 0], "L" [0 -1], "R" [0 1]})

(defn- hash-code
  "Get the MD5 hash for the given pass-code and current path"
  [pass path]
  (u/md5 (reduce str pass path)))

(defn- get-moves
  "Get valid moves based on the hash-code"
  [hashcode]
  (let [door-open (for [x (take 4 hashcode)]
                    (case x
                      (\b \c \d \e \f) true
                      false))
        open-map  (zipmap ["U" "D" "L" "R"] door-open)]
    (filter open-map (keys open-map))))

(defn- is-valid?
  "Is the given location valid for this map?"
  [[x y]]
  (and (> x -1) (< x 4)
       (> y -1) (< y 4)))

(defn- valid-moves
  "Return only those moves that are valid from the current coordinates"
  [coord moves]
  (filter #(is-valid? (mapv + coord (move-map %))) moves))

(defn- find-path
  "Find the shortest path from (0,0) to (3,3) with conditional movement options"
  [pass]
  (let [get-hash (partial hash-code pass)
        queue    (into clojure.lang.PersistentQueue/EMPTY
                       [{:pos [0 0], :path []}])]
    (loop [queue queue]
      (let [node (peek queue), queue (pop queue)]
        (cond
          (nil? node)           "failed"
          (= (:pos node) [3 3]) (apply str (:path node))
          :else
          (let [{coord :pos
                 path  :path} node
                moves         (valid-moves coord (get-moves (get-hash path)))]
            (recur (into queue (for [x moves]
                                 {:pos  (mapv + coord (move-map x))
                                  :path (conj path x)})))))))))

(defn part-1
  "Day 17 Part 1"
  [input]
  (->> input
       u/to-lines
       first
       find-path))

(defn- find-longest-path
  "Find the length of the longest path from (0,0) to (3,3)"
  [pass]
  (let [get-hash (partial hash-code pass)
        queue    (into clojure.lang.PersistentQueue/EMPTY
                       [{:pos [0 0], :path []}])]
    (loop [queue queue, lens ()]
      (let [node (peek queue), queue (pop queue)]
        (cond
          (nil? node)           (reverse (sort lens))
          (= (:pos node) [3 3]) (recur queue (cons (count (:path node)) lens))
          :else
          (let [{coord :pos
                 path  :path} node
                moves         (valid-moves coord (get-moves (get-hash path)))]
            (recur (into queue (for [x moves]
                                 {:pos  (mapv + coord (move-map x))
                                  :path (conj path x)}))
                   lens)))))))

(defn part-2
  "Day 17 Part 2"
  [input]
  (->> input
       u/to-lines
       first
       find-longest-path
       first))
