(ns ld39.data
  (:require [ld39.utils :as u]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))


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
          :chain-range chain-range :effects effects
          :chain-cooldown (if chain-range 0 nil) :chains []))))

(defn spawn! [entities word x y]
  "Spawn a given entity"
  (let [entity (case word
                 :snake (assoc (create-token! x y 1 0 10 nil nil nil) :player? true)
                 :plain (create-token!
                          x y  0 2  2 1  1 ())
                 :armed (create-token!
                          x y  0 0  3 1  1
                          (list (list :tick :chained :defense 1)))
                 nil)]
    (-> entities
        (conj entity)
        (create-numbers! entity))))

(defn despawn [entities entity]
  "Destroys the entity and any entities that have it as owner"
  (map (fn [e] (if (or ((u/is? entity) e)
                       (= (:id entity) (:owner-id e))
                       (= (:id entity) (:chained-1 e))
                       (= (:id entity) (:chained-2 e)))
                 nil e)) entities))


(defn -get-p [word x y tx ty]
  "Returns the attributes for the wanted particle type"
   (let [chain-life (u/calculate-particle-life x y tx ty)
         angle (- (u/radian-to-degree (Math/atan2 (- ty y) (- tx x))) 90)
         particle {:x (- x u/half-font-size) :y (- y u/half-font-size)
                  :tween-x (- tx u/half-font-size) :tween-y (- ty u/half-font-size)
                   :z 0.5 :angle angle
                  :frame-x 12 :frame-y 0
                  :life u/particle-life :tween-speed u/tween-speed}]
     (case word
       :blue-arrow (assoc particle :life chain-life)
       :red-arrow (assoc (-get-p :blue-arrow x y tx ty) :frame-x 13)
       :green-arrow (assoc (-get-p :blue-arrow x y tx ty) :frame-x 14)
       :yellow-arrow (assoc (-get-p :blue-arrow x y tx ty) :frame-x 15)
       :blue-line (assoc (-get-p :blue-arrow x y tx ty) :frame-y 1)
       :red-line (assoc (-get-p :blue-line x y tx ty) :frame-x 13)
       :green-line (assoc (-get-p :blue-line x y tx ty) :frame-x 14)
       :yellow-line (assoc (-get-p :blue-line x y tx ty) :frame-x 15)
       nil)))


(defn spawn-particle! [entities word x y target-x target-y chained-1 chained-2]
  "Spawns a given particle"
  (let [p (-get-p word x y target-x target-y)]
    (u/create-particle! entities
                        (:x p) (:y p) (:z p) (:angle p) (:frame-x p) (:frame-y p)
                        (:tween-x p) (:tween-y p)(:life p) (:tween-speed p)
                        chained-1 chained-2)))
