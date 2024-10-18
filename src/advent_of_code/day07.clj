(ns advent-of-code.day07
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- is-abba
  "Test if the given string `s` has an ABBA pattern"
  [s]
  (let [parts (partition 4 1 s)]
    (reduce (fn [_ [a b c d]]
              (if (and (= a d)
                       (= b c)
                       (not= a b))
                (reduced true)
                false))
            false
            parts)))

(defn- valid
  "Is the given IP string valid?"
  [ip]
  (let [parts (str/split ip #"[\[\]]")
        cycl  (cycle [false true])]
    (loop [[part & parts] parts, [inner & cycl] cycl, found 0]
      (if (nil? part)
        (pos? found)
        (if (is-abba part)
          (if inner
            (recur nil nil 0)
            (recur parts cycl (inc found)))
          (recur parts cycl found))))))

(defn part-1
  "Day 07 Part 1"
  [input]
  (->> input
       u/to-lines
       (filter valid)
       count))

(defn- find-aba
  "Find all aba-style sequences in `p` and add them to the set `s`"
  [p s]
  (let [parts (partition 3 1 p)]
    (reduce (fn [s' [a b c]]
              (if (and (= a c) (not= a b))
                (conj s' (str a b c))
                s'))
            s
            parts)))

(defn- match-found?
  "Look to see if any elements of `inner` have a counterpart in `outer`"
  [inner outer]
  (reduce (fn [_ [a b]]
            (if (outer (str b a b))
              (reduced true)
              false))
          false
          inner))

(defn- valid2
  "Is the given IP string valid for 'SSL'?"
  [ip]
  (let [parts (str/split ip #"[\[\]]")
        cycl  (cycle [false true])]
    (loop [[p & ps] parts, [inner & cycl] cycl, inner-set #{}, outer-set #{}]
      (if (nil? p)
        (match-found? inner-set outer-set)
        (if inner
          (recur ps cycl (find-aba p inner-set) outer-set)
          (recur ps cycl inner-set (find-aba p outer-set)))))))

(defn part-2
  "Day 07 Part 2"
  [input]
  (->> input
       u/to-lines
       (filter valid2)
       count))
