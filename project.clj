(defproject com.redhat.qe/bz-checker "2.0.1-SNAPSHOT"
  :description "a small library to check the status of a bugzilla bug."
  :java-source-path "src" ;; lein1
  :java-source-paths ["src"]
  :javac-options ["-g" "-target" "1.8" "-source" "1.8"]
  :dependencies [[com.redhat.qe/xmlrpc-client-tools "1.0.5"]
                 [com.google.inject/guice "4.1.0"]
                 [org.json/json "20160810"]
                 [org.apache.httpcomponents/httpclient "4.5.2"]
                 [clojurewerkz/propertied "1.2.0"]
                 [org.clojure/clojure "1.8.0"]]
  :plugins [[quickie "0.4.1"]
            [lein2-eclipse "2.0.0"]])
