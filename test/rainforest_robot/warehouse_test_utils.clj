(ns rainforest-robot.warehouse-test-utils
  (:require [rainforest-robot.belt :as belt]
           [rainforest-robot.robot :as robot]
           [rainforest-robot.crate :as crate]
           [rainforest-robot.warehouse :as warehouse]))

(defn warehouse-empty
  []
  (warehouse/create))

(defn warehouse-with-belt
  [x y]
  (assoc (warehouse/create) :belt (belt/create x y)))

(defn warehouse-with-robot
  [x y]
  (assoc (warehouse/create) :robot (robot/create x y)))

(defn warehouse-with-crate
  [x y n]
  (update (warehouse/create) :crates conj (crate/create x y n)))

(defn warehouse-with-two-crates
  [first second]
  (let [warehouse (warehouse-empty)]
    (-> warehouse
      (update :crates conj (apply crate/create first))
      (update :crates conj (apply crate/create second)))))

(defn belt-with-no-bags
  [x y]
  (belt/create x y))

(defn belt-with-one-bag
  [x y]
  (assoc (belt/create x y) :n 1))

(defn healthy-robot
  [x y n]
  (assoc (robot/create x y) :n n))

(defn broken-robot
  [x y n]
  (assoc (robot/create x y) 
         :n n
         :h ::robot/BROKEN))

(defn crate-with-no-bags
  [x y]
  (assoc (crate/create x y 1) :n 0))

(defn crate-with-one-bag
  [x y]
  (crate/create x y 1))
