(ns rainforest-robot.belt)

(defn create
  "Creates a belt with the specified position and number of bags"
  [x y]
  {:x x :y y :n 0})

(defn make-report
  "Returns a formatted text report for the belt"
  [belt]
  (str (:n belt)))

(defn add-bags
  "Add a number of bags to the belt"
  [belt n]
  (update belt :n #(+ % n)))

(defn belt-at?
  "Returns true if the belt is at the given position"
  [belt x y]
  (and (= x (:x belt)) (= y (:y belt))))