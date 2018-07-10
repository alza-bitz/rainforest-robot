(ns rainforest-robot.core-test
  (:require [clojure.test :refer :all]
            [rainforest-robot.warehouse-test-utils :as utils]
            [rainforest-robot.core :refer :all]))

(deftest parse-valid-belt-tests
  (testing "With positive components"
           (is (=
                 (utils/warehouse-with-belt 1 2)
                 (parse-belt (utils/warehouse-empty) "1 2"))))
  (testing "With negative components"
           (is (=
                 (utils/warehouse-with-belt -1 -2)
                 (parse-belt (utils/warehouse-empty) "-1 -2")))))

(deftest parse-invalid-belt-tests
  (testing "With no components (empty)"
           (is (thrown-with-msg? 
                 Exception #"Invalid belt format"
                 (parse-belt (utils/warehouse-empty) ""))))
  (testing "With no components (whitespace)"
         (is (thrown-with-msg? 
               Exception #"Invalid belt format"
               (parse-belt (utils/warehouse-empty) "   "))))
  (testing "With missing component"
           (is (thrown-with-msg? 
                 Exception #"Invalid belt format"
                 (parse-belt (utils/warehouse-empty) "0"))))
  (testing "With non-numeric component"
           (is (thrown-with-msg? 
                 Exception #"Invalid belt format"
                 (parse-belt (utils/warehouse-empty) "x y"))))
  (testing "With too many components"
           (is (thrown-with-msg? 
                 Exception #"Invalid belt format"
                 (parse-belt (utils/warehouse-empty) "0 1 2")))))

(deftest parse-valid-robot-tests
  (testing "With positive components"
           (is (=
                 (utils/warehouse-with-robot 1 2)
                 (parse-robot (utils/warehouse-empty) "1 2"))))
  (testing "With negative components"
           (is (=
                 (utils/warehouse-with-robot -1 -2)
                 (parse-robot (utils/warehouse-empty) "-1 -2")))))

(deftest parse-valid-crates-tests
  (testing "With no crates"
          (is (=
                 (utils/warehouse-empty)
                 (parse-crates (utils/warehouse-empty) ""))))
  (testing "With one crate"
           (is (=
                 (utils/warehouse-with-crate 1 2 3)
                 (parse-crates (utils/warehouse-empty) "1 2 3"))))
  (testing "With two crates"
           (is (=
                 (utils/warehouse-with-two-crates [1 2 3] [4 5 6])
                 (parse-crates (utils/warehouse-empty) "1 2 3, 4 5 6")))))

(deftest parse-invalid-crates-tests
  (testing "With one crate that is missing components"
           (is (thrown-with-msg? 
                 Exception #"Invalid crate format"
                 (parse-crates (utils/warehouse-empty) "1 2"))))
  (testing "With two crates and second crate is missing components"
           (is (thrown-with-msg? 
                 Exception #"Invalid crate format"
                 (parse-crates (utils/warehouse-empty) "1 2 3, 4 5")))))

(deftest parse-valid-robot-instructions-tests
  (testing "With no instructions"
           (is (=
                 (utils/warehouse-with-robot 1 2)
                 (parse-robot-instructions (utils/warehouse-with-robot 1 2) ""))))
  (testing "With one instruction"
           (is (=
                 (utils/warehouse-with-robot 1 3)
                 (parse-robot-instructions (utils/warehouse-with-robot 1 2) "N"))))
  (testing "With two instructions"
           (is (=
                 (utils/warehouse-with-robot 2 3)
                 (parse-robot-instructions (utils/warehouse-with-robot 1 2) "NE")))))

(deftest parse-invalid-robot-instructions-tests
  (testing "With one instruction that is invalid"
           (is (thrown-with-msg? 
                 Exception #"Invalid instruction format"
                 (parse-robot-instructions (utils/warehouse-with-robot 1 2) "X"))))
  (testing "With two instructions and second instruction is invalid"
           (is (thrown-with-msg? 
                 Exception #"Invalid instruction format"
                 (parse-robot-instructions (utils/warehouse-with-robot 1 2) "N ")))))

(deftest parse-from-lines-tests
  (testing "With no lines"
           (is (thrown-with-msg? 
                 Exception #"Missing belt"
                 (parse-from-lines []))))
  (testing "With belt line"
           (is (thrown-with-msg? 
                 Exception #"Missing robot"
                 (parse-from-lines ["0 0"]))))
  (testing "With belt line and robot line"
           (is (thrown-with-msg? 
                 Exception #"Missing crates"
                 (parse-from-lines ["0 0" "0 0"]))))
  (testing "With belt line and robot line and crate line"
           (is (thrown-with-msg? 
                 Exception #"Missing robot instructions"
                 (parse-from-lines ["0 0" "0 0" "0 1 1"]))))
  
  (testing "With all lines and no crates and no robot instructions"
           (is (= 
                 "0\n0 0 OK"
                 (parse-from-lines ["0 0" "0 0" "" ""]))))
  (testing "With all lines and one crate and no robot instructions"
           (is (= 
                 "0\n0 0 OK"
                 (parse-from-lines ["0 0" "0 0" "0 1 1" ""]))))
  (testing "With all lines and one crate and robot instructions"
           (is (= 
                 "3\n0 2 OK"
                 (parse-from-lines ["0 2" "0 0" "0 1 10" "NPPPND"]))))
  (testing "With all lines and two crates and robot instructions"
           (is (= 
                 "3\n0 2 OK"
                 (parse-from-lines ["0 2" "0 0" "0 1 10, -1 -2 5" "NPPPND"])))))

(deftest parse-from-file-tests
  (testing "With file that exists"
           (is (=
                 "3\n0 2 OK"
                 (parse-from-file (clojure.java.io/resource "instructions.txt")))))
  (testing "With file that does not exist"
           (is (thrown? Exception
                 (parse-from-file (clojure.java.io/resource "missing.txt"))))))

(deftest sample-tests
  (testing "With sample test 1 input"
           (is (=
                 "0\n0 1 BROKEN"
                 (parse-from-lines ["1 1" "0 1" "0 0 10" "PNNEE"]))))
  (testing "With sample test 2 input"
           (is (=
                 "5\n0 3 OK"
                 (parse-from-lines ["0 5" "0 1" "0 1 3, 1 3 3" "PPPPENNPPWNNDSS"]))))
  (testing "With sample test 3 input"
           (is (=
                 "1\n-2 -1 BROKEN"
                 (parse-from-lines ["-2 -2" "0 0" "-1 -1 2" "SWPSWDNDN"])))))

