(ns minimal-3d-physics.core
  (:require [kludge.core :refer :all]
            [kludge.g3d :refer :all]
            [kludge.g3d-physics :refer :all]
            [kludge.math :refer :all]
            [kludge.ui :refer :all]))

(def ^:const mass 10)

(defn get-environment
  []
  (let [attr-type (attribute-type :color :ambient-light)
        attr (attribute :color attr-type 0.3 0.3 0.3 1)]
    (environment :set attr)))

(defn get-camera
  []
  (doto (perspective 67 (game :width) (game :height))
    (position! 10 10 10)
    (direction! 0 0 0)))

(defn get-material
  []
  (let [c (color (+ 0.5 (* 0.5 (rand)))
                 (+ 0.5 (* 0.5 (rand)))
                 (+ 0.5 (* 0.5 (rand)))
                 1)]
    (material :set (attribute! :color :create-specular 1 1 1 1)
              :set (attribute! :float :create-shininess 8)
              :set (attribute! :color :create-diffuse c))))

(defn get-attrs
  []
  (bit-or (usage :position) (usage :normal)))

(defn create-sphere-body!
  [screen radius]
  (let [shape (sphere-shape radius)
        local-inertia (vector-3 0 0 0)]
    (sphere-shape! shape :calculate-local-inertia mass local-inertia)
    (->> (rigid-body-info mass nil shape local-inertia)
         rigid-body
         (add-body! screen))))

(defn create-sphere!
  [screen w h]
  (-> (model-builder)
      (model-builder! :create-sphere w h 4 24 24 (get-material) (get-attrs))
      model
      (assoc :body (create-sphere-body! screen (/ w 2)))))

(defn create-box-body!
  [screen half-w half-h]
  (let [shape (box-shape (vector-3 half-w half-h 1))
        local-inertia (vector-3 0 0 0)]
    (box-shape! shape :calculate-local-inertia mass local-inertia)
    (->> (rigid-body-info mass nil shape local-inertia)
         rigid-body
         (add-body! screen))))

(defn create-box!
  [screen w h]
  (-> (model-builder)
      (model-builder! :create-box w h 2 (get-material) (get-attrs))
      model
      (assoc :body (create-box-body! screen (/ w 2) (/ h 2)))))

(defscreen main-screen
  :on-show
  (fn [screen entities]
    (let [screen (update! screen
                          :renderer (model-batch)
                          :world (bullet-3d :rigid
                                            :set-gravity (vector-3 0 -10 0))
                          :attributes (get-environment)
                          :camera (get-camera))]
      (e/create-entities {} [(doto (create-sphere! screen 4 4)
         (body-position! 0 5 5))
       (doto (create-box! screen 4 4)
         (body-position! 0 5 0))])))

  :on-render
  (fn [screen entities]
    (clear!)
    (->> entities
         (step! screen)
         (render! screen)))

  :on-resize
  (fn [{:keys [width height] :as screen} entities]
    (size! screen width height))

  :on-touch-down
  (fn [{:keys [x y] :as screen} entities]
    (conj entities (create-box! screen 4 4)))

  :on-begin-contact
  (fn [screen entities]
    (let [e1 (first-entity screen entities)
          e2 (second-entity screen entities)]
      ; do something with the entities that made contact
      nil)))

(defscreen text-screen
  :on-show
    (fn [screen entities]
      (update! screen :camera (orthographic) :renderer (stage))
      (e/create-entity {} (e/entity-uid :fps) (assoc (label "0" (color :black))
                                                     :x 5)))

  :on-render
    (fn [screen entities]
      (doto (get entities (e/entity-uid :fps)) (label! :set-text (str (game :fps))))
      (render! screen entities))
  :on-resize
    (fn [screen entities]
      (height! screen 300)))

(defgame minimal-3d-physics
  :on-create
  (fn [this]
    (set-screen! this main-screen text-screen)))
