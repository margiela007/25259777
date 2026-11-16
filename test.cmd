@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
javac -encoding UTF-8 -Xlint:all -d bin src\*.java tests\SelfTest.java
if errorlevel 1 goto failed
java -cp bin SelfTest
if errorlevel 1 goto failed
pause
exit /b 0
:failed
echo.
echo Compilation or test failed. Read the messages above.
pause
exit /b 1
