package fr.hugya.gsbandroid.modele;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.Hashtable;

import fr.hugya.gsbandroid.controleur.Controleur;
import fr.hugya.gsbandroid.outils.AccesHTTP;
import fr.hugya.gsbandroid.outils.AsyncResponse;

/**
 * Classe qui gère l'accès aux données de la base distante :
 * - identification
 * - récupération des frais
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class AccesDistant implements Serializable, AsyncResponse {
    // PROPRIETES :
    // ------------
    private Hashtable<String,String> id ;
    public void setId(Hashtable<String,String> logs) { this.id = logs ; }
    public Hashtable<String,String> getId () { return id ; }
    private final String adresseIP = "10.20.161.28";
    private final String adresseServeur = "http://" + adresseIP + "/gsbandroid/gsb.android.php" ;
    private final String CLASS_TAG = this.getClass().getName() ;
    private Controleur controle ;


    // CONSTRUCTEURS :
    // ---------------
    public AccesDistant (Controleur controle) {
        id = new Hashtable<String,String>() ;
        this.controle = controle ;
    }
    public AccesDistant(Hashtable<String,String> logs, Controleur controle) {
        this(controle) ;
        id = logs ;
    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Transfert des infos vers le serveur distant pour être ajoutées
     * - création de l'acces HTTP
     * - appel de la méthode execute sur l'accès, après avoir créé les paramètres nécessaires
     * A priori cette méthode n'est pas à modifier : il suffit de l'appeler dans le contrôleur,
     * lorsque c'est nécessaire, et en envoyant les bons paramètres
     * @param lesDonneesJSON
     */
    public void envoiDistant(String operation, JSONArray lesDonneesJSON) {
        // Création de l'objet d'accès à distance
        AccesHTTP accesDonnees = new AccesHTTP();
        accesDonnees.delegate = this;
        // Ajout des données en paramêtre (la page php doit donc attendre 2 paramètres)
        accesDonnees.addParam("op", operation);
        accesDonnees.addParam("lesDonnees", lesDonneesJSON.toString());
        // Envoi
        accesDonnees.execute(adresseServeur);
        Controleur.nbReqEnCours++ ;
    }
    /**
     * Retour du serveur : méthode événementielle qui s'éxécute au moment où le processus se termine donc :
     * - soit au moment d'un retour serveur suite à une requête
     * - soit au terme d'une impossibilité de connexion au serveur
     * @param output
     */
    public void processFinish(String output) {
        // Mise à jour du compteur de requête au serveur
        Controleur.nbReqEnCours-- ;

        // Vérification d'une erreur de connexion
        if (Controleur.erreurConnexion != "") {
            controle.message(Controleur.erreurConnexion) ;
            controle.endChargement();
            return ;
        }
        // Séparation du retour serveur action%donnees
        String[] message = output.split("%") ;
        // Echec de la connexion
        if (message[0].equals("echec")) {
            controle.message("Identifiant ou mot de passe incorrects") ;
            controle.endChargement() ;
        }
        // Erreur accés BDD
        else if (message[0].equals("erreur")) {
            controle.message(message[1]);
            controle.endChargement() ;
        }
        else if (message[0].equals("identificationOk")) {
            // Si c'est un retour d'identification positif, mise à jour des données, enregistrement de l'id distant
            id.put("id", message[1]) ;
            controle.setId(id) ;
            controle.syncDown(controle.getApp());
            controle.enregistrerUtilisateurLocal(controle.getApp(), id) ;
        }
        else if (message[0].equals("insertionFMOk")) {
            // Si c'était la dernière requête en cours, c'est que la synchronisation est terminée
            if (Controleur.nbReqEnCours == 0) {
                controle.message("Synchronisation terminée !") ;
                controle.endChargement();
                controle.refreshMenu();
            }
        }
        else if (message[0].equals("insertionFHFOk")) {
            // Si c'était la dernière requête en cours, c'est que la synchronisation est terminée
            if (Controleur.nbReqEnCours == 0) {
                controle.message("Synchronisation terminée !") ;
                controle.endChargement();
                controle.refreshMenu();
            }
        }
        else if (message[0].equals("recupFraisOK")) {
            // Retour d'une demande de syncDown
            try {
                // Récupération des données JSON à traiter dans le retour serveur et appel de la méthode de traitement
                JSONArray jsonTab = new JSONArray(message[1]);
                controle.recupererFraisDistant(jsonTab) ;
            } catch (JSONException e) {
                Log.d (CLASS_TAG, "Erreur JSON : " + e.getMessage()) ;
                controle.message("Erreur lors de la récupération des frais distants") ;
            }
        }
        else if (message[0].equals("recupFraisHFOK")) {
            // Retour d'une demande de syncDown
            try {
                // Récupération des données JSON à traiter dans le retour serveur et appel de la méthode de traitement
                JSONArray jsonTab = new JSONArray(message[1]) ;
                controle.recupererFraisHFDistant(jsonTab) ;
            } catch (JSONException e) {
                Log.d (CLASS_TAG, "Erreur JSON" + e.getMessage()) ;
                controle.message("Erreur lors de la récupération des frais distants") ;
            }
        }
    }
}