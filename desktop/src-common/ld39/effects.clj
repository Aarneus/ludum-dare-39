(ns ld39.effects
  (:require [ld39.utils :as u]
            [ld39.data :as d]
            [play-clj.core :refer :all]
            [play-clj.ui :refer :all]
            [play-clj.g2d :refer :all]))




(defn move-to-tile [entity tile-x tile-y]
  "Moves the entity to the coordinates"
  (assoc entity :tile-x tile-x :tile-y tile-y
    :x (u/get-screen-x tile-x tile-y)
    :y (u/get-screen-y tile-x tile-y)))
