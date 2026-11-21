package banque.rmi.client;

import banque.rmi.commun.BanqueException;
import banque.rmi.commun.ICompte;
import banque.rmi.commun.IConseiller;
import java.rmi.Naming;

/** Scenarios de test automatiques. Usage : java banque.rmi.client.ClientTest [adresse_serveur] */
public class ClientTest {
    private static int ok = 0, ko = 0;

    private interface Action { void run() throws Exception; }

    private static void check(String nom, boolean condition) {
        if (condition) { ok++; System.out.println("[OK] " + nom); }
        else { ko++; System.out.println("[KO] " + nom); }
    }

    /** Verifie qu'une BanqueException est bien levee. */
    private static void attendErreur(String nom, Action a) {
        try {
            a.run();
            ko++; System.out.println("[KO] " + nom + " (aucune erreur levee)");
        } catch (BanqueException e) {
            ok++; System.out.println("[OK] " + nom + " -> " + e.getMessage());
        } catch (Exception e) {
            ko++; System.out.println("[KO] " + nom + " (exception inattendue : " + e + ")");
        }
    }

    private static boolean egal(double a, double b) { return Math.abs(a - b) < 1e-9; }

    public static void main(String[] args) throws Exception {
        String hote = (args.length > 0) ? args[0] : "localhost";
        final IConseiller cons = (IConseiller) Naming.lookup("rmi://" + hote + ":1099/Conseiller");

        System.out.println("=== T1 : creation et recherche de comptes ===");
        final ICompte a = cons.creerCompte("Ali", 500);
        final ICompte b = cons.creerCompte("Sara", 200);
        check("Identifiants uniques", a.getId() != b.getId());
        check("Recherche d'un compte existant", cons.rechercherCompte(a.getId()).getId() == a.getId());
        attendErreur("Recherche d'un compte inexistant", new Action() { public void run() throws Exception { cons.rechercherCompte(99999); } });
        attendErreur("Creation avec solde initial negatif", new Action() { public void run() throws Exception { cons.creerCompte("Test", -10); } });
        attendErreur("Creation avec titulaire vide", new Action() { public void run() throws Exception { cons.creerCompte(" ", 10); } });

        System.out.println("=== T2 : consultation du solde ===");
        check("Solde de Ali = 500", egal(a.getSolde(), 500));
        check("Solde de Sara = 200", egal(b.getSolde(), 200));

        System.out.println("=== T3 : depot ===");
        a.depot(100);
        check("Depot de 100 -> solde 600", egal(a.getSolde(), 600));
        attendErreur("Depot de 0", new Action() { public void run() throws Exception { a.depot(0); } });
        attendErreur("Depot negatif", new Action() { public void run() throws Exception { a.depot(-5); } });

        System.out.println("=== T4 : retrait ===");
        a.retrait(50);
        check("Retrait de 50 -> solde 550", egal(a.getSolde(), 550));
        attendErreur("Retrait superieur au solde", new Action() { public void run() throws Exception { a.retrait(10000); } });
        attendErreur("Retrait negatif", new Action() { public void run() throws Exception { a.retrait(-1); } });
        check("Solde inchange apres erreurs", egal(a.getSolde(), 550));

        System.out.println("=== T5 : virements ===");
        cons.virement(a.getId(), b.getId(), 150);
        check("Virement 150 : source = 400", egal(a.getSolde(), 400));
        check("Virement 150 : destinataire = 350", egal(b.getSolde(), 350));
        attendErreur("Virement superieur au solde source", new Action() { public void run() throws Exception { cons.virement(a.getId(), b.getId(), 9999); } });
        attendErreur("Virement de montant negatif", new Action() { public void run() throws Exception { cons.virement(a.getId(), b.getId(), -20); } });
        attendErreur("Virement vers un compte inexistant", new Action() { public void run() throws Exception { cons.virement(a.getId(), 99999, 10); } });
        attendErreur("Virement depuis un compte inexistant", new Action() { public void run() throws Exception { cons.virement(99999, b.getId(), 10); } });
        attendErreur("Virement vers le meme compte", new Action() { public void run() throws Exception { cons.virement(a.getId(), a.getId(), 10); } });
        check("Soldes inchanges apres virements refuses", egal(a.getSolde(), 400) && egal(b.getSolde(), 350));
        check("Total conserve (750)", egal(a.getSolde() + b.getSolde(), 750));

        System.out.println("=== T6 : clients simultanes (concurrence) ===");
        final ICompte c = cons.creerCompte("Concurrent", 0);
        Thread[] clients = new Thread[4];
        for (int i = 0; i < clients.length; i++) {
            clients[i] = new Thread(new Runnable() {
                public void run() {
                    try { for (int k = 0; k < 50; k++) c.depot(1); }
                    catch (Exception e) { e.printStackTrace(); }
                }
            });
            clients[i].start();
        }
        for (Thread t : clients) t.join();
        check("4 clients x 50 depots de 1 -> solde 200", egal(c.getSolde(), 200));

        final ICompte x = cons.creerCompte("X", 1000);
        final ICompte y = cons.creerCompte("Y", 1000);
        Thread t1 = new Thread(new Runnable() { public void run() {
            try { for (int k = 0; k < 50; k++) cons.virement(x.getId(), y.getId(), 10); } catch (Exception e) { e.printStackTrace(); } } });
        Thread t2 = new Thread(new Runnable() { public void run() {
            try { for (int k = 0; k < 50; k++) cons.virement(y.getId(), x.getId(), 10); } catch (Exception e) { e.printStackTrace(); } } });
        t1.start(); t2.start();
        t1.join(20000); t2.join(20000);
        check("Virements croises sans interblocage", !t1.isAlive() && !t2.isAlive());
        check("Total conserve apres virements croises (2000)", egal(x.getSolde() + y.getSolde(), 2000));

        System.out.println("\nRESULTAT : " + ok + " tests reussis, " + ko + " echoues");
        System.exit(ko == 0 ? 0 : 1);
    }
}
