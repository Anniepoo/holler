(ns holler.test.space
  (:use [holler.space] :reload-all)
  (:use [clojure.test]))

(deftest test-simple-make-space
  (is (= (set  (keys (defspace))) #{:dispatch :messages} )))

(deftest test-sending-a-message
  (def *test-results* "never set")
  (let [a-space (defspace)]
    (println "in the test")
    (add-listener a-space :foo "imadoodad"
     (fn [rcvr data]
      (def *test-results* (str  "in the test rcvr is " rcvr
       " and data is " data))))
    (holler a-space "imasender" :foo "imsomedata"))
  (is (= *test-results* "in the test rcvr is doodad and data is data" )))