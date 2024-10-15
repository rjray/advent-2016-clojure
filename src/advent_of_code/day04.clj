(ns advent-of-code.day04
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

;; A regexp to match each line of input and separate out the room letter-groups,
;; the ID, and the checksum.
(def ^:private line-re #"^([a-z]+(?:-[a-z]+)*)-(\d+)\[([a-z]{1,5})\]$")

(defn- sort-by-values
  "Sort the two given keys based on their values in the map `m`"
  [m a b]
  (let [va (m a), vb (m b)]
    (if (= va vb)
      (compare a b)
      (compare vb va))))

(defn- is-real
  "Determine if this room spec is a real room"
  [[spec _ chksum]]
  (let [stats  (frequencies (str/replace spec "-" ""))
        sorted (sort (partial sort-by-values stats) (keys stats))
        chkval (apply str (take 5 sorted))]
    (= chkval chksum)))

(defn part-1
  "Day 04 Part 1"
  [input]
  (->> input
       u/to-lines
       (map (partial re-matches line-re))
       (map rest)
       (filter is-real)
       (map (comp parse-long second))
       (apply +)))

(def ^:private lc-letters (map (comp char (partial + 97)) (range 26)))

(defn- decrypt
  "De-crypt the room name based on sector ID"
  [[name id]]
  (let [id (parse-long id), offset (mod id 26)]
    (loop [[ltr & ltrs] name, acc ()]
      (case ltr
        nil        (list (apply str (reverse acc)) id)
        \-         (recur ltrs (cons \space acc))
        (let [val (- (int ltr) 97)
              new (char (+ (mod (+ val offset) 26) 97))]
          (recur ltrs (cons new acc)))))))

(defn part-2
  "Day 04 Part 2"
  [input]
  (->> input
       u/to-lines
       (map (partial re-matches line-re))
       (map rest)
       (filter is-real)
       (map decrypt)
       (filter #(str/includes? (first %) "northpole"))
       last))
