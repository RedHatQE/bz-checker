BzChecker is a small utility that helps us to communicate with Bugzilla.
It uses Google Guice injection.

# How to run tests with clojure `boot`

* install `boot.sh`  http://boot-clj.com/
* run tests with beep as additional service

        boot.sh watch javac speak test
   
# How to run tests with clojure `lein`

* install `lein` https://leiningen.org/
* run tests

        lein test

# If you want to see how google guice injection is started

* for java see main() method in module `XMLRPCModule` or `RESTModule`
   - see `src/com/redhat/qe/auto/bugzilla/XMLRPCModule.java`
   - or  `src/com/redhat/qe/auto/bugzilla/RESTModule.java`
   
* for clojure see =use-fixtures= method in any clojure test file
   - `test/com/redhat/qe/auto/bugzilla/bz_checker_using_xmlrpc_tests.clj`
   - `test/com/redhat/qe/auto/bugzilla/bz_checker_using_rest_tests.clj`
   - `test/com/redhat/qe/auto/bugzilla/rest_api_implementation_tests.clj`
   
# It might be necessary to import a trusted certificate when using REST implemenation
You can do that this way:

      openssl s_client -connect beta-bugzilla.redhat.com:443 > trusted-certificate.crt
      sudo keytool -import -trustcacerts -keystore /etc/pki/java/cacerts \
                   -storepass changeit -noprompt -alias mycert \
                   -file trusted-certificate.crt 
