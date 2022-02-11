(ns com.redhat.qe.auto.bugzilla.bz-checker-using-xmlrpc-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io]
            [clojure.reflect :as r])
  (:import [com.redhat.qe.auto.bugzilla
            BzChecker
            IBugzillaAPI$bzState
            XMLRPCModule]
           [com.google.inject Guice]))

(def injector (atom nil))

(use-fixtures :once (fn [f]
                      (let [properties (p/load-from (io/file (System/getProperty "user.home")
                                                             "automation.properties.bugzilla.xmlrpc.testing"))]
                        (doseq [[k v] properties]
                          (System/setProperty k v)))
                      (reset! injector (Guice/createInjector [(new XMLRPCModule)]))
                      (f)))

(deftest bz-checker-main-get-methods-test
  (let [checker (.getInstance @injector BzChecker)]
    (is (= IBugzillaAPI$bzState/CLOSED (.getBugState checker "1")))
    (is (= "CLOSED" (.getBugField checker "1" "status")))
    (is (= "Bugzilla" (.getBugField checker "1" "product") ))
    (is (= "bugzilla@redhat.com" (.getBugField checker "1" "qa_contact")))
    (is (= #{"Reopened" "TestCaseApproved" "TestCaseRejected"}
           (into #{} (.getBugField checker "1" "keywords"))))
    (is (= false (.isBugOpen checker "1")))))
