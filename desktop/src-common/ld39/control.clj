(ns ld39.control
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [ld39.effects :as e]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))




(defn click-at [entities x y left-button?]
  "Handles user mouse clicks"
  (let [player (find-first :player? entities)
        tile-x (u/get-tile-x x y)
        tile-y (u/get-tile-y x y)
        token (u/get-token-at entities x y)]
    (if (<= (u/get-token-distance player {:tile-x tile-x :tile-y tile-y})  1)
      (if (some? token)
        ; Handle token clicks
        (if (:player? token)
          entities
          (-> entities
              (e/interact player token)
              (e/activate-planets :tick)))
        ; Handle empty cell clicks
        (-> entities
            (e/move-snake player tile-x tile-y)
            (e/activate-planets :tick)))
      entities)))
