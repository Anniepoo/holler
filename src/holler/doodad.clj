(ns holler.doodad)					;

;==================================
(defn add-holler-raw [space-ref msg]
  (dosync
    (alter space-ref #(assoc % :msg-queue (cons msg (:msg-queue %))))
    )
  )

(defn add-listener
  "add a handler, created by listen, to a space.
This actually queues a holler "
  [the-space-ref the-doodad [the-listen-id the-listen-handler]]
; TODO - make sure add-holler is documented as internal use only
; because none of the binds are set up here  
  (add-holler-raw the-space-ref
	       [:add-listen [the-doodad the-listen-id the-listen-handler]]))


(defn defdoodad
  "Return a new doodad. Doodads are objects in the holler system.
is is the object id, space is a reference to the space, and must point
at a valid space, state is the initial state of the system, and listens
is the message handlers"
  [id space state & listens]
  (let [me {
	  :def [id space state listens]	; only for changing space
	  :space space			; ro access ref to our space
	  :state (ref  state)		; internal state info
	    }]
    (add-doodad space me)
    (map #(add-istener space me %) listens)))