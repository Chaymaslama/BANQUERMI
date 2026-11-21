@echo off
chcp 65001 >nul
java -cp out banque.rmi.client.ClientTest %1
pause
