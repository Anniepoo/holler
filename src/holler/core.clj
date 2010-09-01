(ns holler.core)



;==================================
(defn add-holler-raw [space-ref msg]
  (dosync
    (alter space-ref #(assoc % :msg-queue (cons msg (:msg-queue %))))
    )
  )
