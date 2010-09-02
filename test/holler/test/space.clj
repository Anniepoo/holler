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
  (println *test-results*))

(deftest test-sending-a-mass-o-messages
  (def *test-results* "never set")
  (let [a-space (defspace)]
    (println "in the mass test")
    (add-listener a-space :foo "imadoodad"
     (fn [rcvr data]
      (println   "in the test rcvr is " rcvr
		 " and data is " data)))
    (add-listener a-space :foo "seconddoodad"
		  (fn [rcvr data]
		    (println "sender " holler-sender "id " holler-id "rcvr " rcvr "data " data)
		    (holler a-space "seconddoodad" :bar "firsthalfofbar")
		    (holler a-space "seconddoodad" :bar "secondhalfofbar" )))
    (add-listener a-space :bar "thirddoodad"
		  (fn [rcvr data] (println "rcvr " rcvr " in the bar listener sees data " data " from " holler-sender)))
    (holler a-space "imasender" :foo "imsomedata")
    (println *test-results*))
  (is (= *test-results* "in the test rcvr is doodad and data is data" )))


(deftest test-sending-a-message-and-getting-a-callback
  (def *toast-results* "not yet set")
  (let [a-space (defspace)]
    (println "in the test")
    (add-listener a-space :foo "imadoodad"
     (fn [rcvr data]
      (def *toast-results* (str  "in the callback test rcvr is " rcvr
       " and data is " data))))
    (holler a-space "imasender" :foo "imsomedata" (println "in the callback toast-results is " *toast-results*)))
  (println *toast-results*))
