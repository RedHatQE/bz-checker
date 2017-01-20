(ns com.redhat.qe.auto.bugzilla.bz-checker-using-rest-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io])
  (:import (com.redhat.qe.auto.bugzilla BzCheckerUsingREST)
           (com.redhat.qe.auto.bugzilla.BzChecker)
           ))

(use-fixtures :once (fn [f]
                      (let [properties (p/load-from (io/file (System/getProperty "user.home") "automation.properties"))]
                        (doseq [[k v] properties]
                          (System/setProperty k v)))
                      (f)))

(deftest bz-checker-using-rest-test
  (let [checker (new BzCheckerUsingREST)]
    (is (= 1 1))))
