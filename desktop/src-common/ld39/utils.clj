(ns ld39.utils
  (:require
    [play-clj.core :refer :all]
    [play-clj.g2d :refer :all]))


(def textures (atom {}))





(defn -load-texture-no-tiles! [filename]
  "Loads a plain texture into the cache"
  (swap! textures assoc filename (texture filename)))



(defn -load-texture-tiles! [filename width height]
  "Loads a texture into the cache as a spritesheet"
  (let [sheet (texture filename)
        tiles (texture! sheet :split width height)]
    (swap! textures assoc filename tiles)))


(defn get-texture! [filename width height]
  "Returns the wanted texture and loads it if it's not cached"
  (if (some? (get @textures filename))
    (get @textures filename)
    (do
      (if (and (some? width) (some? height))
        (-load-texture-tiles! filename width height)
        (-load-texture-no-tiles! filename))
      (get @textures filename))))