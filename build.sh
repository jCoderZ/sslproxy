echo adding JDK 1.4.2 to PATH
PATH=$PATH:/tools/jdk/1.4.2/bin

echo compiling classes
javac com/encorus/sslproxy/*.java

echo generating proxy.jar
jar cvf proxy.jar com/encorus/sslproxy/*.class
