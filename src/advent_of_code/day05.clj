(ns advent-of-code.day05
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- candidate
  "Predicate to see if an ID+index MD5 checksum is a candidate for the password"
  [sum]
  (str/starts-with? sum "00000"))

(defn- get-pw-char
  "Get the password character from the validated checksum"
  [sum]
  (nth sum 5))

(defn- generate-sums
  "Generate the infinite sequence of MD5 sums"
  [id]
  (map #(u/md5 (str id %)) (range)))

(defn part-1
  "Day 05 Part 1"
  [input]
  (->> input
       u/to-lines
       first
       generate-sums
       (filter candidate)
       (take 8)
       (map get-pw-char)
       (apply str)))

(defn- fill-in
  "Fill in one slot of the password from the hash, if that slot is still empty"
  [pw h]
  (let [pos (Integer/parseInt (str (nth h 5)) 16)
        val (nth h 6)]
    (if (and (< pos 8)
             (nil? (pw pos)))
      (assoc pw pos val)
      pw)))

(defn- find-pw
  "Find the password according to part 2 of the puzzle"
  [s]
  (loop [[h & hs] s, pw (vec (repeat 8 nil))]
    (if (not-any? nil? pw)
      pw
      (recur hs (fill-in pw h)))))

(defn part-2
  "Day 05 Part 2"
  [input]
  (->> input
       u/to-lines
       first
       generate-sums
       (filter candidate)
       find-pw
       (apply str)))
