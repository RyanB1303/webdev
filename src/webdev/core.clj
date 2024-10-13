(ns webdev.core
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [compojure.core :refer [defroutes GET]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

(defn goodbye [_]
  {:status 200
   :body "Goodbye, good world!"
   :headers {}})

(defn greet [_]
  {:status 200
   :body "Hello, World!!!!"
   :headers {}})

(defn about [_]
  {:status 200
   :body "Hello, i'm a hobbyist programmer that somehow found lisp to make website."
   :headers {}})

(defn yo [req]
  (let [name (get-in req [:route-params :name])]
    {:status 200
     :body (str "Yo! " name "!")
     :headers {}}))

(def ops
  {"+" +
   "-" -
   "*" *
   ":" /})

(defn calc [req]
  (let [first_num (Integer. (get-in req [:route-params :first_num]))
        second_num (Integer. (get-in req [:route-params :second_num]))
        operator (get-in req [:route-params :operator])
        f (get ops operator)]
    (if f
      {:status 200
       :body (str first_num operator second_num "=" (f first_num second_num))
       :headers {}}
      {:status 404
       :body (str "Unknown operator: " operator)
       :headers {}})))

(defroutes app
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (GET "/about" [] about)
  (GET "/request" [] handle-dump)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:first_num/:operator/:second_num" [] calc)
  (not-found "Page not found."))

(defn -main [port]
  (jetty/run-jetty app
                   {:port (Integer. port)}))

(defn -dev-main [port]
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
