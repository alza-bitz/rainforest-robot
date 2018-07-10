(ns rainforest-robot.warehouse-test
  (:require [clojure.test :refer :all]
            [rainforest-robot.robot :as robot]
            [rainforest-robot.warehouse-test-utils :as utils]
            [rainforest-robot.warehouse :refer :all]))

(deftest create-tests
  (testing "With no belt and no robot and no crates"
           (is (= 
                 {:crates []}
                 (create)))))

(deftest place-belt-tests
  (testing "With belt not yet placed"
           (is (=
                 (utils/warehouse-with-belt 0 0)
                 (place-belt (utils/warehouse-empty) 0 0))))
  (testing "With belt already placed"
           (is (thrown? AssertionError
                        (place-belt (utils/warehouse-with-belt 0 0) 0 0))))
  (testing "With a crate in same position"
           (is (thrown? AssertionError
                        (place-belt (utils/warehouse-with-crate 0 0 1) 0 0)))))

(deftest place-crate-tests
  (testing "With no belt or crate in same position"
           (is (=
                 (utils/warehouse-with-crate 0 0 1)
                 (place-crate (utils/warehouse-empty) 0 0 1))))
  (testing "With belt in same position"
           (is (thrown? AssertionError
                        (place-crate {:belt (utils/belt-with-no-bags 0 0)} 0 0 1))))
  (testing "With a crate in same position"
           (is (thrown? AssertionError
                        (place-crate {:crates [(utils/crate-with-one-bag 0 0)]} 0 0 1)))))

(deftest place-robot-tests
  (testing "With robot not yet placed" 
           (is (= 
                 {:robot (utils/healthy-robot 0 0 0)} 
                 (place-robot {} 0 0))))
  (testing "With robot already placed"
           (is (thrown? AssertionError
                        (place-robot (utils/warehouse-with-robot 0 0) 0 0)))))

(deftest instruct-robot-move-tests
  (testing "With known direction"
           (is (=
                 {:robot (utils/healthy-robot 0 1 0)}
                 (instruct-robot-move {:robot (utils/healthy-robot 0 0 0)} ::robot/NORTH)))))

(deftest instruct-robot-pickup-bag-tests
  (testing "With crate in different position"
           (is (=
                 {:robot (utils/broken-robot 0 0 0)
                  :crates [(utils/crate-with-one-bag 1 1)]}
                 (instruct-robot-pickup-bag {:robot (utils/healthy-robot 0 0 0)
                                             :crates [(utils/crate-with-one-bag 1 1)]}))))
  (testing "With crate in same position"
           (is (=
                 {:robot (utils/healthy-robot 0 0 1)
                  :crates [(utils/crate-with-no-bags 0 0)]}
                 (instruct-robot-pickup-bag {:robot (utils/healthy-robot 0 0 0)
                                             :crates [(utils/crate-with-one-bag 0 0)]})))))

(deftest instruct-robot-drop-bags-tests
  (testing "With belt in different position"
           (is (=
                 {:belt (utils/belt-with-no-bags 1 1)
                  :robot (utils/broken-robot 0 0 0)}
                 (instruct-robot-drop-bags {:belt (utils/belt-with-no-bags 1 1)
                                            :robot (utils/healthy-robot 0 0 1)}))))
  (testing "With belt in same position"
           (is (=
                 {:belt (utils/belt-with-one-bag 0 0)
                  :robot (utils/healthy-robot 0 0 0)}
                 (instruct-robot-drop-bags {:belt (utils/belt-with-no-bags 0 0)
                                            :robot (utils/healthy-robot 0 0 1)})))))
