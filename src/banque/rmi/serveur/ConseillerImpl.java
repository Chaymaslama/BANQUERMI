package banque.rmi.serveur;

import banque.rmi.commun.BanqueException;
import banque.rmi.commun.IConseiller;
import banque.rmi.commun.ICompte;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/** Service du conseiller bancaire. Les donnees sont gardees en memoire. */
public class ConseillerImpl extends UnicastRemoteObject implements IConseiller {
    private static final long serialVersionUID = 1L;

    private final Map<Integer, CompteImpl> comptes = new ConcurrentHashMap<Integer, CompteImpl>();
    private final AtomicInteger sequence = new AtomicInteger(1000);   // identifiants uniques

    public ConseillerImpl() throws RemoteException {
        super();
    }

    public ICompte creerCompte(String titulaire, double soldeInitial) throws RemoteException, BanqueException {
        if (titulaire == null || titulaire.trim().isEmpty()) {
            throw new BanqueException("Titulaire invalide");
        }
        if (!(soldeInitial >= 0) || Double.isInfinite(soldeInitial)) {
            throw new BanqueException("Solde initial invalide : " + soldeInitial);
        }
        int id = sequence.incrementAndGet();          // regle : identifiant unique
        CompteImpl c = new CompteImpl(id, titulaire.trim(), soldeInitial);
        comptes.put(id, c);
        System.out.println("[Serveur] Compte cree : " + id + " (" + c.getTitulaire() + ")");
        return c;     // RMI remplace l'objet par son STUB lors du retour vers le client
    }

    public ICompte rechercherCompte(int id) throws RemoteException, BanqueException {
        return trouver(id);
    }

    public String infosCompte(int id) throws RemoteException, BanqueException {
        CompteImpl c = trouver(id);
        return "Compte " + c.getId() + " | " + c.getTitulaire() + " | solde = " + c.getSolde();
    }

    public String[] listerComptes() throws RemoteException {
        TreeMap<Integer, CompteImpl> tri = new TreeMap<Integer, CompteImpl>(comptes);
        String[] res = new String[tri.size()];
        int i = 0;
        for (CompteImpl c : tri.values()) {
            res[i++] = c.getId() + " | " + c.getTitulaire() + " | " + c.getSolde();
        }
        return res;
    }

    public void virement(int idSource, int idDest, double montant) throws RemoteException, BanqueException {
        if (idSource == idDest) {
            throw new BanqueException("Les comptes source et destinataire doivent etre differents");
        }
        CompteImpl src = trouver(idSource);           // regle : les deux comptes existent
        CompteImpl dst = trouver(idDest);
        // Verrouillage dans un ordre fixe (identifiant croissant) : evite l'interblocage
        CompteImpl premier = idSource < idDest ? src : dst;
        CompteImpl second = (premier == src) ? dst : src;
        synchronized (premier) {
            synchronized (second) {
                src.retrait(montant);   // verifie montant > 0 et montant <= solde source
                dst.depot(montant);     // debit et credit dans la meme section critique
            }
        }
        System.out.println("[Serveur] Virement " + montant + " : " + idSource + " -> " + idDest);
    }

    private CompteImpl trouver(int id) throws BanqueException {
        CompteImpl c = comptes.get(id);
        if (c == null) {
            throw new BanqueException("Compte inexistant : " + id);
        }
        return c;
    }
}
