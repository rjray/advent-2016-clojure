(ns advent-of-code.day08
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- parse-line
  "Parse one line of instructions into a simple structure"
  [line]
  (let [[command parts] (str/split line #"\s+" 2)]
    (case command
      "rect"   {:command :rect,
                :size (vec (u/parse-out-longs parts))}
      "rotate" (let [[which length] (u/parse-out-longs parts)]
                 {:command :rotate,
                  :along (if (str/starts-with? parts "row") :row :col)
                  :index which
                  :length length}))))

(defn- fill
  "Fill in a rectangle in `field`. Always rooted at (0, 0)"
  [field [x y]]
  (loop [[row & rows] field, cur-y 0, result []]
    (cond
      (nil? row)  result
      (= cur-y y) (recur nil y (vec (apply concat (conj result row) [rows])))
      :else       (recur rows (inc cur-y) (conj result
                                                (vec (concat (repeat x \#)
                                                             (drop x row))))))))

(defn- rotate-real
  "Rotate a single vector by `length` steps"
  [vector length]
  (let [vlen  (count vector)
        len   (mod length vlen)
        pivot (- vlen len)]
    (if (zero? pivot)
      vector
      (vec (apply concat (subvec vector pivot) [(subvec vector 0 pivot)])))))

(defn- rotate
  "Rotate line `index` along `along` for `length` steps"
  [field along index length]
  (case along
    :row (assoc field index (rotate-real (get field index) length))
    :col (let [field'  (u/transpose field)
               new-col (rotate-real (get field' index) length)]
           (u/transpose (assoc field' index new-col)))))

(defn- light-up
  "Apply the sequence of lighting commands to `field`"
  [field commands]
  (reduce (fn [f cmd]
            (case (:command cmd)
              :rect   (fill f (:size cmd))
              :rotate (let [{along :along, index :index, length :length} cmd]
                        (rotate f along index length))))
          field
          commands))

(defn- count-lights
  "Count the lit-up cells in `field`"
  [field]
  (reduce + (map {\# 1, \. 0} (apply concat field))))

(defn part-1
  "Day 08 Part 1"
  [input & [max-y max-x]]
  (let [max-y (or max-y 6)
        max-x (or max-x 50)
        field (u/create-field max-x max-y \.)]
    (->> input
         u/to-lines
         (map parse-line)
         (light-up field)
         count-lights)))

(defn part-2
  "Day 08 Part 2"
  [input & [max-y max-x]]
  (let [max-y (or max-y 6)
        max-x (or max-x 50)
        field (u/create-field max-x max-y \.)]
    (->> input
         u/to-lines
         (map parse-line)
         (light-up field)
         u/display)))
