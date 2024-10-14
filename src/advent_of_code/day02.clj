(ns advent-of-code.day02
  (:require [advent-of-code.utils :as u]))

(def ^:private keypad [["1" "2" "3"]
                       ["4" "5" "6"]
                       ["7" "8" "9"]])

(def ^:private moves {\U [-1 0], \D [1 0], \L [0 -1], \R [0 1]})

(defn- get-digit-at
  "Get the keypad digit at the coordinate"
  [pad coord]
  (get-in pad coord))

(defn- move-one
  "Move one button in the given direction"
  [pad pos dir]
  (let [new-pos (map + pos (moves dir))]
    (if (get-digit-at pad new-pos) new-pos pos)))

(defn- find-button
  "Follow on line of movement commands to find the digit"
  [pad pos line]
  (reduce (partial move-one pad) pos line))

(defn- find-code
  "Derive the code from the lines of input on the given keypad"
  [pad start-at lines]
  (loop [[line & lines] lines, pos start-at, code ()]
    (if (nil? line)
      (apply str (map #(get-digit-at pad %) (reverse code)))
      (let [new-pos (find-button pad pos line)]
        (recur lines new-pos (cons new-pos code))))))

(defn part-1
  "Day 02 Part 1"
  [input]
  (->> input
       u/to-lines
       (map seq)
       (find-code keypad [1 1])))

(def ^:private keypad2 [[nil nil "1" nil nil]
                        [nil "2" "3" "4" nil]
                        ["5" "6" "7" "8" "9"]
                        [nil "A" "B" "C" nil]
                        [nil nil "D" nil nil]])

(defn part-2
  "Day 02 Part 2"
  [input]
  (->> input
       u/to-lines
       (map seq)
       (find-code keypad2 [2 0])))
