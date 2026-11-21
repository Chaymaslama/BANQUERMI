package banque.rmi.commun;

import java.rmi.Remote;
import java.rmi.RemoteException;

/** Interface distante du conseiller bancaire : gestion des comptes et virements. */
public interface IConseiller extends Remote {
    /** Cree un compte et retourne une REFERENCE DISTANTE (stub) sur ce compte. */
    ICompte creerCompte(String titulaire, double soldeInitial) throws RemoteException, BanqueException;
    /** Retrouve un compte existant et retourne une reference distante. */
    ICompte rechercherCompte(int id) throws RemoteException, BanqueException;
    String infosCompte(int id) throws RemoteException, BanqueException;
    String[] listerComptes() throws RemoteException;
    /** Transfert coherent : debit de la source = credit de la destination. */
    void virement(int idSource, int idDest, double montant) throws RemoteException, BanqueException;
}
