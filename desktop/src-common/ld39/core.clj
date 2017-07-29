(ns ld39.core
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [ld39.control :as c]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))



(defn start-game [entities]
  "Inits a new game"
  (do
    (u/play-sound! "level-start.wav")
    (-> entities
        (conj (u/create-sprite! "astral.png" 0 0 0 640 640 0 0))
        (d/spawn :snake 0 1)
        (d/spawn :plain 3 4)
        (d/spawn :plain 8 5)
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
      (d/animate)
      (render! screen)))

  :on-touch-down
  (fn [screen entities]
    (c/click-at entities (game :x) (game :y) (= (:button screen) (button-code :left)))))



(defgame ld39-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))




