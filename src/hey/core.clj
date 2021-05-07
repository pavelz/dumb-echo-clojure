(ns hey.core
  (:gen-class))


(require '[clojure.java.io :as io])
(import '[java.net ServerSocket])

(defn receive
  "Read a line of textual data from the given socket"
  [socket]
  (.readLine (io/reader socket)))

(defn send
  "Send the given string message out over the given socket"
  [socket msg]
  (let [writer (io/writer socket)]
      (.write writer msg)
      (.flush writer)))

(defn serve [port handler]
  (with-open [server-sock (ServerSocket. port)
              sock (.accept server-sock)]
    (loop [condit true]
      (do
        (let [msg-in (receive sock)
              msg-out (handler msg-in)]
          (send sock msg-out))
        (recur ()
        ))
    )
  )
)

(defn handle [msg]
  (do
    (println msg)
    "hello from clojure!"
    )
  )

(defn messenger
  ([]     (messenger "Hello world!"))
  ([msg]  (println msg)))

(defn -main 
  "I don't do a whole lot ... yet."
  [& args]
  (messenger "heyhhoio\n")
  (loop [condition true]
    (do
      (serve 8000 handle)
      (recur ())
      )
    )
  )
