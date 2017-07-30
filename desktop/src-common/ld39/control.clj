(ns ld39.control
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [ld39.effects :as e]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))




(defn click-at [entities x y left-button?]
  "Handles user mouse clicks"
  (let [tile-x (u/get-tile-x x y)
        tile-y (u/get-tile-y x y)
        token (u/get-token-at entities x y)]
      (if (some? token)
        ; Handle token clicks
        (if (:player? token)
          entities
          (e/interact entities (find-first :player? entities) token))
        ; Handle empty cell clicks
        (e/move-to-tile entities (find-first :player? entities) tile-x tile-y))))
