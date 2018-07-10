(ns rainforest-robot.warehouse
  (:require
    [rainforest-robot.belt :as belt]
    [rainforest-robot.robot :as robot]
    [rainforest-robot.crate :as crate]))

(defn create
  "Creates an empty warehouse"
  [] 
  {:crates []})

(defn has-belt?
  "Returns true if the warehouse has a belt placed"
  [warehouse]
  (contains? warehouse :belt))

(defn has-robot?
  "Returns true if the warehouse has a robot placed"
  [warehouse]
  (contains? warehouse :robot))

(defn make-report
  "Returns a formatted text report for the warehouse"
  [warehouse]
  {:pre [(has-belt? warehouse) (has-robot? warehouse)]}
  (let [{:keys [belt robot]} warehouse] 
    (clojure.string/join "\n" [(belt/make-report belt) (robot/make-report robot)])))

(defn crate-at
  "Returns the index of the crate at the given position if found, otherwise nil"
  [{:keys [crates]} x y]
  (first (keep-indexed 
           (fn [idx crate] (when 
                             (and (= x (:x crate)) (= y (:y crate))) idx)) crates)))

(defn place-belt
  "Places a belt on the warehouse floor"
   [warehouse x y]
   {:pre [(not (has-belt? warehouse)) (nil? (crate-at warehouse x y))]}
   (assoc warehouse :belt (belt/create x y)))

(defn place-robot
  "Places a robot on the warehouse floor"
  [warehouse x y]
  {:pre [(not (has-robot? warehouse))]}
  (assoc warehouse :robot (robot/create x y)))

(defn place-crate
  "Places a crate on the warehouse floor"
  [warehouse x y n]
  {:pre [(not (belt/belt-at? (:belt warehouse) x y)) (nil? (crate-at warehouse x y))]}
  (update warehouse :crates conj (crate/create x y n)))

(defn instruct-robot-move
  "Instruct the robot to move to a new position"
  [warehouse direction]
  {:pre [(has-robot? warehouse)]}
  (let [{:keys [robot]} warehouse
        [robot-updated _] (robot/perform robot (robot/instruction-move direction))]
    (assoc warehouse :robot robot-updated)))

(defn instruct-robot-pickup-bag
  "Instruct the robot to pick up a bag from a crate"
  [warehouse]
  {:pre [(has-robot? warehouse)]}
  (let [{:keys [robot crates]} warehouse
        crate-idx (crate-at warehouse (:x robot) (:y robot))
        crate (get crates crate-idx)
        [robot-updated {crate-updated :crate}] (robot/perform robot (robot/instruction-pickup-bag crate))
        warehouse-updated (assoc warehouse :robot robot-updated)]
    (if (not crate-idx) 
      warehouse-updated 
      (assoc-in warehouse-updated [:crates crate-idx] crate-updated))))

(defn instruct-robot-drop-bags
  [warehouse]
  "Instruct the robot to drop its bags on to the belt"
  {:pre [(has-robot? warehouse) (has-belt? warehouse)]}
  (let [{:keys [robot belt]} warehouse
        [robot-updated {belt-updated :belt}] (robot/perform robot (robot/instruction-drop-bags belt))]
    (assoc warehouse 
           :robot robot-updated
           :belt belt-updated)))

