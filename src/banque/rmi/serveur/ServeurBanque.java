package banque.rmi.serveur;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

/** Demarre le registre RMI (port 1099) et publie le conseiller sous le nom "Conseiller". */
public class ServeurBanque {
    private static Registry registre;                 // references gardees pour eviter le ramasse-miettes
    private static ConseillerImpl conseiller;

    public static void main(String[] args) {
        int port = (args.length > 0) ? Integer.parseInt(args[0]) : 1099;
        try {
            registre = LocateRegistry.createRegistry(port);            // equivalent de "rmiregistry"
            conseiller = new ConseillerImpl();                         // 1. exposer l'objet distant
            Naming.rebind("rmi://localhost:" + port + "/Conseiller", conseiller);   // 2. publier
            System.out.println("Serveur bancaire pret sur le port " + port + " (Ctrl+C pour arreter)");
        } catch (Exception e) {
            System.err.println("Erreur serveur : " + e);
            e.printStackTrace();
        }
    }
}
