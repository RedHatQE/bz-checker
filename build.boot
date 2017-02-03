(set-env! :dependencies '[[adzerk/boot-test "1.1.2" :scope "test"]
                          [com.redhat.qe/xmlrpc-client-tools "1.0.5"]
                          [org.json/json "20160810"]
                          [org.apache.httpcomponents/httpclient "4.5.2"]
                          [clojurewerkz/propertied "1.2.0"]
                          [spyscope "0.1.5"]
                          [com.google.inject/guice "4.1.0"]]

          :source-paths #{"src" "test"}
          :resource-paths #{"src"})

(require '[adzerk.boot-test :refer :all])
(require 'spyscope.core)
(boot.core/load-data-readers!)
