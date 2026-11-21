package banque.rmi.commun;

/**
 * Erreur metier de la banque (compte inexistant, montant invalide, solde insuffisant...).
 * Exception CONTROLEE ordinaire (serialisable) : elle est declaree dans le "throws" des
 * interfaces distantes, a cote de RemoteException, et RMI la renvoie telle quelle au client.
 * (Si elle heritait de RemoteException, RMI l'envelopperait dans une ServerException.)
 */
public class BanqueException extends Exception {
    private static final long serialVersionUID = 1L;

    public BanqueException(String message) {
        super(message);
    }
}
