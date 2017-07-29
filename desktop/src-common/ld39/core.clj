(ns ld39.core
  (:require [ld39.utils :as u]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))



(defn start-game [entities]
  "Inits a new game"
  [(assoc (u/get-texture! "astral.png" nil nil) :x 0 :y 0 :z 0)])










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
    (render! screen entities)))

(defgame ld39-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))
