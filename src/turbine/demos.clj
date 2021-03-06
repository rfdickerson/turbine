(ns turbine.demos
    (:require [clojure.core.async :refer [chan]]
              [turbine.routes :refer :all]
			  [turbine.core :refer :all]))

;;;; HELPER TRANSDUCERS.
;;;; Note that if the transducers are stateful it's better to def a function
;;;; that returns a transducer so state isn't accidentally shared amongst
;;;; the channels. volatiles are _not_ thread-safe.
(def single-!-xform (map #(str % "!")))
(def double-!-xform (map #(str % "!!")))
(def cat-xform (map (fn [[l r]] (str l " " r))))

;;;; DEMO FUNCTIONS. EACH FUNCTION RETURNS AN ENTRY POINT TO THE TOPOLOGY.
(defn scatter-demo [] 
	(first (make-topology 
		[[:in :in1]
		 [:scatter :in1 [[:exc1 single-!-xform]
						 [:exc2 double-!-xform]]]
		 [:sink :exc1 println]
		 [:sink :exc2 println]])))

(defn splatter-demo []
	(first (make-topology 
		[[:in :in1]
		 [:splatter :in1 [[:exc1 single-!-xform]
						  [:exc2 double-!-xform]]]
		 [:sink :exc1 println]
		 [:sink :exc2 println]])))

(defn select-demo []
	(first (make-topology
		[[:in :in1]
		 [:select :in1 [[:exc1 single-!-xform true]
						[:exc2 double-!-xform false]] 
				  (fn [x] (Character/isLowerCase (first x)))]
		 [:sink :exc1 println]
		 [:sink :exc2 println]])))

(defn spread-demo []
	(first (make-topology
		[[:in :in1]
		 [:spread :in1 [[:exc1 single-!-xform]
		 				[:exc2 double-!-xform]]]
		 [:sink :exc1 #(println (str "Left: " %))]
		 [:sink :exc2 #(println (str "Right: " %))]])))

(defn union-demo []
	(first (make-topology
		[[:in :in1]
		 [:scatter :in1 [[:exc1 single-!-xform]
						 [:exc2 double-!-xform]]]
		 [:union [:exc1 :exc2] [:ident]]
		 [:sink :ident println]])))

(defn gather-demo []
	(first (make-topology
		[[:in :in1]
		 [:scatter :in1 [[:exc1 single-!-xform]
		 				 [:exc2 double-!-xform]]]
		 [:gather [:exc1 :exc2] [:ident]]
		 [:sink :ident println]])))

(defn collect-demo []
	(first (make-topology
		[[:in :in1]
		 [:collect :in1 [:out]
		 		   (fn [a v] (max a v)) 0]
		 [:sink :out println]])))