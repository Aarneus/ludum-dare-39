(ns ld39.data
  (:require [ld39.utils :as u]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))



(defn create-token [tile-x tile-y frame-x frame-y energy defense chain-range effects]
  "Creates a token as a sprite"
  (let [x (u/get-screen-x tile-x tile-y)
        y (u/get-screen-y tile-x tile-y)]
    (-> (u/create-sprite! "planets.png"  x y 1
                          u/token-size u/token-size
                          frame-x frame-y)
        (assoc :token? true
          :tile-x tile-x :tile-y tile-y
          :energy energy :defense defense
          :chain-range chain-range :effects effects))))


(defn create-number! [entities x y flip? word owner colour]
  (if (some? (word owner))
    (conj entities
          (-> (u/create-sprite! "font.png" x y 2 u/font-size u/font-size colour 1)
              (assoc :number? true :owner-id (:id owner) :word word :colour colour :flip? flip?)))
    entities))

(defn create-numbers [entities entity]
  "Creates the number sprites for the entity"
  (-> entities
      (create-number! 0 0 true :energy entity (if (:player? entity) 0 1))
      (create-number! 10 10 false :defense entity 2)))


(defn spawn [entities word x y]
  "Spawn a given entity"
  (let [entity (case word
                 :snake (assoc (create-token x y 1 0 5 nil nil nil) :player? true)
                 :plain (create-token x y 0 1 3 0 2 [])
                 nil)]
    (-> entities
        (conj entity)
        (create-numbers entity))))


(defn animate [entities]
  (->> entities
       ; Numbers
       (map (fn [entity]
              (if (:number? entity)
                (let [owner (u/get-entity-by-id entities (:owner-id entity))
                      value ((:word entity) owner)]
                  (-> entity
                      (u/set-frame (:colour entity) value)
                      (assoc
                        :x (+ (:x owner) (if (:flip? entity) u/number-flip-offset 0))
                        :y (:y owner))))
                  entity)))))
