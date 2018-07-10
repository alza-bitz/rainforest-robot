(ns rainforest-robot.robot
  (:require [rainforest-robot.belt :as belt]
           [rainforest-robot.crate :as crate]))

(def move-delta
  "Table of delta movements based on direction"
  {::NORTH { :dx  0  :dy  1 }
   ::EAST  { :dx  1  :dy  0 }
   ::SOUTH { :dx  0  :dy -1 }
   ::WEST  { :dx -1  :dy  0 }})

(defn create
  "Creates a robot with the specified position"
  [x y]
  {:x x :y y :n 0 :h ::OK})

(defn ok?
  "Returns true if the robot is ok"
  [robot]
  (= (:h robot) ::OK))

(defn broken?
  "Returns true if the robot is broken"
  [robot]
  (= (:h robot) ::BROKEN))

(defn broken
  "Renders the robot broken"
  [robot]
  (assoc robot :h ::BROKEN))

(defn make-report
  "Returns a formatted text report for the robot"
  [{:keys [x y n h]}]
  (clojure.string/join " " [x y (name h)]))

(defn has-bags?
  "Returns true if the robot has one or more bags"
  [robot]
  (not (zero? (:n robot))))

(defmacro when-ok
  "When the robot is healthy, evaluates body in an implicit do; 
  otherwise, returns the robot and any crate or belt from the instruction unmodified"
  [robot instruction body]
  (list 'if (list ok? robot)
        (list 'do body)
        (vector robot (list 'select-keys instruction (vector :crate :belt)))))

(defn instruction-move
  "Creates a 'move' instruction for the specified direction"
  [direction]
  {:type ::move :direction direction})

(defn instruction-pickup-bag
  "Creates a 'pickup-bag' instruction for the specified crate"
  [crate]
  {:type ::pickup-bag :crate crate})

(defn instruction-drop-bags
  "Creates a 'drop-bags' instruction for the specified belt"
  [belt]
  {:type ::drop-bags :belt belt})

(defmulti perform
  "Instructs the robot to perform the instruction specified by the instruction type"
  (fn [robot instruction] (:type instruction)))

(defmethod perform ::move
  [robot {:keys [direction] :as instruction}]
  {:pre [(contains? move-delta direction)]}
  (when-ok robot instruction
           [(assoc robot 
                 :x (+ (:x robot) (:dx (move-delta direction)))
                 :y (+ (:y robot) (:dy (move-delta direction)))) {}]))

(defmethod perform ::pickup-bag
  [robot {:keys [crate] :as instruction}]
  (when-ok robot instruction
           (cond
             (not crate) [(broken robot) {:crate crate}]
             (not (crate/has-bags? crate)) [robot {:crate crate}]
             true [(update robot :n inc) {:crate (crate/remove-bag crate)}])))

(defmethod perform ::drop-bags
  [robot {:keys [belt] :as instruction}]
  (when-ok robot instruction 
           (let [robot-updated (assoc robot :n 0)]
             (cond
               (not (belt/belt-at? belt (:x robot) (:y robot))) [(broken robot-updated) {:belt belt}]
               (not (has-bags? robot)) [robot {:belt belt}]
               true [robot-updated {:belt (belt/add-bags belt (:n robot))}]))))


