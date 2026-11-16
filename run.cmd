@echo off
cd /d "%~dp0"
if not exist bin mkdir bin
javac -encoding UTF-8 -Xlint:all -d bin src\*.java
if errorlevel 1 goto failed
java -cp bin AssignmentOne
if errorlevel 1 goto failed
pause
exit /b 0
:failed
echo.
echo Compilation or run failed. Check that a full JDK is installed and on PATH.
pause
exit /b 1
