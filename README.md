BzChecker is a small utility that helps us to communicate with Bugzilla.
It uses Google Guice injection.

# Requirements

  - java 8
  
# How to run tests with clojure `boot`

* install `boot.sh`  http://boot-clj.com/
* run tests with beep as additional service

        boot.sh watch javac speak test
   
# How to run tests with clojure `lein`

* install `lein` https://leiningen.org/
* run tests

        lein test

# If you want to see how injection is initialized

* for java see `main()` method in module `XMLRPCModule` or `RESTModule`
   - see `src/com/redhat/qe/auto/bugzilla/XMLRPCModule.java`
   - or  `src/com/redhat/qe/auto/bugzilla/RESTModule.java`
   
* for clojure see `use-fixtures` method in any clojure test file
   - `test/com/redhat/qe/auto/bugzilla/bz_checker_using_xmlrpc_tests.clj`
   - `test/com/redhat/qe/auto/bugzilla/bz_checker_using_rest_tests.clj`
   - `test/com/redhat/qe/auto/bugzilla/rest_api_implementation_tests.clj`
   
# It might be necessary to import a trusted certificate when using REST implemenation
You can do that this way:

      openssl s_client -connect beta-bugzilla.redhat.com:443 > trusted-certificate.crt
      sudo keytool -import -trustcacerts -keystore /etc/pki/java/cacerts \
                   -storepass changeit -noprompt -alias mycert \
                   -file trusted-certificate.crt 

# Using of REST API

  There are a few steps to do if you want to use REST API
  
## generate key in a web page for bugzilla access

   You can authorize your access by a generated key. 
   The key is generated on the web page `your account` in bugzilla.
   
## paste the generated key into a property `bugzilla.apikey`

   - you can still use `password` and `login` as usually instead of a generated key.
   
## update the property `bugzilla.url` to a new url

  Please note that the library recognizes a usage of REST API from the string automatically.
  If the string contains of `rest` it uses REST API implementation.
  
  If this is not what you want please send me an issue at github 
  to add other way to tell the library what API to use.

# ToDo

* `REST_API.java` methods:
   - `getBugs`
   - `update_bug_status`
   - `update_bug`
   - `add_bug_comment`
