(ns ld39.utils
  (:require
    [play-clj.core :refer :all]
    [play-clj.g2d :refer :all]))


(def entity-size 64)


(def textures (atom {}))


(defn -load-texture! [filename width height]
  "Loads a plain texture into the cache"
  (let [sheet (texture filename)
        tiles (texture! sheet :split width height)]
    (do (swap! textures assoc filename tiles)
      tiles)))

(defn get-texture! [filename width height]
  "Returns the wanted texture and loads it if it's not cached"
  (if (some? (get @textures filename))
    (get @textures filename)
    (do
      (-load-texture! filename width height)
      (get @textures filename))))

(defn set-frame [entity width height frame-x frame-y]
  "Set the entity texture frame"
  (do (texture! entity :set-region
                (* width frame-x)
                (* height frame-y)
                width height)
    entity))

(defn create-sprite! [filename x y z width height frame-x frame-y]
  "Creates the given sprite"
  (-> (get-texture! filename width height)
      (aget frame-x frame-y)
      (texture)
      (assoc :x x :y y :z z :frame-x frame-x :frame-y frame-y)))
