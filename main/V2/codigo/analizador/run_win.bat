@echo off
setlocal

cd /d "%~dp0"

where mvn >nul 2>nul
if errorlevel 1 (
  echo Maven no esta instalado o no esta en PATH.
  exit /b 1
)

mvn -q -DskipTests org.codehaus.mojo:exec-maven-plugin:3.5.0:java -Dexec.mainClass=app.Main
exit /b %errorlevel%