(ns holler.space)

(defn defspace
  "returns a new space"
  []
  (let [space   ; yes yes for now it's not needed but it will be
	{
	 :dispatch (ref  {})	 ; map from id's to lists of listeners
	 ; each listener is [doodad handler-fn] 
; not needed yet	 :doodads   (ref '()) ; list of doodads. When you remove, remove the listeners
	 :messages  (ref '()) ; message queue. each msg is [sender id data]
	 }
	]
    space))

(def holler-sender nil)

(def holler-id nil)

(defn- dispatch-msg
  "directly dispatch a message to a listener. You probably
don't want to do this"
  [[doodad handler-fn :as listen] [sender id data :as msg]]
  (binding [holler-sender sender
	    holler-id id]
    (if (= doodad sender)
      nil
      (apply handler-fn doodad data))))

(defn- process-a-msg
  "you don't want this, you want process-queue or send-msg"
  [{dispatch-table :dispatch} [sender id data :as msg]]
  (let [listeners (id @dispatch-table)]
    (if listeners
      (map #(dispatch-msg % msg) listeners ))
    )
  )

(defn- pop-ref
  "given a ref, assume it points at a seq. Atomicly alter the ref
to contain the rest, returning the first"
  [ref]
  (dosync
   (let [ a  (first @ref)
	 _  (alter ref pop)]
     a)))


; removes the first message from the queue
; process it, and do this until the queue is empty, because
; processing may add things.
(defn- process-queue
  "process as many messages as possible from the queue.
probably not what you're looking for"
  [space]
  (loop
  ; this can't be done with map because we explicitly need to allow
      ; the queue to be altered during execution
	; the mutable messages queue. see below
      (let [m (pop-ref (:messages space))]
	(if (not m)
	  nil
	  (do
	    (process-a-msg space m)
	    (recur))))))

(defn- add-msg
  "Add a message to the end of the message queue of a space.
Usage: space - space to broadcast message in
sender - the sending doodad, or nil
id  - the id of the message
data - the body of the message"
  [space sender id data]
    (dosync
     (alter (:messages space)
	    (fn [q]
	      (into q [[sender id data]]))))
    (process-queue space))

(defn holler
  "broadcast a message into this space."
  [space sender id data]
  (add-msg space sender id data))

(defn add-listener
  "add a listener."
  [space id receiver handler]
  (dosync
   (alter (:dispatch space)
	  (fn [disp]
	    (assoc disp id (cons [receiver handler] (id disp)))))))

(defn remove-listener
  "remove all listeners on a single id for a single receiver"
   [space id receiver]
  (dosync
   (alter (:dispatch space)
	  (fn [disp]
	    (assoc disp id (filter #(=r receiver (first %)) (id disp)))))))

