(ns configurati.core
  (:refer-clojure :exclude [resolve])
  (:require
    [configurati.definition
     :refer [->ConfigurationDefinition]]
    [configurati.parameters
     :refer [map->ConfigurationParameter]]
    [configurati.sources
     :refer [->MapConfigurationSource
             ->EnvConfigurationSource
             ->YamlFileConfigurationSource
             ->MultiConfigurationSource]]
    [configurati.specification
     :refer [->ConfigurationSpecification]]))

(defn map-source [map]
  (->MapConfigurationSource map))

(defn env-source [& rest]
  (let [options (apply hash-map rest)
        prefix (:prefix options)]
    (->EnvConfigurationSource prefix)))

(defn yaml-file-source [path & rest]
  (let [options (apply hash-map rest)
        prefix (:prefix options)]
    (->YamlFileConfigurationSource path prefix)))

(defn multi-source [& sources]
  (->MultiConfigurationSource sources))

(defn with-parameter [name & rest]
  (let [defaults {:nilable false
                  :as      :string}
        base {:name name}
        options (apply hash-map rest)]
    [:parameter (map->ConfigurationParameter
                  (merge defaults base options))]))

(defn with-source [source]
  [:source source])

(defn configuration-specification [& parameters]
  (let [parameter-set (mapv second parameters)]
    (->ConfigurationSpecification parameter-set)))

(defn define-configuration [& rest]
  (let [elements (group-by #(first %) rest)
        parameters (mapv second (:parameter elements))
        sources (mapv second (:source elements))]
    (->ConfigurationDefinition
      (->MultiConfigurationSource sources)
      (->ConfigurationSpecification parameters))))

(defn resolve [definition]
  (configurati.definition/resolve definition))