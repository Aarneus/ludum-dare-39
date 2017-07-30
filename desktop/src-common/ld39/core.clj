(ns ld39.core
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [ld39.control :as c]
            [ld39.process :as p]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))


(defn update-underlay [entities]
  "Shows the underlay when the player is not moving"
  (let [player (find-first :player? entities)
        player-moving? (and (:tween-x player) (> (Math/abs (- (:tween-x player) (:x player))) 1))
        ux (- (:x player) u/token-size)
        uy (- (:y player) u/token-size)
        uz (if player-moving? -1 0.2)]
    (u/update-entities
      entities :underlay? (fn [e] (assoc e :x ux :y uy :z uz)))))



(defn start-game [entities]
  "Inits a new game"
  (do
    (u/play-sound! "level-start.wav")
    (-> entities
        (conj (u/create-sprite! "astral.png" 0 0 0 640 640 0 0))
        (conj (assoc (u/create-sprite! "underlay.png" 32 0 0.2 192 192 0 0) :underlay? true))
        (d/spawn! :snake 0 1)
        (d/spawn! :plain 3 4)
        (d/spawn! :plain 4 3)
        (d/spawn! :plain 3 6)
        (d/spawn! :plain 4 7)
        (d/spawn! :armed 2 1)
        (d/spawn! :armed 3 3)
        )))





(defscreen main-screen
  :on-show
  (fn [screen entities]
    (graphics! :set-resizable false)
    (graphics! :set-title "LD 39")
    (update! screen :renderer (stage))
    (start-game entities))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (p/update-chains)
         (p/animate-chains)
         (p/animate)
         (update-underlay)
         (sort-by :z)
         (render! screen)))

  :on-touch-down
  (fn [screen entities]
    (c/click-at entities (game :x) (game :y) (= (:button screen) (button-code :left)))))



(defgame ld39-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))




