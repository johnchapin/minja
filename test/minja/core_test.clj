(ns minja.core-test
  (:require [clojure.test :refer :all]
            [clojure.data.json :as json]
            [minja.core :refer :all]))

(def string
  (json/read-str "\"foo\""))

(def string-array
  (json/read-str "[\"foo\" \"bar\"]"))

(def number
  (json/read-str "1"))

(def number-array
  (json/read-str "[1 2]"))

(def bool
  (json/read-str "true"))

(def bool-array
  (json/read-str "[true false]"))

(def null
  (json/read-str "null"))

(def null-array
  (json/read-str "[null null]"))

;; Helper fcns

(defn read-json [s]
  (json/read-str s :key-fn keyword))

(defmacro with-json [s & body]
  `(with-element (read-json ~s)
     ~@body))

;; Tests

(deftest test-values
  (are [expected input]
       (= expected (with-json input (v)))

       "foo"        "\"foo\""
       1            "1"
       true         "true"
       false        "false"
       nil          "null"
       {:foo "bar"} "{\"foo\": \"bar\"}"))

(deftest test-arrays
  (are [expected input]
       (= expected (with-json input (n* [] (v))))

       ["foo" "bar"]  "[\"foo\" \"bar\"]"
       [["foo" "bar"]]  "[[\"foo\" \"bar\"]]"
       [["foo" "bar"] "baz"] "[[\"foo\" \"bar\"] \"baz\"]"
       ["foo" ["bar" "baz"]] "[\"foo\" [\"bar\" \"baz\"]]"
       ["foo" ["bar"] "baz"] "[\"foo\" [\"bar\"] \"baz\"]"))

(deftest test-object
  (is (= {:foo "bar" :baz [true false]}
         (with-json "{\"foo\": \"bar\", \"baz\": [true false]}" (v))))

  (is (= "bar"
         (with-json "{\"foo\": \"bar\", \"baz\": [true false]}" (v [:foo])))))

(deftest test-objects
  (let [input "[{\"foo\": \"bar\"}, {\"foo\": 2}]"]
    (is (= [{:foo "bar"} {:foo 2}]
           (with-json input (n* [] (v)))))))
