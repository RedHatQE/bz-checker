(defproject com.redhat.qe/bz-checker "2.2.1-SNAPSHOT"
  :description "a small library to check the status of a bugzilla bug."
  :java-source-path "src" ;; lein1
  :java-source-paths ["src"]
  :javac-options ["-g" "-target" "1.8" "-source" "1.8"]
  :dependencies [[com.redhat.qe/xmlrpc-client-tools "1.0.5"]
                 [com.google.inject/guice "5.1.0"]
                 [org.json/json "20211205"]
                 [org.apache.httpcomponents/httpclient "4.5.13"]
                 [clojurewerkz/propertied "1.3.0"]
                 [org.clojure/clojure "1.10.3"]]
  :plugins [[quickie "0.4.1"]
            [lein2-eclipse "2.0.0"]])
