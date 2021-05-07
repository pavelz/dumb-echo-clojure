(ns hey.core
  (:gen-class))


(require '[clojure.java.io :as io])
(require '[clj-postgresql.core :as pg])
(require '[clojure.java.jdbc :as jdbc])

(def db2 (delay(pg/pool :host "localhost" :user "pavel" :dbname "insta-go" :password "321pavel")))

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
  (let [running [atom true]]
    (future
      (with-open [server-sock (ServerSocket. port) ]
        (while @running
          (with-open [sock (.accept server-sock)]
            (loop []
              (do
                (println "READ")
                (let [msg-in (receive sock)
                      msg-out (handler msg-in)]
                  (send sock msg-out))
                (recur)
                )
              )
            )
          )
        )
      )
    )
  )

(defn serve-persistent [port handler]
  (let [running (atom true)]
    (future
      (with-open [server-sock (ServerSocket. port)]
        (while @running
          (with-open [sock (.accept server-sock)]
            (let [msg-in (receive sock)
                  msg-out (handler msg-in)]
              (send sock msg-out))))))
    running))

(defn handle 
  "send messages received back"
  [msg]
  (do
    (println msg)
    (str msg "\n")
    )
  )

(defn messenger
  ([]     (messenger "Hello world!"))
  ([msg]  (println msg)))

(defn -main 
  "I don't do a whole lot ... yet."
  [& args]
  (messenger "Remote SQL qurey runner\n")

  (let [result (jdbc/query @db2 ["SELECT 1"])]
    (prn result)
  )

      (def a (serve 8888 #(.toUpperCase %)))

  )
