(ns com.redhat.qe.auto.bugzilla.bz-checker-static-factory-using-rest-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io]
            [spyscope.core :refer :all]
            [clojure.reflect :as r])
  (:import [com.redhat.qe.auto.bugzilla
            BzChecker
            OldBzChecker
            IBugzillaAPI$bzState
            RESTModule]))

(use-fixtures :once (fn [f]
                      (let [properties (p/load-from (io/file (System/getProperty "user.home")
                                                             "automation.properties.bugzilla.rest.testing"))]
                        (doseq [[k v] properties]
                          (System/setProperty k v)))
                      (f)))

(deftest bz-checker-main-get-methods-test
  (let [checker (OldBzChecker/getInstance (new RESTModule))]
    (is (= IBugzillaAPI$bzState/CLOSED (.getBugState checker "1")))
    (is (= "CLOSED" (.getBugField checker "1" "status")))
    (is (= "Bugzilla" (.getBugField checker "1" "product") ))
    (is (= "bugzilla@redhat.com" (.getBugField checker "1" "qa_contact")))
    (is (= #{"Reopened" "TestCaseApproved" "TestCaseRejected"}
           (into #{} (.getBugField checker "1" "keywords"))))
    (is (= false (.isBugOpen checker "1")))))
