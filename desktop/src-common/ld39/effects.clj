(ns ld39.effects
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))


(defn move-to-tile [entity tile-x tile-y]
  "Moves the entity to the coordinates"
  (assoc entity :tile-x tile-x :tile-y tile-y
    :x (u/get-screen-x tile-x tile-y)
    :y (u/get-screen-y tile-x tile-y)))



(defn consume [entities snake planet]
  "Adds the planet's energy and subtracts its defense from the snake's energy"
  (u/update-entities entities
    (u/is? snake)
    (fn [entity] (assoc entity :energy
                   (-> (:energy entity)
                       (+ (:energy planet))
                       (- (:defense planet))
                       (min 15)
                       (max 0))))))







(defn activate [entities snake planet]
  "Performs the planet's effects"
  entities
  )





(defn interact [entities snake planet]
  "Haves the snake move to and eat the target planet"
  (-> entities
      (u/update-entities (u/is? snake) (fn [e] (move-to-tile e (:tile-x planet) (:tile-y planet))))
      (consume snake planet)
      (activate snake planet)))
