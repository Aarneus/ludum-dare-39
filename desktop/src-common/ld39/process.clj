(ns ld39.process
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [clojure.data :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))



(defn remove-dead [entities]
  (let [removed (filter (fn [e] (and (:energy e) (= 0 (:energy e)))) entities)]
    (loop [r removed e entities]
      (if (empty? r) e (recur (rest r) (d/despawn e (first r)))))))



(defn -get-planets-in-range [entities planet]
  "Returns a list of the id:s of the planets in chain range of the given one"
  (filter some? (->> entities
                     (map (fn [e]
                            (if (and
                                  (not= (:id e) (:id planet))
                                  (some? (:chain-range e))
                                  (<= (u/get-token-distance e planet)
                                      (+ (:chain-range e) (:chain-range planet))))
                              (:id e)
                              nil))))))


(defn -destroy-chain [planet chained entities]
  (do
    (u/play-sound! "break.wav")
    (assoc planet :chains (remove (fn [c] (= c chained)) (:chains planet)))))

(defn -create-chain [planet chained entities]
  (assoc planet :chains (conj (:chains planet) chained)))


(defn -apply-chains [planet function chains entities]
  "Applies the chain function to the given planet"
  (if (some? chains)
    (loop [p planet c (filter some? chains)]
      (if (empty? c) p
        (recur (function p (first c) entities) (rest c))))
    planet))


(defn -update-planet-chains [planet entities ]
  "Updates this planet's chains (updates only this planet!)"
  (let [in-range (-get-planets-in-range entities planet)
        diffs (diff (:chains planet) in-range)
        cut-chains (nth diffs 0)
        new-chains (nth diffs 1)]
    (-> planet
        (-apply-chains -create-chain new-chains entities)
        (-apply-chains -destroy-chain cut-chains entities))))

(defn update-chains [entities]
  "Groups all planets together into chained networks"
  (->> entities
       (map (fn [e]
              (if (nil? (:chain-range e)) e (-update-planet-chains e entities))))))



(defn animate-chain [entities planet chain]
  "Sends a particle from the planet to the target chained planet"
  (let [target (u/get-entity-by-id entities chain)
        src-x (-> (:x planet) (+ u/half-token-size))
        src-y (-> (:y planet) (+ u/half-token-size))
        dst-x (-> (:x target) (+ u/half-token-size))
        dst-y (-> (:y target) (+ u/half-token-size))]
    (if (some? (:particle planet))
      (if (<= (:chain-cooldown planet) 0)
        (-> entities
            (d/spawn-particle! (:particle planet) src-x src-y dst-x dst-y (:id planet) chain)
            (u/update-entities (u/is? planet)
                               (fn [e] (assoc e :chain-cooldown
                                         (/ (u/calculate-particle-life src-x src-y dst-x dst-y) u/chain-interval)))))
        entities)
      entities)))



(defn animate-chains [entities]
  "Fires line particles between chained planets"
  (let [planets (remove nil? (map (fn [e] (if (:chain-range e) e nil)) entities))]
    (loop [p planets e entities]
      (if (empty? p) e
        (recur (rest p) (loop [c (:chains (first p)) e2 e]
                         (if (empty? c) e2
                           (recur (rest c) (animate-chain e2 (first p) (first c))))))))))


(defn tween-token [entity tween word]
  "Animates the entities with a basic tween"
  (let [t (tween entity)
        w (word entity)]
  (if (some? t)
    (assoc entity word (if (:tween-lerp? entity)
                         (+ w (/ (- t w) (if (:particle? entity) (:life entity) 3)))
                         (+ w (* (- t w) (:tween-speed entity)))))
    entity)))


(defn animate [entities]
  "Animates all entities including particles and numbers"
  (->> entities

       ; Planets
       (map (fn [entity]
              (if (:chain-cooldown entity)
                (assoc entity :chain-cooldown (- (:chain-cooldown entity) 1))
                entity)))

       ; Particles
       (map (fn [entity]
              (if (:particle? entity)
                (if (> (:life entity) 1)
                  (assoc entity :life (- (:life entity) 1))
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
