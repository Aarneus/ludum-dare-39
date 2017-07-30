(ns ld39.core
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [ld39.control :as c]
            [ld39.process :as p]
            [ld39.world :as w]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))


(defscreen main-screen
  :on-show
  (fn [screen entities]
    (graphics! :set-resizable false)
    (graphics! :set-title "LD 39")
    (update! screen :renderer (stage))
    (w/main-menu))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (p/remove-dead)
         (w/check-game-state)
         (p/update-chains)
         (p/animate-chains)
         (p/animate)
         (u/update-underlay)
         (sort-by :z)
         (render! screen)))

  :on-touch-down
  (fn [screen entities]
    (c/click entities (game :x) (game :y) (= (:button screen) (button-code :left)))))



(defgame ld39-game
  :on-create
  (fn [this]
    (set-screen! this main-screen)))




