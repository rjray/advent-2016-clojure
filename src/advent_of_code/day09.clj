(ns advent-of-code.day09
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(def ^:private re-pat #"^([A-Z]*)(\(\d+x\d+\))?(.*)?$")

(defn- expand
  "Decompress the input based on the specs of the puzzle"
  [input]
  (let [start (re-matches re-pat input)]
    (loop [[_ lead segment other] start, parts ()]
      (cond
        ;; When `lead` is empty ("") and `segment` is nil, we're done
        (and (empty? lead)
             (nil? segment)) (apply str (reverse parts))
        ;; When `segment` is nil, only the text in `lead` remains
        (nil? segment)       (recur (list "" "") (cons lead parts))
        ;; Otherwise, process `segment` against `other` and recurse
        :else
        (let [[len cnt] (u/parse-out-longs segment)
              copy      (subs other 0 len)
              left      (subs other len)
              content   (reduce conj (list lead) (repeat cnt copy))]
          (recur (re-matches re-pat left) (concat content parts)))))))

(defn part-1
  "Day 09 Part 1"
  [input]
  (-> input
      (str/replace "\n" "")
      expand
      count))

(defn- expand2
  "Decompress the input based on 'version 2' of the compression spec"
  [input]
  (let [start (re-matches re-pat input)]
    (loop [[_ lead segment other] start, accum 0]
      (cond
        ;; When `lead` is empty ("") and `segment` is nil, we're done
        (and (empty? lead)
             (nil? segment)) accum
        ;; When `segment` is nil, only the text in `lead` remains
        (nil? segment)       (+ accum (count lead))
        ;; Otherwise, process `segment` against `other` and recurse
        :else
        (let [[len cnt] (u/parse-out-longs segment)
              copy      (subs other 0 len)
              left      (subs other len)
              length    (* cnt (expand2 copy))]
          (recur (re-matches re-pat left) (+ accum (count lead) length)))))))

(defn part-2
  "Day 09 Part 2"
  [input]
  (-> input
      (str/replace "\n" "")
      expand2))
