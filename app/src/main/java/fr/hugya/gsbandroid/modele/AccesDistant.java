package fr.hugya.gsbandroid.modele;

import android.util.Log;

import org.json.JSONArray;

import java.io.Serializable;
import java.util.Hashtable;

import fr.hugya.gsbandroid.outils.AccesHTTP;

/**
 * Classe qui gère l'accès aux données de la base distante :
 * - identification
 * - récupération des frais
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class AccesDistant implements Serializable {
    // PROPRIETES :
    // ------------
    private boolean estCo = false ;
    public boolean estCo () {
        return estCo ;
    }
    private Hashtable<String,String> id ;
    private final String adresseIP = "192.168.1.46";


    // CONSTRUCTEURS :
    // ---------------
    public AccesDistant () {
        id = new Hashtable<String,String>() ;
    }
    public AccesDistant(Hashtable<String,String> logs) {
        id = logs ;
    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Interrogation du serveur pour vérifier que l'utilisateur existe dans la BDD distante
     * @param logs
     */
    public void identifierUtilisateur (Hashtable<String,String> logs) {
        // On vérifie que les logs sont bien mis en forme
        if (logs.size()!=2) {
            return ;
        }
        AccesHTTP accesDonnee = new AccesHTTP() {
            /**
             * Code exécuté en cas de retour du serveur
             * @param result
             */
            @Override
            protected void onPostExecute (Long result) {
                switch (this.ret) {
                    case "echec":
                        Log.d("ERREUR ACCES DISTANT", "Identification impossible") ;
                        break ;
                    case "succes":
                        estCo = true ;
                        break ;
                }
            }
        };
        // Type d'opération
        accesDonnee.addParam("op","checkLogs");
        // Ajout des paramètres : ici l'identifiant et le mdp
        accesDonnee.addParam(logs) ;
        // Envoi des requêtes HTTP
        accesDonnee.execute("http://" + adresseIP + "/gsbandroid/gsb.android.php");
    }
    /**
     * Récupére les informations de la base distante enregistré en local
     * @return le dictionnaire de données
     */
    public Hashtable<Integer,FraisMois> recupererDistant () {
        Hashtable<Integer,FraisMois> listFraisMois = new Hashtable<>();

        return listFraisMois ;
    }
    /**
     * Surcharge
     * @param tab les identifiants de l'utilisateur
     * @return le dictionnaire de données
     */
    public Hashtable<Integer,FraisMois> recupererDistant (Hashtable<String,String> tab) {
        // On ne peut faire une synchronisation descendante qu'en étant correctement loggé
        if (tab==null||tab.size()!=2) {
            return null ;
        }
        // Enregistrement des identifiants
        id = tab ;
        return recupererDistant() ;
    }
    /**
     * Envoie les informations enregistrées localement à la BDD distante
     * @param lesFrais
     */
    public void enregistrerDistant (Hashtable<Integer,FraisMois> lesFrais) {
        // On ne peut faire une synchronisation descendante qu'en étant correctement loggé
        if (id==null||id.size()!=2) {
            return ;
        }
        try {
            JSONArray tJson = new JSONArray(lesFrais);
        }
        catch (Exception e) {
            Log.d("JSONERROR","!!! Imcompatible !!!") ;
        }
    }
}