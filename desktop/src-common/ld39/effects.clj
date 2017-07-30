(ns ld39.effects
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))



(defn modify-number [entity number amount]
  "Adds the given amount to the entity's number"
  (assoc entity number (-> (+ (number entity) amount) (min 15) (max 0))))

(defn move-to-tile [entities entity tile-x tile-y]
  "Moves the entity to the coordinates"
  (u/update-entities entities (u/is? entity)
                     (fn [e] (assoc e
                               :tile-x tile-x :tile-y tile-y
                               :tween-x (u/get-screen-x tile-x tile-y)
                               :tween-y (u/get-screen-y tile-x tile-y)))))



(defn consume [entities snake planet]
  "Adds the planet's energy and subtracts its defense from the snake's energy"
  (u/update-entities entities
    (u/is? snake)
    (fn [entity] (modify-number entity :energy (- (:energy planet) (:defense planet))))))



(defn activate [entities snake planet]
  "Performs the planet's effects"
  entities
  )



(defn move-snake [entities snake tile-x tile-y]
  "Move the snake to an empty space"
  (do (u/play-sound! "move.wav")
    (-> entities
        (move-to-tile snake tile-x tile-y)
        (u/update-entities (u/is? snake) (fn [e] (modify-number e :energy -1))))))



(defn interact [entities snake planet]
  "Haves the snake move to and eat the target planet"
  (do (u/play-sound! "consume.wav")
    (-> entities
        (move-to-tile snake (:tile-x planet) (:tile-y planet))
        (consume snake planet)
        (activate snake planet)
        (d/despawn planet))))
