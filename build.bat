@setlocal

@echo adding JDK 1.5.0_05 to PATH
@set PATH=%PATH%;c:\sdk\jdk1.5.0_15\bin

@echo compiling classes
@javac -cp fawkez-all.jar org/jcoderz/keytoolz/sslproxy/*.java

@echo generating sslproxy.jar
@jar cvf sslproxy.jar org/jcoderz/keytoolz/sslproxy/*.class

@endlocal
