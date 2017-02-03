BzChecker is a small utility that helps us to communicate with Bugzilla.
It uses Google Guice injection.

# How to run tests with clojure boot

* install boot.sh  http://boot-clj.com/
* run tests with beep

   boot.sh watch javac speak test
   
# How to run tests with clojure lein

* install lein https://leiningen.org/
* run tests

   lein test

# It might be necessary to import a trusted certificate when using REST implemenation
You can do that this way:

   openssl s_client -connect beta-bugzilla.redhat.com:443 > trusted-certificate.crt

   sudo keytool -import -trustcacerts -keystore /etc/pki/java/cacerts -storepass changeit -noprompt -alias mycert -file trusted-certificate.crt 
