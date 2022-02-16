(ns com.redhat.qe.auto.bugzilla.rest-api-implementation-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io])
  (:import [com.redhat.qe.auto.bugzilla
            BzChecker
            REST_API
            IBugzillaAPI$bzState
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

(deftest get-bug-test
  (let [api (new REST_API)]
    (.connectBZ api)
    (let [bug (into {} (.getBug api "1"))
          bug-without-comments (dissoc bug "comments")]
      (is (= {"summary" "please DO NOT use this bug for testing",
              "status" "CLOSED"
              "product" "Bugzilla"
              "keywords" ["Reopened" "TestCaseApproved" "TestCaseRejected"]
              "qa_contact" "nobody@redhat.com"} bug-without-comments))

      (let [comments (get bug "comments")
            commentary (first comments)]
        (is (= false (get commentary "is_private")))
        (is (= "test bug" (get commentary "text")))))))

(deftest apikey-with-spaces-test
  (let [originalBugzillaApikey (System/getProperty "bugzilla.apikey")]
    (System/setProperty "bugzilla.apikey" (str originalBugzillaApikey " "))
    (let [api (new REST_API)]
      (.connectBZ api)
      (let [bug (into {} (.getBug api "1275179"))
            bug-without-comments (dissoc bug "comments")]
      (is (= {"summary" "subscription-manager attach --quantity 2 without --pool option auto-attaches the subscription",
              "status" "CLOSED"
              "product" "Red Hat Enterprise Linux 6"
              "keywords" ["Reopened" "Triaged"]
              "qa_contact" "jsefler@redhat.com"} bug-without-comments))))))

(deftest summary-field-exists-test
  (let [api (new REST_API)]
    (.connectBZ api)
    (let [bug (into {} (.getBug api "1418476"))]
      (is (contains? bug "summary")))))
