(ns ld39.core
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))



(defn start-game [entities]
  "Inits a new game"
  (-> entities
      (conj (u/create-sprite! "astral.png" 0 0 0 640 640 0 0))
      (d/spawn :snake 0 1)
      (d/spawn :planet 3 4)
      ))





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
