(ns ld39.data
  (:require [ld39.utils :as u]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))



(defn create-token [tile-x tile-y frame-x frame-y energy defense]
  "Creates a token as a sprite"
  (let [x (u/get-screen-x tile-x tile-y)
        y (u/get-screen-y tile-x tile-y)]
    (-> (u/create-sprite! "planets.png"  x y 1
                          u/token-size u/token-size
                          frame-x frame-y)
        (assoc :token? true
          :tile-x tile-x :tile-y tile-y
          :energy energy :defense defense))))


(defn create-number! [entities x y flip? word owner]
  (if (some? (word owner))
    (conj entities
          (-> (u/create-sprite! "font.png" x y 2 u/font-size u/font-size 0 1)
              (assoc :number? true :owner-id (:id owner) :word word :flip? flip?)))
    entities))

(defn create-numbers [entities entity]
  "Creates the number sprites for the entity"
  (-> entities
      (create-number! 0 0 true :energy entity)
      (create-number! 10 10 false :defense entity)))

(defn spawn [entities word x y]
  "Spawn a given entity"
  (let [entity (case word
                 :snake (assoc (create-token x y 1 0 5 nil) :player? true)
                 :planet (create-token x y 0 1 3 4)
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
                      (u/set-frame 0 value)
                      (assoc
                        :x (+ (:x owner) (if (:flip? entity) u/number-flip-offset 0))
                        :y (:y owner))))
                  entity)))))
