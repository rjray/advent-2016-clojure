(ns advent-of-code.day10
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(def ^:private line-re #"bot (\d+).*(bot|output) (\d+).*(bot|output) (\d+)")

(defn- add-to-bots
  "Parse the line and add a bot to `system`"
  [line system]
  (let [[value bot] (u/parse-out-longs line)]
    (update-in system [:bots bot] conj value)))

(defn- add-to-system
  "Parse the line and add an entry to `circuit` for the decision-point"
  [line system]
  (let [[_ bot-s lo-type lo-s hi-type hi-s] (re-matches line-re line)
        bot                                 (parse-long bot-s)
        lo                                  (parse-long lo-s)
        lo-type                             (keyword (str lo-type "s"))
        hi                                  (parse-long hi-s)
        hi-type                             (keyword (str hi-type "s"))]
    (-> system
        (assoc-in [:circuit bot] {:lo-type lo-type, :lo lo,
                                  :hi-type hi-type, :hi hi})
        (update-in [lo-type lo] concat ())
        (update-in [hi-type hi] concat ()))))

(defn- parse-lines
  "Parse the lines of input, separating assignment lines from structure lines"
  [lines]
  (loop [[line & lines] lines, system {:bots {}, :outputs {}, :circuit {}}]
    (if (nil? line)
      system
      (if (str/starts-with? line "value")
        (recur lines (add-to-bots line system))
        (recur lines (add-to-system line system))))))

(defn- system-done
  "Return true/false whether all bots are now empty-handed"
  [bots]
  (empty? (filter #(pos? (count %)) (vals bots))))

(defn- bot-found
  "Return the bot (if any) whose current holdings match `pred`"
  [bots pred]
  (ffirst (filter #(pred (last %)) bots)))

(defn- apply-all-bots
  "Update `system` to reflect hand-offs for any bots currently holding 2 chips"
  [system]
  (reduce (fn [sys [bot chips]]
            (let [[lo hi] (sort chips)
                  spec    (get-in sys [:circuit bot])]
              (-> sys
                  (assoc-in [:bots bot] ())
                  (update-in [(:lo-type spec) (:lo spec)] conj lo)
                  (update-in [(:hi-type spec) (:hi spec)] conj hi))))
          system
          (filter #(= 2 (count (last %))) (:bots system))))

(defn- find-bot
  "Walk through the circuit and start-values given. Find the bot for the chips."
  [c1 c2 system]
  (let [pred (fn [x] (or (= x (list c1 c2)) (= x (list c2 c1))))]
    (loop [system system]
      (if (system-done (:bots system))
        "No matching bot found"
        (if-let [found (bot-found (:bots system) pred)]
          found
          (recur (apply-all-bots system)))))))

(defn part-1
  "Day 10 Part 1"
  [input & [chip1 chip2]]
  (let [chip1 (or chip1 17)
        chip2 (or chip2 61)]
    (->> input
         u/to-lines
         parse-lines
         (find-bot chip1 chip2))))

(defn- find-product
  "Find the result of multiplying one value from bins 0, 1, and 2"
  [system]
  (loop [system system]
    (if (system-done (:bots system))
      (apply * (map #(first (get-in system [:outputs %])) (range 3)))
      (recur (apply-all-bots system)))))

(defn part-2
  "Day 10 Part 2"
  [input]
  (->> input
       u/to-lines
       parse-lines
       find-product))
