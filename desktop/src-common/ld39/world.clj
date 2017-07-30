(ns ld39.world
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [clojure.data :refer :all]
            [play-clj.g2d :refer :all]
            [play-clj.core :refer :all]))

(declare start-game)
(declare next-level)

(defn main-menu []
  "Jumps to the main menu"
  (do (reset! u/level 0)
    (-> [(u/create-sprite! "astral.png" 0 0 0 640 640 0 0)]
        (u/show-message "menu.png"))))


(defn check-game-state [entities]
  "Checks if the level is over"
  (cond
    (u/menu? entities) entities
    (u/game-over? entities) (do (reset! u/level 0) (u/show-message entities "game-over.png"))
    (u/victory? entities) (u/show-message entities "next-level.png")
    :else entities))


(defn level-entities [entities level]
  "Spawns the level specific tokens"
  (case level
    1 (-> entities ;A
          (d/spawn! :plain 3 4)
          (d/spawn! :plain 3 6)
          (d/spawn! :plain 7 7))
    2 (-> entities ;B
          (d/spawn! :armed 4 9)
          (d/spawn! :armed 3 8)
          (d/spawn! :armed 5 5))
    3 (-> entities ;AB
          (d/spawn! :armed 4 3)
          (d/spawn! :armed 3 2)
          (d/spawn! :armed 5 0)
          (d/spawn! :plain 7 0)
          (d/spawn! :plain 8 2))

    (do (reset! u/level 0) (u/show-message entities "ending.png"))))


(defn next-level [entities]
  "Spawns the next level"
  (let [level (swap! u/level + 1)]
  (do
    (u/play-sound! "level-start.wav")
    (-> (if (= level 1) (start-game) entities)
        (u/clear-messages)
        (level-entities level)))))

(defn start-game []
  "Inits a new game"
  (do (-> []
          (conj (u/create-sprite! "astral.png" 0 0 0 640 640 0 0))
          (conj (assoc (u/create-sprite! "underlay.png" 32 0 0.2 192 192 0 0) :underlay? true))
          (d/spawn! :snake 2 2))))
