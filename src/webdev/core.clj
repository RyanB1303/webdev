(ns webdev.core
  (:require [webdev.item.model :as items]
            [webdev.item.handler :refer [handle-index-items
                                         handle-create-item
                                         handle-delete-item
                                         handle-update-item]])
  (:require [ring.adapter.jetty :as jetty]
            [ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.params :refer [wrap-params]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.middleware.file-info :refer [wrap-file-info]]
            [compojure.core :refer [defroutes ANY GET POST PUT DELETE]]
            [compojure.route :refer [not-found]]
            [ring.handler.dump :refer [handle-dump]]))

;; jdbc - database connection
(def db-spec
  (or (System/getenv "DATABASE_URL")
      {:dbtype "postgresql"
       :host "localhost"
       :port 5432
       :user "postgres"
       :password "postgres"
       :dbname "webdev"}))

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

(defroutes routes
  (GET "/" [] greet)
  (GET "/goodbye" [] goodbye)
  (GET "/about" [] about)
  (ANY "/request" [] handle-dump)
  (GET "/yo/:name" [] yo)
  (GET "/calc/:first_num/:operator/:second_num" [] calc)

  (GET "/items" [] handle-index-items)
  (POST "/items" [] handle-create-item)
  (DELETE "/items/:item-id" [] handle-delete-item)
  (PUT "/items/:item-id" [] handle-update-item)

  (not-found "Page not found."))

;; our own middleware
;; wrap db variable to parameter webdev/db
(defn wrap-db [handler]
  (fn [req]
    (handler (assoc req :webdev/db db-spec))))
;; wrap response headers
(defn wrap-headers [handler]
  (fn [req]
    (assoc-in (handler req) [:headers "Server"] "WebDev 79")))
;; wrap custom html method
(def sim-methods {"PUT" :put
                  "DELETE" :delete})

(defn wrap-simulated-method [handler]
  (fn [req]
    (if-let [method (and (= :post (:request-method req))
                         (sim-methods (get-in req [:params "_method"])))]
      (handler (assoc req :request-method method))
      (handler req))))

(def app
  (wrap-headers
   (wrap-file-info
    (wrap-resource
     (wrap-db
      (wrap-params
       (wrap-simulated-method
        routes)))
     "static"))))

(defn -main [port]
  (items/create-table db-spec)
  (jetty/run-jetty app
                   {:port (Integer. port)}))

(defn -dev-main [port]
  (items/create-table db-spec)
  (jetty/run-jetty (wrap-reload #'app) {:port (Integer. port)}))
