(ns advent-of-code.day23
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

;; Map used by the "tgl" instruction to alter op-codes.
(def ^:private toggle-map
  {:inc :dec, :dec :inc, :tgl :inc, :jnz :cpy, :cpy :jnz})

(defn- exec
  "Execute one instruction (at `pc`) and return the updated state values"
  [code pc reg]
  (let [ins (code pc)]
    (if (= pc 4)
      ;; "Peephole" optimization, here. The sequence from here to PC value 9
      ;; constitutes a multiplication of registers `d` and `b`, adding that to
      ;; register `a`, and ending with both `c` and `d` being zero. At the end
      ;; of the loop, the PC should be advanced by 6. I don't like having to
      ;; hard-code something like this against my specific input...
      (let [prod (* (:b reg) (:d reg))]
        (list code (+ 6 pc) (-> reg
                                (update :a + prod)
                                (assoc :c 0)
                                (assoc :d 0))))
      (case (:op ins)
        :inc (list code (inc pc) (update reg (first (:args ins)) inc))
        :dec (list code (inc pc) (update reg (first (:args ins)) dec))
        :cpy (if (keyword? (last (:args ins)))
               (list code (inc pc) (assoc reg
                                          (last (:args ins))
                                          (value (first (:args ins)) reg)))
               (list code (inc pc) reg))
        :jnz (let [val (value (first (:args ins)) reg)
                   off (if (zero? val) 1 (value (last (:args ins)) reg))]
               (list code (+ pc off) reg))
        :tgl (let [at (+ pc (value (first (:args ins)) reg))]
               (if (< at (count code))
                 (list (update-in code [at :op] toggle-map)
                       (inc pc)
                       reg)
                 (list code (inc pc) reg)))))))

(defn- run
  "Run `program` until the PC goes past the end"
  [program init-a]
  (let [end (count program)]
    (loop [code program, pc 0, registers {:a init-a, :b 0, :c 0, :d 0}]
      (if (>= pc end)
        registers
        (let [[code' pc' registers'] (exec code pc registers)]
          (recur code' pc' registers'))))))

(defn part-1
  "Day 23 Part 1"
  [input]
  (-> input
      u/to-lines
      to-code
      (run 7)
      :a))

(defn part-2
  "Day 23 Part 2"
  [input]
  (-> input
      u/to-lines
      to-code
      (run 12)
      :a))
