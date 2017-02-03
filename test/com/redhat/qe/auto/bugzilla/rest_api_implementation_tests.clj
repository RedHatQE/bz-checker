(ns com.redhat.qe.auto.bugzilla.rest-api-implementation-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io])
  (:import [com.redhat.qe.auto.bugzilla
            BzChecker
            BugzillaAPI
            REST_API
            BugzillaAPI$bzState
            RESTModule]
           [com.google.inject Guice])
  )

(def injector (atom nil))

(use-fixtures :once (fn [f]
                      (let [properties (p/load-from (io/file (System/getProperty "user.home") "automation.properties.bugzilla.rest.testing"))]
                        (doseq [[k v] properties]
                          (System/setProperty k v)))
                      (reset! injector (Guice/createInjector [(new RESTModule)]))
                      (f)))

(deftest credentials-test
  (is (= "rhq-xmlrpc@redhat.com" (System/getProperty "bugzilla.login"))))

(deftest get-bug-test
  (let [api (new REST_API)]
    (.connectBZ api)
    (let [bug (into {} (.getBug api "1"))
          bug-without-comments (dissoc bug "comments")]
      (is (= {"status" "ASSIGNED"
              "product" "Bugzilla"
              "keywords" ["Reopened" "TestCaseApproved" "TestCaseRejected"]
              "qa_contact" "bugzilla@redhat.com"} bug-without-comments)))))
