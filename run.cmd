@echo off
cd /d "%~dp0"

rem Use the JDK 26 installed on this machine, even if PATH is not configured.
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-26.0.2.101-hotspot"
if not exist "%JAVA_HOME%\bin\javac.exe" (
    echo Could not find javac at "%JAVA_HOME%\bin".
    echo Edit the JAVA_HOME line in this file to point at your JDK.
    pause
    exit /b 1
)
set "PATH=%JAVA_HOME%\bin;%PATH%"

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
