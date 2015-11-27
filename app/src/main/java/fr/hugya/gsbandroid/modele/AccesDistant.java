package fr.hugya.gsbandroid.modele;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

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
    private final String adresseIP = "192.168.1.46";
    private final String adresseServeur = "http://" + adresseIP + "/gsbandroid/gsb.android.php" ;
    private Controleur controle ;


    // CONSTRUCTEURS :
    // ---------------
    public AccesDistant (Controleur controle) {
        id = new Hashtable<String,String>() ;
        this.controle = controle ;
    }
    public AccesDistant(Hashtable<String,String> logs, Controleur controle) {
        this.controle = controle ;
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
        Log.d("ENVOI", "DISTANT !!!");
        accesDonnees.execute(adresseServeur);
        Controleur.nbReqEnCours++ ;
        Log.d("nbReq =", Controleur.nbReqEnCours.toString()) ; // DEBUG
    }
    /**
     * Retour du serveur : méthode événementielle qui s'éxécute au moment où le processus se termine donc :
     * - soit au moment d'un retour serveur suite à une requête
     * - soit au terme d'une impossibilité de connexion au serveur
     * @param output
     */
    public void processFinish(String output) {
        // la réponse du serveur est dans le paramètre output
        // il ne reste plus qu'à la traiter (en faisant plusieurs if pour gérer les différentes réponses)
        // le mieux est de faire, côté serveur (php) une réponse unique contenant la concaténation d'un message
        // pour connaitre le type de réponse, suivi d'un signe distinctif (par exemple %)
        // suivi de l'information retournée au format json. Il est alors possible d'utiliser split pour séparer le message
        // du reste de l'information reçue, ensuite faire un test sur le message afin de savoir le type de réponse reçue
        // exemple :

        // Mise à jour du compteur de requête au serveur
        Controleur.nbReqEnCours-- ;
        Log.d("MESSAGE =", output) ; // DEBUG
        String[] message = output.split("%");
        Log.d("nbReq =", Controleur.nbReqEnCours.toString()) ; // DEBUG
        if (message[0].equals("echec")) {
            controle.message("Identifiant ou mot de passe incorrects") ;
        }
        else if (message[0].equals("erreur")) {
            controle.message(message[1]) ;
        }
        else if (message[0].equals("identificationOk")) {
            // Si c'est un retour d'identification positif, mise à jour des données, enregistrement de l'id distant
            id.put("id", message[1]) ;
            controle.setId(id) ;
            controle.retourMenu(controle.getApp()) ;
            controle.enregistrerUtilisateurLocal(controle.getApp(),id);
        }
        else if (message[0].equals("InsertionFMOk")) {
            // Si c'était la dernière requête en cours, c'est que la synchronisation est terminée
            if (Controleur.nbReqEnCours == 0) {
                controle.message("Synchronisation terminée !");
            }
        }
        else if (message[0].equals("insertionFHFOk")) {
            // Si c'était la dernière requête en cours, c'est que la synchronisation est terminée
            if (Controleur.nbReqEnCours == 0) {
                controle.message("Synchronisation terminée !");
            }
        }
    }
}