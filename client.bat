@echo off
chcp 65001 >nul
java -cp out banque.rmi.client.ClientBanque %1
pause
