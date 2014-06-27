(ns minja.core)

(def ^:dynamic *strict* false)

(def ^:dynamic ^:private *element*)

(defmacro with-element [element & body]
  `(binding [*element* ~element]
     ~@body))

(defn v
  ([] *element*)
  ([ks] (get-in *element* ks)))

(defmacro _n [count-fn ks body]
  `(let [els# (v ~ks)]
     (when *strict*
       (assert (~count-fn (count els#))))
     (map #(with-element % ~@body) els#)))

(defmacro n* [ks & body]
  `(_n (constantly true) ~ks ~body))

(defmacro n+ [ks & body]
  `(_n pos? ~ks ~body))

(defmacro n? [ks & body]
  `(first (_n (partial >= 1) ~ks ~body)))

(defmacro n [ks & body]
  `(first (_n (partial = 1) ~ks ~body)))
