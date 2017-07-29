(ns ld39.utils
  (:require
    [play-clj.core :refer :all]
    [play-clj.g2d :refer :all]))


(def token-size 64)
(def font-size 16)
(def map-width 9)
(def map-height 9)

(def number-flip-offset (- token-size font-size))

(def textures (atom {}))
(def sounds (atom {}))
(def id (atom 0))

(defn floor [i]
  (-> i (Math/floor) (int)))

(defn get-entity-by-id [entities id]
  (find-first (fn [e] (= (:id e) id)) entities))

(defn get-next-id! []
  "Creates a new id and returns it"
  (swap! id + 1))

(defn in?
  [coll element]
  (some #(= element %) coll))

(defn is? [entity]
  (fn [e] (= (:id e) (:id entity))))

(defn not-player? [entity]
  (and (:token? entity) (not (:player? entity))))


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


(defn get-sound! [filename]
  "Returns the wanted sound file and loads it if not cached"
  (if (some? (get @sounds filename))
    (get @sounds filename)
    (do
      (swap! sounds assoc filename (sound filename))
      (get @sounds filename))))

(defn play-sound! [filename]
  "Plays the given sound file"
  (sound! (get-sound! filename) :play))


(defn create-sprite! [filename x y z width height frame-x frame-y]
  "Creates the given sprite"
  (-> (get-texture! filename width height)
      (aget frame-x frame-y)
      (texture)
      (assoc :x x :y y :z z :id (get-next-id!)
        :filename filename :width width :height height)))


(defn set-frame [entity frame-x frame-y]
  (let [filename (:filename entity)
        width (:width entity)
        height (:height entity)
        object (:object
                 (-> (get-texture! filename width height)
                     (aget frame-x frame-y)
                     (texture)))]
  (assoc entity :object object)))


(defn get-screen-x [tile-x tile-y]
  "Returns the screen x coordinate for the given tile"
  (+ (* tile-x token-size) (* (mod tile-y 2) (/ token-size 2))))

(defn get-screen-y [tile-x tile-y]
  "Returns the screen y coordinate for the given tile"
  (* tile-y token-size))

(defn get-tile-y [screen-x screen-y]
  "Returns the tile coordinate y for the given screen coordinates"
  (-> screen-y (/ token-size) (floor)))

(defn get-tile-x [screen-x screen-y]
  "Returns the tile coordinate x for the given screen coordinates"
  (-> screen-x (- (* (/ token-size 2) (mod (get-tile-y screen-x screen-y) 2))) (/ token-size) (floor)))


(defn get-token-at [entities x y]
  "Returns the entity in screen coordinates x and y"
  (let [tile-y (get-tile-y x y)
        tile-x (get-tile-x x y)]
    (find-first (fn [entity]
                  (and (:token? entity)
                       (= (:tile-x entity) tile-x)
                       (= (:tile-y entity) tile-y)))
                  entities)))


(defn update-entities [entities condition effect]
  "Applies the effect to the entities that fulfill the condition"
  (map
    (fn [entity] (if (condition entity) (effect entity) entity))
    entities))








