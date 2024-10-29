(ns advent-of-code.day21
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]
            [clojure.math.combinatorics :as comb]))

(defn- find-pos
  "Find the index of `c` within the vector `v`"
  [v c]
  (first (filter #(= (v %) c) (range (count v)))))

(defn- swap
  "Swap the elements at the two indices within `v`"
  [v [x y]]
  (-> v
      (assoc x (v y))
      (assoc y (v x))))

(defn- rotate
  "Rotate the vector `v` by `n` places in the direction of `dir`"
  [v n dir]
  (let [v'  (concat v v)
        cnt (count v)
        n'  (mod n cnt)]
    (vec (if (= dir :left)
           (take cnt (drop n' v'))
           (take cnt (drop (- cnt n') v'))))))

(defn- move
  "Move the character from index `x` to index `y`"
  [v [x y]]
  (let [ch (v x)
        v' (concat (subvec v 0 x) (subvec v (inc x)))]
    (vec (concat (take y v') [ch] (drop y v')))))

(def ^:private steps
  {:swapp   swap
   :swapl   (fn [p [x y]]
              (swap p [(find-pos p x) (find-pos p y)]))
   :rotl    (fn [p [x]]
              (rotate p x :left))
   :rotr    (fn [p [x]]
              (rotate p x :right))
   :rotb    (fn [p [x]]
              (let [x'  (find-pos p x)
                    x'' (if (>= x' 4) (+ x' 2) (inc x'))]
                (rotate p x'' :right)))
   :reverse (fn [p [x y]]
              (vec (concat (subvec p 0 x)
                           (reverse (subvec p x (inc y)))
                           (subvec p (inc y)))))
   :move    move})

(defn- parse-line
  "Parse the line of instructions into an actionable value"
  [line]
  (cond
    (str/starts-with? line "swap p")   {:op   :swapp
                                        :args (u/parse-out-longs line)}
    (str/starts-with? line "swap l")   {:op   :swapl
                                        :args (map (comp first seq last)
                                                   (re-seq #"letter (\w)"
                                                           line))}
    (str/starts-with? line "rotate l") {:op   :rotl
                                        :args (u/parse-out-longs line)}
    (str/starts-with? line "rotate r") {:op   :rotr
                                        :args (u/parse-out-longs line)}
    (str/starts-with? line "rotate b") {:op   :rotb
                                        :args (list (last line))}
    (str/starts-with? line "reverse")  {:op   :reverse
                                        :args (u/parse-out-longs line)}
    (str/starts-with? line "move")     {:op   :move
                                        :args (u/parse-out-longs line)}))

(defn- scramble
  "Scramble the given password based on the sequence of instructions"
  [pass inst]
  (reduce (fn [p {:keys [op args]}]
            ((steps op) p args))
          pass
          inst))

(defn part-1
  "Day 21 Part 1"
  [input & [passwd]]
  (let [passwd (or passwd "abcdefgh")]
    (->> input
         u/to-lines
         (map parse-line)
         (scramble (vec passwd))
         (apply str))))

(defn- unscramble
  "Find the unscrambled version of `pass` (brute force)"
  [pass inst]
  (reduce (fn [p perm]
            (if (= p (scramble (vec perm) inst))
              (reduced perm)
              p))
          pass
          (comb/permutations (sort pass))))

(defn part-2
  "Day 21 Part 2"
  [input & [passwd]]
  (let [passwd (or passwd "fbgdceah")]
    (->> input
         u/to-lines
         (map parse-line)
         (unscramble (vec passwd))
         (apply str))))
