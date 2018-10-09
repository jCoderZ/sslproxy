sslproxy
========

What is the SSL proxy?

   The SSL proxy starts a multithreaded server on a local port and opens for
   every incoming connection a new SSL encoded connection to the specified
   remotehost:remoteport. It encrypts the incoming data, forwards it to the
   SSL server, receives and decrypts the response and passes it back to the
   client.


How to build the SSL proxy?

   Use the provided build.bat and build.sh scripts.


How to start the SSL proxy?

   Customize the provided start_proxy.sh script.

   If you want to start the proxy without script:
      java -cp proxy.jar
           -Djavax.net.ssl.trustStore=proxy_truststore.jks
           com.encorus.sslproxy.ProxyServer <port> <remoteHost> <remotePort>


What is the truststore for?

   The Java property "javax.net.ssl.trustStore" can be used to specify a
   certain trust store that contains the necessary trusted certificates for
   the SSL connection. If the property is not provided the default truststore
   of the JDK will be used which is normaly located in
      $JAVA_HOME/jre/lib/security/cacerts

   The truststore must contain the issuer certificate of the SSL server
   certificate as a trusted certificate in order to perform the SSL handshake
   successfully.

   In order to view or change the contents of a truststore you have to use
   the Java keytool, which is part of the JDK.


How to add trusted certificates to the truststore?

   keytool -import
           -trustcacerts
           -alias <alias>
           -file <ca cert file>
           -keystore proxy_truststore.jks
           -storepass mko0okm 
 

How to view the contents of the truststore?

    keytool -list
            [-v]
            -keystore proxy_truststore.jks
            -storepass mko0okm


What is the password of the truststore?

   The password of the provided truststore is "mko0okm".
   The password of the JDK default truststore is "changeit".
