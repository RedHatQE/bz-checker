(ns com.redhat.qe.auto.bugzilla.bz-checker-using-rest-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io])
  (:import [com.redhat.qe.auto.bugzilla
            BzChecker
            BugzillaAPI
            BugzillaAPI$bzState
            RESTModule]
           [com.google.inject Guice]))

(def injector (atom nil))

(use-fixtures :once (fn [f]
                      (let [properties (p/load-from (io/file (System/getProperty "user.home") "automation.properties.bugzilla.rest.testing"))]
                        (doseq [[k v] properties]
                          (System/setProperty k v)))
                      (reset! injector (Guice/createInjector [(new RESTModule)]))
                      (f)))

(deftest bz-checker-using-rest-test
  (let [checker (.getInstance @injector BzChecker)]
    (is (= BugzillaAPI$bzState/ASSIGNED (.getBugState checker "1")))
    (is (= "ASSIGNED" (.getBugField checker "1" "status")))
    (is (= "Bugzilla" (.getBugField checker "1" "product") ))
    (is (= "bugzilla@redhat.com" (.getBugField checker "1" "qa_contact")))
    (is (= #{"Reopened" "TestCaseApproved" "TestCaseRejected"}
           (into #{} (.getBugField checker "1" "keywords"))))
    (is (= true (.isBugOpen checker "1")))))
