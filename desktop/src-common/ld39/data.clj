(ns ld39.data
  (:require [ld39.utils :as u]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))



(defn create-token [tile-x tile-y frame-x frame-y]
  "Creates a token as a sprite"
  (let [x (u/get-screen-x tile-x tile-y)
        y (u/get-screen-y tile-x tile-y)]
    (-> (u/create-sprite! "planets.png"  x y 1
                          u/token-size u/token-size
                          frame-x frame-y)
        (assoc :token? true :tile-x tile-x :tile-y tile-y))))




(defn spawn [entities word x y]
  "Spawn a given entity"
  (conj entities
        (case word
          :snake (assoc (create-token x y 1 0)
                   :player? true)
          :planet (create-token x y 0 1)
          nil)))



