(import java.lang.Thread)

(def foo (agent 0))

(defn ricardo [a] (println "pilot"))

(defn george [a] (dorun (do (send foo ricardo) (println "hi") (Thread/sleep 5000) (println "bye"))))

; (send foo george)

(def bar (agent 0))

(defn pym [a] (dorun (do (send bar ricardo) (println "hi") (Thread/sleep 5000) (println "bye"))))

(send foo pym)