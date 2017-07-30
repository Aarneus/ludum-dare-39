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


(defn apply-effect [entities effect planet]
  "Applies the effect using the given planet as the source"
  (let [target (nth effect 1)
        number (nth effect 2)
        amount (nth effect 3)
        function (fn [e] (modify-number e number amount))]
    (case target
      :self (u/update-entities entities (u/is? planet) function)
      :chained (u/update-entities entities (u/chained-to? planet) function)
      entities)))




(defn activate [entities word planet]
  "Performs the planet's effects"
  (let [effects (filter (fn [e] (= word (first e))) (:effects planet))]
    (loop [f effects e entities]
      (if (empty? f) e
        (recur (rest f) (apply-effect e (first f) planet))))))


(defn activate-planets [entities word]
  "Performs all the planets' effects"
  (let [planets (filter (fn [p] (and (:effects p))) entities)]
    (loop [p planets e entities]
      (if (empty? p) e
        (recur (rest p) (activate e word (first p)))))))

(defn move-snake [entities snake tile-x tile-y]
  "Move the snake to an empty space"
  (do (u/play-sound! "move.wav")
    (-> entities
        (move-to-tile snake tile-x tile-y)
        (u/update-entities (u/is? snake) (fn [e] (modify-number e :energy -1))))))



(defn interact [entities snake planet]
  "Haves the snake move to and eat the target planet"
  (do
    (when (> (:energy planet) 0) (u/play-sound! "consume.wav"))
    (when (> (:defense planet) 0) (u/play-sound! "hit.wav"))
    (-> entities
        (move-to-tile snake (:tile-x planet) (:tile-y planet))
        (consume snake planet)
        (activate :eaten planet)
        (d/despawn planet))))
