package fr.hugya.gsbandroid.modele;

import android.content.Context;
import android.util.Log;

import java.io.Serializable;
import java.util.Hashtable;

import fr.hugya.gsbandroid.outils.Serializer;

/**
 * Classe qui gère l'accès aux données locales :
 * - enregistrement et récupération de l'authentification en local
 * - enregistrement et récupération des frais en local
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class AccesLocal implements Serializable {
    // PROPRIETES :
    // ------------
    // Fichier contenant les informations sérialisées
    private final String bddLocalFilename = "lesFrais.gsb" ;
    private final String authenticationFilename = "Utilisateur.gsb" ;
    private Hashtable<String,String> id ;
    public Hashtable<String,String> getId () { return id ; }
    private boolean estCo ;
    public boolean estCo () { return estCo ; }


    // CONSTRUCTEURS :
    // ---------------
    public AccesLocal () {
        id = new Hashtable<String,String>() ;
    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Fonctions visant à récupérer les informations enregistrées localement (deSerialization)
     * @param context
     */
    public Hashtable<Integer,FraisMois> recupererLocal (Context context) {
        Hashtable<Integer,FraisMois> listFraisMois = (Hashtable<Integer, FraisMois>) Serializer.deSerialize(bddLocalFilename, context) ;
        // Si rien n'a été récupéré, il faut créer la liste
        if (listFraisMois==null) {
            listFraisMois = new Hashtable<>();
        }
        return listFraisMois ;
    }
    /**
     * Fonction visant à enregistrer en local les frais modifiés, utilisés lors d'un changement d'activité entre autre
     * @param context
     * @param listFraisMois
     */
    public void enregistrerLocal (Context context, Hashtable<Integer,FraisMois> listFraisMois) {
        Serializer.serialize(bddLocalFilename,listFraisMois,context);
    }
    /**
     * Fonction visant à récupérer les identifiants qui sont enregistrés en local s'il y en a
     * @param context
     * @return true si l'utilisateur a un profil sauvegardé, false sinon
     */
    public boolean recupererProfilLocal (Context context) {
        boolean ret = false ;
        id = (Hashtable<String,String>) Serializer.deSerialize(authenticationFilename, context);
        if (id!=null) {
            ret = true ;
            estCo = true ;
        }
        return ret ;
    }
    /**
     * Fonction visant à enregistrer le profil actuel de l'utilisateur en local pour ne pas lui redemander de s'identifier
     * @param context
     * @param profil identifiants de l'utilisateur à enregistrer localement
     */
    public void enregistrerUtilisateurLocal(Context context, Hashtable<String,String> profil) {
        Log.d("id = ", profil.get("id")) ;
        Serializer.serialize(authenticationFilename,profil, context);
    }
}
