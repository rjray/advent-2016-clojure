(ns advent-of-code.day11
  (:require [advent-of-code.utils :as u]
            [clojure.set :as set]
            [clojure.math.combinatorics :as comb]))

(def ^:private line-re #"(?:([a-z]+) generator|([a-z]+)-compatible)")

(defn- setup-floor
  "Take the parsing result from one floor and create a struct for it"
  [floor]
  (loop [[item & items] floor, struct {:microchips #{}, :generators #{}}]
    (if (nil? item)
      struct
      (let [[_ generator microchip] item]
        (recur items (if (nil? generator)
                       (update-in struct [:microchips] conj microchip)
                       (update-in struct [:generators] conj generator)))))))

(defn- setup-system
  "Create the representation of the initial microchips/generators configuration"
  [floors & [xtras]]
  (let [base-data  (mapv setup-floor (map #(re-seq line-re %) floors))
        floor-data (if xtras
                     (-> base-data
                         (update-in [0 :microchips] set/union xtras)
                         (update-in [0 :generators] set/union xtras))
                     base-data)]
    {:state floor-data
     :total (apply + (map #(count (:microchips %)) floor-data))}))

(defn- floor-safe?
  "Predicate to determine if a floor-configuration would be safe"
  [{:keys [generators microchips]}]
  (cond
    ;; If there are no generators, any number of microchips is OK
    (zero? (count generators)) true
    ;; Otherwise any/all microchips must have their corresponding generator
    :else
    (empty? (filter (complement identity) (map generators microchips)))))

(defn- safe-state?
  "Predicate to determine if the current state is safe"
  [state]
  (reduce (fn [_ floor]
            (if (not (floor-safe? floor))
              (reduced false)
              true))
          true
          state))

(defn- is-completed?
  "Predicate to determine if `state` represents a completion of the puzzle"
  [total state]
  (let [{microchips :microchips, generators :generators} (last state)]
    (= total (count microchips) (count generators))))

(defn- floor-content
  "Determine the content of the current floor in a format for comb/subsets"
  [floor]
  (let [set-to-vec (fn [[kw s]] (map #(list kw %) s))]
    (apply concat (map set-to-vec floor))))

(defn- safe-option?
  "Is the given option of items to take on the elevator together safe?"
  [options]
  (cond
    (= 1 (count options))         true
    (apply = (map first options)) true
    (apply = (map last options))  true
    :else                         false))

(defn- floor-options
  "Generate the possible options for using the elevator from the current floor"
  [floor]
  (filter safe-option?
          (rest (filter #(<= (count %) 2)
                        (comb/subsets (floor-content floor))))))

(defn- move-elevator
  "Calculate the new state that comes from moving between floors with `items`"
  [state items from to]
  (let [from-floor (reduce (fn [fl [k v]] (update fl k disj v))
                           (state from)
                           items)
        to-floor   (reduce (fn [fl [k v]] (update fl k conj v))
                           (state to)
                           items)]
    (-> state
        (assoc from from-floor)
        (assoc to to-floor))))

(defn- target-floors
  "Return a list of floors reachable from `floor`"
  [floor]
  (case floor
    0 (list 1)
    3 (list 2)
    ;; else
    (list (dec floor) (inc floor))))

(defn- solve
  "Solve the puzzle from the given starting configuration"
  [{:keys [state total]}]
  (let [complete?   (partial is-completed? total)
        start-state [{:state state, :floor 0, :steps 0}]
        queue       (into clojure.lang.PersistentQueue/EMPTY start-state)]
    (loop [queue queue, seen #{}, lengths (), depth 0]
      (let [node (peek queue), queue (pop queue)]
        (if (nil? node)
          {:length (apply min lengths), :nodes (count seen), :depth depth}
          (let [{state :state
                 steps :steps
                 floor :floor} node]
            (cond
              (seen [floor state])      (recur queue seen lengths depth)
              (complete? state)         (recur queue seen (cons steps lengths)
                                               (max steps depth))
              :else
              (let [possible-moves (comb/cartesian-product
                                    (target-floors floor)
                                    (floor-options (state floor)))
                    new-moves      (reduce (fn [acc [fl' items]]
                                             (let [move (move-elevator
                                                         state items floor fl')]
                                               (if (safe-state? move)
                                                 (cons {:state move
                                                        :steps (inc steps)
                                                        :floor fl'} acc)
                                                 acc)))
                                           ()
                                           possible-moves)]
                (recur (into queue new-moves)
                       (conj seen [floor state])
                       lengths
                       (max steps depth))))))))))

(defn part-1
  "Day 11 Part 1"
  [input]
  (-> input
      u/to-lines
      setup-system
      solve))

(defn part-2
  "Day 11 Part 2"
  [input]
  (-> input
      u/to-lines
      (setup-system (set (list "elerium" "dilithium")))
      solve))
