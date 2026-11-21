package banque.rmi.serveur;

import banque.rmi.commun.BanqueException;
import banque.rmi.commun.ICompte;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/** Implementation d'un compte. Objet distant : UnicastRemoteObject l'exporte automatiquement. */
public class CompteImpl extends UnicastRemoteObject implements ICompte {
    private static final long serialVersionUID = 1L;

    private final int id;
    private final String titulaire;
    private double solde;

    public CompteImpl(int id, String titulaire, double soldeInitial) throws RemoteException {
        super();                       // exporte l'objet (cree le point d'entree RMI)
        this.id = id;
        this.titulaire = titulaire;
        this.solde = soldeInitial;
    }

    public int getId() { return id; }
    public String getTitulaire() { return titulaire; }
    public synchronized double getSolde() { return solde; }

    // synchronized : un seul thread a la fois modifie le solde (clients simultanes)
    public synchronized void depot(double montant) throws RemoteException, BanqueException {
        verifierMontant(montant);
        solde += montant;
    }

    public synchronized void retrait(double montant) throws RemoteException, BanqueException {
        verifierMontant(montant);
        if (montant > solde) {
            throw new BanqueException("Solde insuffisant (solde = " + solde + ", retrait = " + montant + ")");
        }
        solde -= montant;
    }

    static void verifierMontant(double montant) throws BanqueException {
        if (!(montant > 0) || Double.isInfinite(montant)) {
            throw new BanqueException("Montant invalide (doit etre strictement positif) : " + montant);
        }
    }
}
