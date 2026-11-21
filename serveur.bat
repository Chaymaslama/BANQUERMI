@echo off
chcp 65001 >nul
java -cp out banque.rmi.serveur.ServeurBanque
pause
