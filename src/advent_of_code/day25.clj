(ns advent-of-code.day25
  (:require [advent-of-code.utils :as u]
            [clojure.string :as str]))

(defn- convert
  "Take the sequence of tokens and create code representation"
  [[op & args]]
  (let [op   (keyword op)
        args (map #(if (re-matches #"-?\d+" %)
                     (parse-long %)
                     (keyword %)) args)]
    {:op op, :args args}))

(defn- to-code
  "Convert the lines to sequences of code instructions"
  [lines]
  (mapv convert (map #(str/split % #" ") lines)))

(defn- value
  "Return either the integer constant or the content of the register"
  [val reg]
  (if (integer? val) val (reg val)))

(defn- exec
  "Execute one instruction (at `pc`) and return the updated state values"
  [code pc reg out]
  (let [ins (code pc)]
    (case pc
      3
      ;; "Peephole" optimization, here. The sequence from here to PC value 7
      ;; constitutes a multiplication of registers `c` and `b`, adding that to
      ;; register `d`, and ending with both `c` and `b` being zero. At the end
      ;; of the loop, the PC should be advanced by 5. I don't like having to
      ;; hard-code something like this against my specific input... again.
      (let [prod (* (:b reg) (:c reg))]
        (list (+ 5 pc) (-> reg
                           (update :d + prod)
                           (assoc :c 0)
                           (assoc :b 0)) out))
      ;; Thought I would have to do a second one, but I managed without it.
      (case (:op ins)
        :inc (list (inc pc) (update reg (first (:args ins)) inc) out)
        :dec (list (inc pc) (update reg (first (:args ins)) dec) out)
        :cpy (if (keyword? (last (:args ins)))
               (list (inc pc) (assoc reg
                                     (last (:args ins))
                                     (value (first (:args ins)) reg)) out)
               (list (inc pc) reg out))
        :jnz (let [val (value (first (:args ins)) reg)
                   off (if (zero? val) 1 (value (last (:args ins)) reg))]
               (list (+ pc off) reg out))
        :out (list (inc pc) reg (cons (value (first (:args ins)) reg) out))))))

(defn- run
  "Run the code with an initial value of `a` in register :a. Return `n`
  values."
  [code init-a n]
  (loop [pc 0, registers {:a init-a, :b 0, :c 0, :d 0}, out ()]
    (cond
      (nil? out)        false
      (= n (count out)) true
      :else
      (let [[pc' registers' out'] (exec code pc registers out)]
        (if (and (> (count out) 1) (apply = (take 2 out)))
          (recur pc' registers' nil)
          (recur pc' registers' out'))))))

(defn- find-val
  "Find the needed positive integer"
  [code]
  (loop [[a & as] (iterate inc 1)]
    (if (run code a 50) a (recur as))))

(defn part-1
  "Day 25 Part 1"
  [input]
  (->> input
       u/to-lines
       to-code
       find-val))

(defn part-2
  "Day 25 Part 2"
  [input]
  "Congrats! You should have all 50 stars by now!")
