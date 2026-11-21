package banque.rmi.commun;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Interface distante d'un compte bancaire (le "contrat" vu par le client). */
public interface ICompte extends Remote {
    int getId() throws RemoteException;
    String getTitulaire() throws RemoteException;
    double getSolde() throws RemoteException;
    /** Montant strictement positif, sinon BanqueException. */
    void depot(double montant) throws RemoteException, BanqueException;
    /** Montant strictement positif et <= solde, sinon BanqueException. */
    void retrait(double montant) throws RemoteException, BanqueException;
}
