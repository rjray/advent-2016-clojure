(ns advent-of-code.day14
  (:require [advent-of-code.utils :as u]))

(defn- md5sums
  "Generate the infinite sequence of MD5 sums with their indices"
  [salt]
  (map #(list % (u/md5 (str salt %))) (range)))

(defn- add-match
  "Add a 3-char match to the cache"
  [entry [_ key] cache]
  (update cache key conj entry))

(defn- find-new-otps
  "Find any 3-char matches for the 5-char keys in `five-matches`"
  [five-matches idx cache]
  (let [base (- idx 1000)]
    (reduce (fn [acc key]
              (concat acc (filter #(<= base (first %)) (cache key))))
            ()
            (map last five-matches))))

(defn- find-otps
  "Create the stream of matching MD5 hashes"
  [md-func cnt salt]
  (loop [[[idx hash] & hashes] (md-func salt), otps #{}, cache {}]
    (if (<= cnt (count otps))
      (take cnt (sort-by first otps))
      (if-let [five-matches (re-seq #"([0-9a-f])\1\1\1\1" hash)]
        ;; If a hash has data in `five-matches`, look backwards for a 3-match
        ;; for any of the 5-matches that were found.
        (let [cache' (add-match (list idx hash) (first five-matches) cache)
              otps'  (apply conj otps (find-new-otps five-matches idx cache))]
          (recur hashes otps' cache'))
        ;; If not, check for a 3-match
        (if-let [three-match (re-find #"([0-9a-f])\1\1" hash)]
          ;; A 3-match is added to the cache, but nothing else is done. Later,
          ;; if/when a matching 5-match occurs we'll look in `cache` for a
          ;; matching previous 3-match.
          (recur hashes otps (add-match (list idx hash) three-match cache))
          (recur hashes otps cache))))))

(defn part-1
  "Day 14 Part 1"
  [input]
  (->> input
       u/to-lines
       first
       (find-otps md5sums 64)
       last))

(defn- gen-one-sum
  "Generate one 2016-iteration checksum"
  [start]
  (reduce (fn [chksum _] (u/md5 chksum)) start (range 2017)))

(defn- md5sums2016
  "Generate the infinite sequence of sums, but each one is run 2017 total times"
  [salt]
  (map #(list % (gen-one-sum (str salt %))) (range)))

(defn part-2
  "Day 14 Part 2"
  [input]
  (->> input
       u/to-lines
       first
       (find-otps md5sums2016 64)
       last))
