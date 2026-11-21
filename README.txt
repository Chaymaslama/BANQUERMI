MINI-PROJET GESTION BANCAIRE - SOLUTION RMI (Windows)
=====================================================

PREREQUIS
- JDK 8 ou plus recent (JDK 8 conseille : il permet aussi de faire la version CORBA).
- Verifier dans cmd :  java -version   et   javac -version

STRUCTURE
src\banque\rmi\commun    ICompte, IConseiller, BanqueException (partage client/serveur)
src\banque\rmi\serveur   CompteImpl, ConseillerImpl, ServeurBanque
src\banque\rmi\client    ClientBanque (menu), ClientTest (tests automatiques)

COMMANDES (ouvrir cmd dans le dossier banque-rmi)
1) Compiler                :  compile.bat
2) Fenetre 1 - Serveur     :  serveur.bat
3) Fenetre 2 - Client      :  client.bat
4) Fenetre 3 - Tests auto  :  test.bat

COMMANDES EQUIVALENTES (sans les .bat)
javac -encoding UTF-8 -d out src\banque\rmi\commun\*.java src\banque\rmi\serveur\*.java src\banque\rmi\client\*.java
java -cp out banque.rmi.serveur.ServeurBanque
java -cp out banque.rmi.client.ClientBanque
java -cp out banque.rmi.client.ClientTest

DEUX MACHINES
Serveur : java -Djava.rmi.server.hostname=IP_SERVEUR -cp out banque.rmi.serveur.ServeurBanque
Client  : client.bat IP_SERVEUR      (ou : java -cp out banque.rmi.client.ClientBanque IP_SERVEUR)
Pare-feu Windows : autoriser java.exe (reseau prive) sur le serveur.
