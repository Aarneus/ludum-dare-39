(ns ld39.data
  (:require [ld39.utils :as u]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))



(defn create-entity [x y frame-x frame-y]
  "Creates a sprite as an entity"
  (-> (u/create-sprite! "planets.png" x y 1 u/entity-size u/entity-size frame-x frame-y)))




(defn spawn [entities word x y]
  "Spawn a given entity"
  (conj entities
        (case word
          :snake (create-entity x y 0 4)
          :planet (create-entity x y 0 1)
          nil)))



