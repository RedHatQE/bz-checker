(defproject com.redhat.qe/bz-checker "1.0.3-SNAPSHOT"
  :description "a small library to check the status of a bugzilla bug."
  :java-source-path "src" ;; lein1
  :java-source-paths ["src"]
  :dependencies [[com.redhat.qe/xmlrpc-client-tools "1.0.4"]]
  :plugins [[lein2-eclipse "2.0.0"]]
  :javac-options {:debug "on"})
