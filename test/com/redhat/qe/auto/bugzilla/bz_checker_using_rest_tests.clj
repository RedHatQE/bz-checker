(ns com.redhat.qe.auto.bugzilla.bz-checker-using-rest-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io])
  (:import [com.redhat.qe.auto.bugzilla
            BzChecker
            IBugzillaAPI$bzState
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
    (is (= IBugzillaAPI$bzState/CLOSED (.getBugState checker "1")))
    (is (= "CLOSED" (.getBugField checker "1" "status")))
    (is (= "Bugzilla" (.getBugField checker "1" "product") ))
    (is (= "bugzilla@redhat.com" (.getBugField checker "1" "qa_contact")))
    (is (= #{"Reopened" "TestCaseApproved" "TestCaseRejected"}
           (into #{} (.getBugField checker "1" "keywords"))))
    (is (= false (.isBugOpen checker "1")))
    (is (= "rhel-x86_64-server-6-rhevh-beta channel maps are missing from channel-cert-mapping.txt"
           (.getBugField checker "1418476" "summary")))))
