(ns rainforest-robot.crate)

(defn create
  "Creates a crate with the specified position and number of bags"
  [x y n]
  {:pre [(number? n) (< 0 n)]}
  {:x x :y y :n n})

(defn has-bags?
  "Returns true if the crate has one or more bags"
  [crate]
  (not (zero? (:n crate))))

(defn remove-bag
  "Remove one bag from the crate"
  [crate]
  {:pre [(has-bags? crate)]}
  (update crate :n dec))