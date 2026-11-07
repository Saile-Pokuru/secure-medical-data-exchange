@echo off
setlocal
if not exist out mkdir out
javac -encoding UTF-8 -cp "lib/*" -d out src\medicalsecure\*.java
if errorlevel 1 (echo Compilation failed. Check that both JCodec JARs are in lib\. & pause & exit /b 1)
java -cp "out;lib/*" medicalsecure.Main
