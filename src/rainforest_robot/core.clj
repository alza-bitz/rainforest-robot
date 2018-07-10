(ns rainforest-robot.core
  (:require [rainforest-robot.warehouse :as warehouse]
           [rainforest-robot.robot :as robot])
  (:gen-class))

(defn parse-belt
  "Attempt to parse a belt from the given line in belt format, 
  and then place the belt in the given warehouse.
  Throws an exception if the belt format is invalid"
  [warehouse line]
  (if-let [[_ x y] (re-matches #"^(-?\d+) (-?\d+)$" line)] 
    (warehouse/place-belt warehouse (Integer/parseInt x) (Integer/parseInt y))
    (throw (ex-info "Invalid belt format" {}))))

(defn parse-robot
  "Attempt to parse a robot from the given line in robot format, 
  and then place the robot in the given warehouse.
  Throws an exception if the robot format is invalid"
  [warehouse line]
  (if-let [[_ x y] (re-matches #"^(-?\d+) (-?\d+)$" line)] 
    (warehouse/place-robot warehouse (Integer/parseInt x) (Integer/parseInt y))
    (throw (ex-info "Invalid robot format" {}))))

(defn parse-crate
  "Attempt to parse a crate in crate format, 
  and then place the crate in the given warehouse.
  Throws an exception if the crate format is invalid"
  [warehouse crate]
  (if-let [[_ x y n] (re-matches #"^(-?\d+) (-?\d+) (\d+)$" crate)] 
    (warehouse/place-crate warehouse 
                           (Integer/parseInt x) (Integer/parseInt y) (Integer/parseInt n))
    (throw (ex-info "Invalid crate format" {}))))

(defn parse-crates
  "Attempt to parse one or more crates from the given line, 
  and then place these crates in the given warehouse"
  [warehouse line]
  (reduce parse-crate warehouse 
          (if (empty? line) [] (clojure.string/split line #", "))))

(def instructions
  {\N #(warehouse/instruct-robot-move %1 ::robot/NORTH)
   \S #(warehouse/instruct-robot-move %1 ::robot/SOUTH)
   \E #(warehouse/instruct-robot-move %1 ::robot/EAST)
   \W #(warehouse/instruct-robot-move %1 ::robot/WEST)
   \P warehouse/instruct-robot-pickup-bag
   \D warehouse/instruct-robot-drop-bags})

(defn parse-robot-instruction
  "Attempt to lookup a robot instruction from the given char, 
  and then carry out the instruction in the given warehouse.
  Throws an exception if the instruction format is invalid"
  [warehouse instruction-char]
  (if-let [instruction (instructions instruction-char)]
    (instruction warehouse)
    (throw (ex-info "Invalid instruction format" {}))))

(defn parse-robot-instructions
  "Attempt to parse one or more robot instructions from the given line,
  and then carry out these instructions in the given warehouse."
  [warehouse line]
  (reduce parse-robot-instruction warehouse (seq line)))

(defmacro nth-or
  "Returns the value at the index;
  otherwise (if the index is out of bounds), evaluates body in an implicit do"
  [coll index body]
  (list 'try (list 'nth coll index)
        (list 'catch 'IndexOutOfBoundsException 'e (list 'do body))))

(defn parse-from-lines
  "Attempt to parse belt, robot, crates and robot instructions from the given lines in text format
  Returns a report of the warehouse in text format"
  [lines]
  (-> (warehouse/create) 
    (parse-belt (nth-or lines 0 (throw (ex-info "Missing belt" {}))))
    (parse-robot (nth-or lines 1 (throw (ex-info "Missing robot" {}))))
    (parse-crates (nth-or lines 2 (throw (ex-info "Missing crates" {}))))
    (parse-robot-instructions (nth-or lines 3 (throw (ex-info "Missing robot instructions" {}))))
    (warehouse/make-report)))

(defn parse-from-file
  "Attempt to parse belt, robot, crates and robot instructions from the given file in text format
  Returns a report of the warehouse in text format"
  [filename]
  (with-open [rdr (clojure.java.io/reader filename)]
    (parse-from-lines (line-seq rdr))))

(defn -main
  "Attempt to parse belt, robot, crates and robot instructions from the given file in text format
  Prints a report of the warehouse in text format to stdout"
  [filename]
  (println (parse-from-file filename)))

