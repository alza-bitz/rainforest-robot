(ns rainforest-robot.robot-test
  (:require [clojure.test :refer :all]
            [rainforest-robot.warehouse-test-utils :as utils]
            [rainforest-robot.robot :refer :all]))

(deftest create-test
  (is (=
        {:x 0 :y 0 :n 0 :h :rainforest-robot.robot/OK}
        (create 0 0))))

(deftest move-tests
  (testing "With broken robot"
           (is (=
                 [(utils/broken-robot 0 0 0) {}]
                 (perform 
                   (utils/broken-robot 0 0 0) 
                   (instruction-move :rainforest-robot.robot/NORTH)))))
  (testing "With direction north"
           (is (= 
                 [(utils/healthy-robot 0 1 0) {}] 
                 (perform 
                   (utils/healthy-robot 0 0 0)
                   (instruction-move :rainforest-robot.robot/NORTH)))))
  (testing "With direction south"
           (is (= 
                 [(utils/healthy-robot 0 -1 0) {}] 
                 (perform 
                   (utils/healthy-robot 0 0 0)
                   (instruction-move :rainforest-robot.robot/SOUTH)))))
  (testing "With direction east"
           (is (= 
                 [(utils/healthy-robot 1 0 0) {}] 
                 (perform 
                   (utils/healthy-robot 0 0 0)
                   (instruction-move :rainforest-robot.robot/EAST)))))
  (testing "With direction west"
           (is (= 
                 [(utils/healthy-robot -1 0 0) {}] 
                 (perform 
                   (utils/healthy-robot 0 0 0)
                   (instruction-move :rainforest-robot.robot/WEST))))))

(deftest pickup-bag-tests
  (testing "With broken robot"
           (is (=
                 [(utils/broken-robot 0 0 0) {:crate (utils/crate-with-one-bag 0 0)}]
                 (perform (utils/broken-robot 0 0 0) (instruction-pickup-bag (utils/crate-with-one-bag 0 0))))))
  (testing "With crate in same position but empty"
           (is (=
                 [(utils/healthy-robot 0 0 0) {:crate (utils/crate-with-no-bags 0 0)}]
                 (perform (utils/healthy-robot 0 0 0) (instruction-pickup-bag (utils/crate-with-no-bags 0 0))))))
  (testing "With crate in different position or no crates"
           (is (= 
                 [(utils/broken-robot 0 0 0) {:crate nil}]
                 (perform (utils/healthy-robot 0 0 0) (instruction-pickup-bag nil)))))
  (testing "With crate in same position and not empty"
           (is (=
                 [(utils/healthy-robot 0 0 1) {:crate (utils/crate-with-no-bags 0 0)}]
                 (perform (utils/healthy-robot 0 0 0) (instruction-pickup-bag (utils/crate-with-one-bag 0 0)))))))

(deftest drop-bags-tests
  (testing "With broken robot"
           (is (=
                 [(utils/broken-robot 0 0 0) {:belt (utils/belt-with-no-bags 0 0)}]
                 (perform (utils/broken-robot 0 0 0) (instruction-drop-bags (utils/belt-with-no-bags 0 0))))))
  (testing "With belt in same position but robot has no bags"
           (is (=
                 [(utils/healthy-robot 0 0 0) {:belt (utils/belt-with-no-bags 0 0)}]
                 (perform (utils/healthy-robot 0 0 0) (instruction-drop-bags (utils/belt-with-no-bags 0 0))))))
  (testing "With belt in different postion"
           (is (= [(utils/broken-robot 0 0 0) {:belt (utils/belt-with-no-bags 1 1)}]
                  (perform (utils/healthy-robot 0 0 1) (instruction-drop-bags (utils/belt-with-no-bags 1 1))))))
  (testing "With belt in same position and robot has bags"
           (is (= [(utils/healthy-robot 0 0 0) {:belt (utils/belt-with-one-bag 0 0)}]
                  (perform (utils/healthy-robot 0 0 1) (instruction-drop-bags (utils/belt-with-no-bags 0 0))))))
  )

