(ns advent-of-code.day01
  (:require [advent-of-code.utils :as u]))

;; Mapping current-facing to new-facing based on movement command
(def ^:private dirs {:N {:L :W, :R :E},
                     :E {:L :N, :R :S},
                     :S {:L :E, :R :W},
                     :W {:L :S, :R :N}})

(defn- dir-and-steps
  "Extract all non-whitespace, non-digit content, followed by digits"
  [input]
  (map #(list (keyword (first %)) (parse-long (last %)))
       (map rest (re-seq #"([LR])(\d+)" input))))

(defn- move
  "Move `steps` in the specified direction from the passed-in point"
  [dir steps [from-y from-x]]
  (case dir
    :N (list (+ from-y steps) from-x)
    :E (list from-y (+ from-x steps))
    :S (list (- from-y steps) from-x)
    :W (list from-y (- from-x steps))))

(defn- trace-walk
  "Trace the path of the given directions"
  [start-pos walk]
  (reduce (fn [[cur-dir pos] [turn steps]]
            (let [dir (get-in dirs [cur-dir turn])]
              (list dir (move dir steps pos))))
          start-pos
          walk))

(defn part-1
  "Day 01 Part 1"
  [input]
  (->> input
       dir-and-steps
       (trace-walk (list :N [0 0]))
       last
       (u/manhattan-dist [0 0])))

(defn- trace-walk2
  "Create a sequence of steps from the starting point over the walk"
  [start-pos walk]
  (let [[start-dir pos] start-pos]
    (loop [[w & ws] walk, cur-dir start-dir, pos pos, path ()]
      (if (nil? w)
        (reverse path)
        (let [[turn steps] w
              dir          (get-in dirs [cur-dir turn])]
          (recur ws dir (move dir steps pos)
                 (reduce (fn [p dist]
                           (cons (move dir dist pos) p))
                         path
                         (range 1 (inc steps)))))))))

(defn part-2
  "Day 01 Part 2"
  [input]
  (->> input
       dir-and-steps
       (trace-walk2 (list :N [0 0]))
       u/first-duplicate
       (u/manhattan-dist [0 0])))
