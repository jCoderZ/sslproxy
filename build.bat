@setlocal

@echo adding JDK to PATH
@set PATH=%PATH%;D:\sdk\jdk-11.0.2\bin

@echo compiling classes
@javac -cp fawkez-all.jar org/jcoderz/keytoolz/sslproxy/*.java

@echo generating sslproxy.jar
@jar cvf sslproxy.jar org/jcoderz/keytoolz/sslproxy/*.class

@endlocal
