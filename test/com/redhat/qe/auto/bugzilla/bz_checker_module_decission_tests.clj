(ns com.redhat.qe.auto.bugzilla.bz-checker-module-decission-tests
  (:require [clojure.test :refer :all]
            [clojure.java.io :as io]
            [clojurewerkz.propertied.properties :as p]
            [clojure.java.io :as io]
            [clojure.reflect :as r])
  (:import [com.redhat.qe.auto.bugzilla
            BzChecker
            IBugzillaAPI$bzState
            RESTModule XMLRPCModule])
  )

(use-fixtures :once (fn [f]
                      (let [properties (p/load-from (io/file (System/getProperty "user.home")
                                                             "automation.properties.bugzilla.rest.testing"))]
                        (doseq [[k v] properties]
                          (System/setProperty k v)))
                      (f)))

(deftest bz-checker-decides-implementation-module-test
  (System/setProperty "bugzilla.url" "https://beta.bugzilla.redhat.com/bugzilla/rest")
  (let [injection-module (BzChecker/getInjectionModule)]
    (is (= "com.redhat.qe.auto.bugzilla.RESTModule" (.. injection-module getClass getName))))

  (System/setProperty "bugzilla.url" "https://bugzilla.redhat.com/xmlrpc.cgi")
  (let [injection-module (BzChecker/getInjectionModule)]
    (is (= "com.redhat.qe.auto.bugzilla.XMLRPCModule" (.. injection-module getClass getName)))))
