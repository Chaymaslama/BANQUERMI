package banque.rmi.client;

import banque.rmi.commun.BanqueException;
import banque.rmi.commun.ICompte;
import banque.rmi.commun.IConseiller;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.util.Scanner;

/** Client interactif en console. Usage : java banque.rmi.client.ClientBanque [adresse_serveur] */
public class ClientBanque {
    private static final Scanner in = new Scanner(System.in);

    public static void main(String[] args) {
        String hote = (args.length > 0) ? args[0] : "localhost";
        try {
            // 3. interroger le registre et 4. recuperer le stub (cast vers l'interface)
            IConseiller conseiller = (IConseiller) Naming.lookup("rmi://" + hote + ":1099/Conseiller");
            System.out.println("Connecte au serveur bancaire " + hote);
            boolean continuer = true;
            while (continuer) {
                afficherMenu();
                String choix = in.nextLine().trim();
                try {
                    switch (choix) {
                        case "1": creer(conseiller); break;
                        case "2": System.out.println(conseiller.infosCompte(lireEntier("Numero de compte : "))); break;
                        case "3": System.out.println("Solde = " + conseiller.rechercherCompte(lireEntier("Numero de compte : ")).getSolde()); break;
                        case "4": depot(conseiller); break;
                        case "5": retrait(conseiller); break;
                        case "6": virement(conseiller); break;
                        case "7": for (String s : conseiller.listerComptes()) System.out.println(s); break;
                        case "0": continuer = false; break;
                        default: System.out.println("Choix invalide");
                    }
                } catch (BanqueException e) {
                    System.out.println("Erreur metier : " + e.getMessage());
                } catch (NumberFormatException e) {
                    System.out.println("Saisie numerique invalide");
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur de communication : " + e);
        }
    }

    private static void afficherMenu() {
        System.out.println("\n--- BANQUE (RMI) ---");
        System.out.println("1 Creer un compte    2 Infos d'un compte   3 Consulter le solde");
        System.out.println("4 Depot              5 Retrait             6 Virement");
        System.out.println("7 Lister les comptes 0 Quitter");
        System.out.print("Choix : ");
    }

    private static void creer(IConseiller c) throws RemoteException, BanqueException {
        System.out.print("Titulaire : ");
        String nom = in.nextLine();
        ICompte compte = c.creerCompte(nom, lireMontant("Solde initial : "));   // reference distante
        System.out.println("Compte cree, numero " + compte.getId() + ", solde " + compte.getSolde());
    }

    private static void depot(IConseiller c) throws RemoteException, BanqueException {
        ICompte compte = c.rechercherCompte(lireEntier("Numero de compte : "));
        compte.depot(lireMontant("Montant : "));
        System.out.println("Nouveau solde = " + compte.getSolde());
    }

    private static void retrait(IConseiller c) throws RemoteException, BanqueException {
        ICompte compte = c.rechercherCompte(lireEntier("Numero de compte : "));
        compte.retrait(lireMontant("Montant : "));
        System.out.println("Nouveau solde = " + compte.getSolde());
    }

    private static void virement(IConseiller c) throws RemoteException, BanqueException {
        int src = lireEntier("Compte source : ");
        int dst = lireEntier("Compte destinataire : ");
        c.virement(src, dst, lireMontant("Montant : "));
        System.out.println("Virement effectue");
    }

    private static int lireEntier(String invite) {
        System.out.print(invite);
        return Integer.parseInt(in.nextLine().trim());
    }

    private static double lireMontant(String invite) {
        System.out.print(invite);
        return Double.parseDouble(in.nextLine().trim().replace(',', '.'));
    }
}
