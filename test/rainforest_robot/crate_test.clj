(ns rainforest-robot.crate-test
  (:require [clojure.test :refer :all]
            [rainforest-robot.crate :refer :all]))

(deftest create-tests
  (testing "With valid number of bags" 
           (is (= 
                 {:x 0 :y 0 :n 1} 
                 (create 0 0 1))))
  (testing "With invalid number of bags" 
           (is (thrown? AssertionError 
                        (create 0 0 0)))))