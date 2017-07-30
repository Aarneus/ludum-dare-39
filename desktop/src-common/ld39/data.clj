(ns ld39.data
  (:require [ld39.utils :as u]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))



(defn create-token! [tile-x tile-y frame-x frame-y energy defense chain-range effects]
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

(defn create-numbers! [entities entity]
  "Creates the number sprites for the entity"
  (-> entities
      (create-number! 0 0 true :energy entity (if (:player? entity) 0 1))
      (create-number! 10 10 false :defense entity 2)))

(defn spawn! [entities word x y]
  "Spawn a given entity"
  (let [entity (case word
                 :snake (assoc (create-token! x y 1 0 5 nil nil nil) :player? true)
                 :plain (create-token! x y 0 1 3 0 2 [])
                 nil)]
    (-> entities
        (conj entity)
        (create-numbers! entity))))



(defn -get-particle [word x y target-x target-y]
  "Returns the attributes for the wanted particle type"
   (let [particle {:x x :y y
                  :angle 0
                  :frame-x 12 :frame-y 0
                  :tween-x target-x :tween-y target-y
                  :life u/particle-life :tween-speed u/tween-speed}]
     (case word
       :blue-arrow (assoc particle :tween-speed 0.05 :life 20)
       :red-arrow (assoc particle :tween-speed 0.05 :life 20 :frame-y 1)
       :green-arrow (assoc particle :tween-speed 0.05 :life 20 :frame-y 2)
       :yellow-arrow (assoc particle :tween-speed 0.05 :life 20 :frame-y 3)
       nil)))


(defn spawn-particle! [entities word x y target-x target-y]
  "Spawns a given particle"
  (let [p (-get-particle word x y target-x target-y)]
    (u/create-particle! entities
                        (:x p) (:y p) (:angle p) (:frame-x p) (:frame-y p)
                        (:tween-x p) (:tween-y p)(:life p) (:tween-speed p))))



(defn tween-token [entity tween word]
  "Animates the entities with a basic tween"
  (let [t (tween entity)
        w (word entity)]
  (if (some? t)
    (assoc entity word (+ w (* (- t w) (:tween-speed entity))))
    entity)))


(defn animate [entities]
  "Animates all entities including particles and numbers"
  (->> entities
       ; Particles
       (map (fn [entity]
              (if (:particle? entity)
                (if (> (:life entity) 0)
                  (assoc entity
                    :life (- (:life entity) 1))
                  nil)
                entity)))

       ; Tweens
       (map (fn [entity]
              (loop [w u/tween-keywords e entity]
                (if (empty? w) e
                  (recur (pop (pop w)) (tween-token e (peek w) (peek (pop w))))))))


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
