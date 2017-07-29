(ns ld39.core.desktop-launcher
  (:require [ld39.core :refer :all])
  (:import [com.badlogic.gdx.backends.lwjgl LwjglApplication]
           [org.lwjgl.input Keyboard])
  (:gen-class))

(defn -main
  []
  (LwjglApplication. ld39-game "ld39" 640 640)
  (Keyboard/enableRepeatEvents true))
