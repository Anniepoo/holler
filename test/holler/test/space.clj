(ns holler.test.space
  (:use [holler.space] :reload-all)
  (:use [clojure.test]))

(deftest test-simple-make-space
  (is (= (set  (keys  (defspace))) #{:dispatch :doodads :messages} )))

