@echo off
chcp 65001 >nul
if not exist out mkdir out
javac -encoding UTF-8 -d out src\banque\rmi\commun\*.java src\banque\rmi\serveur\*.java src\banque\rmi\client\*.java
if errorlevel 1 (
  echo ERREUR DE COMPILATION
  pause
  exit /b 1
)
echo Compilation OK
