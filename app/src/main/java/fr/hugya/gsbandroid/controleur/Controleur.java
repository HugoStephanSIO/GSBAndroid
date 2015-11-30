package fr.hugya.gsbandroid.controleur;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import fr.hugya.gsbandroid.modele.AccesDistant;
import fr.hugya.gsbandroid.modele.AccesLocal;
import fr.hugya.gsbandroid.modele.FraisHf;
import fr.hugya.gsbandroid.modele.FraisMois;
import fr.hugya.gsbandroid.vue.ConnexionActivity;
import fr.hugya.gsbandroid.vue.MenuActivity;

/**
 * Classe controleur faisant le lien entre les vues et le modèle (les données) en utilisant les outils
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class Controleur implements Serializable { // Serializable pour pouvoir être transmis entre les activitées
	// PROPRIETES :
    // ------------
    // Compteur de threads de connexion actifs
    public static Integer nbReqEnCours = 0 ; // PAS PROPRE ?
	// Objet d'accès local
	private AccesLocal accesLocal ;
	// Objet d'accès distant
	private AccesDistant accesDistant ;
    // Logs de l'utilisateur
    private Hashtable<String,String> id ;
    public Hashtable<String,String> getId () {
        return id ;
    }
    public void setId(Hashtable<String,String> tab) {
        this.id = tab ;
    }
    // Statut de la connexion
    private boolean estCo = false ;
    public boolean estCo () { return estCo ; }
    // Tableau d'informations mémorisées en local
    private Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<>() ;
    public Hashtable<Integer, FraisMois> getListFraisMois() {
        return listFraisMois;
    }
    // Contexte éventuel
    private AppCompatActivity app ;
    public AppCompatActivity getApp () {
        return app ;
    }
    public void setApp (AppCompatActivity a) { this.app = a ;}
    // Indicateur de modifications faites en locales pour signaler la nécéssité d'une syncUp
    private boolean modif = false ;
    public void setModif (boolean b) { this.modif = b ;}
    public boolean getModif () { return this.modif ; }


    // CONSTRUCTEURS :
    // --------------
    public Controleur (Context context) {
        // Initialisation des objets du modéle
		accesDistant = new AccesDistant(id,this) ;
		accesLocal = new AccesLocal () ;
        // Si un profil est enregistré en local
        if(accesLocal.recupererProfilLocal(context)) {
            // Mise à jour du statut de la connexion et des logs actuel de l'utilisateur
            estCo = true ;
            id = accesLocal.getId() ;
        }
	}


	// FONCTIONS ACCES DONNEES LOCALES :
	// ---------------------------------
	/**
	 * Fonctions visant à récupérer les informations enregistrées localement (deSerialization)
	 * @param context
	 */
	public void recupererLocal (Context context) {
		listFraisMois = accesLocal.recupererLocal(context) ;
	}
	/**
	 * Fonction qui enregistre la liste des frais par mois actuelle dans le fichier
	 * @param context
	 */
	public void enregistrerLocal (Context context) {
		// Enregistrement de l'état des frais
		accesLocal.enregistrerLocal(context, listFraisMois);
        modif = true ;
	}
    /**
     * Encapsulation de la procédure accesLocal.enregistrerUtilisateurLocal
     * @param context
     * @param tab identifiants de l'utilisateur à enregistrer localement
     */
    public void enregistrerUtilisateurLocal (Context context, Hashtable<String,String> tab) {
        accesLocal.enregistrerUtilisateurLocal(context, tab);
    }


    // FONCTIONS ACCES DONNEES DISTANTES :
    // -----------------------------------
    /**
     * Fonction qui tente d'identifier l'utilisateur à partir des logs transmis en arguments
     * @param logs "login"/login "password/password
     */
    public void identifierUtilisateurDistant (Hashtable<String,String> logs, AppCompatActivity app) {
        // Création de la list JSON compatible avec les ids
		id = logs ;
        List list = new ArrayList<>() ;
		list.add(logs.get("login")) ;
		list.add(logs.get("password")) ;
        this.app = app ;
        accesDistant.setId(id) ;
		// Tentative de connexion de l'utilisateur
        accesDistant.envoiDistant("checkLogs", new JSONArray(list));
    }
    /**
     * Fonction qui récupére les données de frais de l'utilisateur
     * @param context
     */
    public void syncDown (Context context) {
        if (id.size()!=3) { // L'utilisateur n'est pas connecté
            Log.d("Erreur Controleur", "id.size() != 3 while must be");
            return;
        }
        List list = new ArrayList <>() ;
        list.add(id.get("id")) ;
        this.app = ((AppCompatActivity)context) ;
        accesDistant.envoiDistant("recupLesFrais", new JSONArray(list));
    }
    /**
     * Fonction qui envoie les données enregistrées localement à la base distante
     * @param context
     */
    public void syncUp (Context context) {
        // Remise à false de l'indicateur de modif locales à enregistrer en distant
        this.modif = false ;
        app.finish() ;
        app.startActivity(app.getIntent()) ;
        // Lancement d'une boite de dialogue de chargement
        //startChargement("Synchronisation en cours...", context);
        // Récupération de ce qui est enregistré en local ??? NECESSAIRE ???
        recupererLocal(context);
        // Enregistrement du contexte
        this.app = (AppCompatActivity)context ;
        // Transfert d'id dans la classe thread async
        accesDistant.setId(id) ;
        // La synchronisation montante implique d'être identifié en utilisateur distant et donc d'avoir récupérer l'id distant
        if (id.size()!=3) {
            Log.d("Erreur Controleur", "id.size() != 3 while must be") ;
            return ;
        }
        // Déclaration des deux listes qui seront remplies plusieurs fois dans la double boucle
        List<String> listFrais ;
        List<String> listFraisHF;
        // Parcours tous les FraisMois du tableau
        for(FraisMois fm : listFraisMois.values()) {
            // Récupération des frais du mois en forme de liste ce mois
            listFrais = fm.convertirFraisList() ;
            // Récupération de la clef
            Integer key = fm.getAnnee() * 100 + fm.getMois() ;
            // Envoi d'une requête de création d'une fichefrais
			List donneesFicheFrais = new ArrayList () ;
            donneesFicheFrais.add(id.get("id")) ;
            donneesFicheFrais.add(key) ;
            donneesFicheFrais.add("CR") ;
            accesDistant.envoiDistant("newFicheFrais", new JSONArray(donneesFicheFrais));
            // Ajout de l'id de l'utilisateur en première position de la liste de ce mois
            listFrais.add(0, id.get("id")) ;
            // Envoi de la liste au serveur pour enrgistrement des frais forfait de ce mois
            accesDistant.envoiDistant("addLesLignesFrais", new JSONArray(listFrais));
            // Parcours de tous les frais hors forfaits
            for (FraisHf fhf : fm.getLesFraisHF()) {
                // Récupération des frais hors forfait du mois en cours
                listFraisHF = fhf.convertirFraisHFList() ;
                // Ajout de l'id utilisateur en première position de la liste et de la clef en deuxième
                listFraisHF.add(0, id.get("id")) ;
                listFraisHF.add(1, key.toString ()) ;
                // Envoi de la liste au serveur pour enregistrement des frais hors forfait de ce mois
                accesDistant.envoiDistant("addUneLigneFraisHF", new JSONArray(listFraisHF)) ;
            }
        }
    }
    /**
     * Supprime un frais hors forfait de la BDD distante, s'il existe
     * @param key "AAAAMM"
     * @param motif
     * @param jour
     * @param montant
     */
	public void supprFraisHFDistant (String key, String motif, String jour, String montant) {
        // Création de la list compatible JSON avec les infos du frais hors forfait à supprimer
        List list = new ArrayList<>() ;
        list.add(id.get("id")) ;
        list.add(key) ;
        list.add(motif) ;
        list.add(jour) ;
        list.add(montant) ;
        // Envoie de la requête de suppression
        accesDistant.envoiDistant("supprFraisHF", new JSONArray(list)); ;
    }
    /**
     * Fonction qui traite les données récupérées depuis le serveur distant pour les mettre en forme dans la Hashtable listFraisMois
     * et enregistrement en local
     * @param lesDonnees
     */
    public void recupererFraisDistant (JSONArray lesDonnees) {
        // Variables pour simplifier la compréhension de la manipulation des données JSON recup
        JSONObject jObj ;
        Integer v_key ;
        Integer v_annee ;
        Integer v_mois ;
        String v_typeFrais ;
        Integer v_qte ;
        try {
            Integer l = lesDonnees.length() ; // TAG_DEBUG
            Log.d("LENGTH", l.toString()) ; // TAG_DEBUG
            // On parcourt toutes les lignes du tableau
            for (int i = 0;i<lesDonnees.length();i++) {
                // On récupére la ligne courante
                jObj = lesDonnees.getJSONObject(i) ;
                Log.d ("JOBJ", jObj.toString()) ; // TAG_DEBUG
                // On remplit les différents variables
                v_key = jObj.getInt("mois") ;
                v_annee = Integer.parseInt(v_key.toString().substring(0, 4)) ;
                v_mois = Integer.parseInt(v_key.toString().substring(4, 6)) ;
                v_typeFrais = jObj.getString("idFraisForfait") ;
                v_qte = jObj.getInt("quantite") ;
                if (!listFraisMois.containsKey(v_key)) {
                    Log.d("listFraisMois", "MOIS INTROUVABLE") ;
                    // Creation du mois et de l'annee s'ils n'existent pas déjà
                    listFraisMois.put(v_key, new FraisMois(v_annee, v_mois)) ;
                }
                // Switch selon le type de frais à enregistrer
                if (v_typeFrais.equals("REP")) {
                    listFraisMois.get(v_key).setRepas(v_qte);
                } else if (v_typeFrais.equals("NUI")) {
                    listFraisMois.get(v_key).setNuitee(v_qte);
                } else if (v_typeFrais.equals("KM")) {
                    listFraisMois.get(v_key).setKm(v_qte);
                } else if (v_typeFrais.equals("ETP")) {
                    listFraisMois.get(v_key).setEtape(v_qte);
                }
                Log.d("LISTFRAISMOIS", listFraisMois.toString()) ; // TAG_DEBUG
            }
        } catch (JSONException e) { // Si pour une raison quelconque le JSON est illisible
            // Affichage de l'erreur et récupération des infos en local
            Log.d("Erreur JSON : ",e.getMessage()) ;
            this.recupererLocal(app);
            return ;
        }
        // On enregistre la liste des frais fraichement récupérée en local
        this.enregistrerLocal(app);
        // On remet la modif à false car l'enregistrement local s'est fait à partir des données distantes, tout est donc à jour
        modif = false ;
        // On envoie la requête de récupération des frais hors forfaits
        List list = new ArrayList <>() ;
        list.add(id.get("id")) ;
        accesDistant.envoiDistant("recupLesFraisHF", new JSONArray(list));
    }
    /**
     * Fonction qui traite les données récupérées depuis le serveur distant pour les mettre en forme FraisHf dans le dictionnaire
     * de frais Hashtable <Integer,FraisMois>
     * @param lesDonnees
     */
    public void recupererFraisHFDistant (JSONArray lesDonnees) {
        // Variables pour simplifier la compréhension de la manipulation des données JSON recup
        JSONObject jObj ;
        Integer v_key ;
        String v_libelle ;
        String v_date ;
        Integer v_jour ;
        Integer v_montant ;
        FraisHf fHF ;
        try {
            // On parcourt toutes les lignes du tableau
            for (int i = 0;i<lesDonnees.length();i++) {
                // On remplit les différents variables
                jObj = lesDonnees.getJSONObject(i) ;
                v_key = jObj.getInt("mois") ;
                v_libelle = jObj.getString("libelle") ;
                v_date = jObj.getString("date") ;
                v_montant = jObj.getInt("montant") ;
                v_jour = Integer.parseInt(v_date.substring(8)) ;
                fHF = new FraisHf(v_montant, v_libelle, v_jour) ;
                // Creation du frais hors forfait s'il n'existe pas déjà
                if (!listFraisMois.get(v_key).getLesFraisHF().contains(fHF)) {
                    listFraisMois.get(v_key).getLesFraisHF().add(fHF) ;
                }
                Log.d("FraisHF", fHF.toString ()) ; // TAG_DEBUG
            }
        } catch (JSONException e) {
            Log.d("Erreur JSON : ", e.getMessage()) ;
            this.recupererLocal(app) ;
            return ;
        }
        // On enregistre l'état des frais en local, on remet modif
        this.enregistrerLocal(app) ;
        // On remet la modif à false car l'enregistrement local s'est fait à partir des données distantes, tout est donc à jour
        modif = false ;
        ((ConnexionActivity)app).endChargement() ;
        this.retourMenu (app) ;
    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Enregistrement dans la zone de texte et dans la liste de la nouvelle qte, à la date choisie
     * @param txtView afficheur textuel où afficher les informations
     * @param annee
     * @param mois
     * @param qte quantité à enregistrer
     * @param typeFrais type de frais à enregistrer repas/nuit/km/etape
     */
    public void enregNewQte(EditText txtView, int annee, int mois, int qte, String typeFrais) {
        // Enregistrement dans la zone de texte
        txtView.setText(String.valueOf(qte)) ;
        // Enregistrement dans la liste
        int key = annee*100+mois ;
        if (!listFraisMois.containsKey(key)) {
            // Creation du mois et de l'annee s'ils n'existent pas déjà
            listFraisMois.put(key, new FraisMois(annee, mois)) ;
        }
        // Switch selon le type de frais à enregistrer
        switch (typeFrais) {
            case "repas" :
                listFraisMois.get(key).setRepas(qte);
                break ;
            case "nuit" :
                listFraisMois.get(key).setNuitee(qte);
                break ;
            case "km" :
                listFraisMois.get(key).setKm(qte);
                break ;
            case "etape" :
                listFraisMois.get(key).setEtape(qte);
                break ;
        }
    }
    /**
     * Affichage des informations du jour séléctionné si elles existent
     * @param datView datePicker où récupérer la date
     * @param txtView afficheur où afficher les infos récup.
     * @param typeFrais type de frais à récupérer
     */
    public int valoriseProprietes(DatePicker datView, EditText txtView, String typeFrais) {
        // Récupération de l'année et du mois
        int annee = datView.getYear() ;
        int mois = datView.getMonth() + 1 ;
        // Récupération de la qte correspondant au mois actuel
        int qte = 0 ;
        int key = annee*100+mois ;
        // Switch selon le type de frais à récupérer
        if (listFraisMois.containsKey(key)) {
            switch(typeFrais) {
                case "km":
                    qte = listFraisMois.get(key).getKm();
                    break ;
                case "repas":
                    qte = listFraisMois.get(key).getRepas();
                    break;
                case "nuit":
                    qte = listFraisMois.get(key).getNuitee();
                    break;
                case "etape":
                    qte = listFraisMois.get(key).getEtape();
                    break;
                default :
                    Log.d("ERREUR", "Type de frais non reconnu") ;
                    qte = -1 ;
                    break ;
            }
        }
        txtView.setText(String.valueOf(qte)) ;
        return qte ;
    }
    /**
     * Modification de l'affichage de la date (juste le mois et l'année, sans le jour)
     * @param datePicker datePicker a modifié
     */
    public void changeAfficheDate(DatePicker datePicker) {
        //try {
        DatePicker dp_mes = datePicker;
        int year    = dp_mes.getYear();
        int month   = dp_mes.getMonth();
        int day     = dp_mes.getDayOfMonth();

        dp_mes.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
            int daySpinnerId = Resources.getSystem().getIdentifier("day", "id", "android");
            if (daySpinnerId != 0)
            {
                View daySpinner = dp_mes.findViewById(daySpinnerId);
                if (daySpinner != null)
                {
                    daySpinner.setVisibility(View.GONE);
                }
            }
            int monthSpinnerId = Resources.getSystem().getIdentifier("month", "id", "android");
            if (monthSpinnerId != 0)
            {
                View monthSpinner = dp_mes.findViewById(monthSpinnerId);
                if (monthSpinner != null)
                {
                    monthSpinner.setVisibility(View.VISIBLE);
                }
            }
            int yearSpinnerId = Resources.getSystem().getIdentifier("year", "id", "android");
            if (yearSpinnerId != 0)
            {
                View yearSpinner = dp_mes.findViewById(yearSpinnerId);
                if (yearSpinner != null)
                {
                    yearSpinner.setVisibility(View.VISIBLE);
                }
            }
        } else {
            Field f[] = dp_mes.getClass().getDeclaredFields();
            for (Field field : f)
            {
                if(field.getName().equals("mDayPicker") || field.getName().equals("mDaySpinner"))
                {
                    field.setAccessible(true);
                    Object dayPicker = null;
                    try {
                        dayPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        assert ((View) dayPicker) != null;
                        ((View) dayPicker).setVisibility(View.GONE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if(field.getName().equals("mMonthPicker") || field.getName().equals("mMonthSpinner"))
                {
                    field.setAccessible(true);
                    Object monthPicker = null;
                    try {
                        monthPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        assert ((View) monthPicker) != null;
                        ((View) monthPicker).setVisibility(View.VISIBLE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
                if(field.getName().equals("mYearPicker") || field.getName().equals("mYearSpinner"))
                {
                    field.setAccessible(true);
                    Object yearPicker = null;
                    try {
                        yearPicker = field.get(dp_mes);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    try {
                        assert ((View) yearPicker) != null;
                        ((View) yearPicker).setVisibility(View.VISIBLE);
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    /**
     * Affiche un message Toast
     */
    public void message (String message) {
        // Nécessite au préalable d'avoir rempli le champ app du controleur
        Toast.makeText(app, message, Toast.LENGTH_SHORT).show() ;
    }
    /**
     * Fonction qui permet de retourner au menu, appelée dans toutes les sous activitées)
     * @param a activité appelante
     */
    public void retourMenu (AppCompatActivity a) {
        // Transmission du controleur en argument
        Intent monIntent = new Intent(a, MenuActivity.class) ;
        monIntent.putExtra("ctrl", this) ;
        // Ouverture de la nouvelle activité, fermeture de la précédente
        a.startActivity(monIntent);
        a.finish() ;
    }
    /**
     * Fonction qui déconnecte l'utilisateur :
     * - suppression des données locale
     * - retour page connexion
     */
    public void deconnexion () {
        // On vide les variables locales
        listFraisMois = null ;
        id = null ;
        // On vide les fichiers enregistrés en local
        accesLocal.viderLocal(app) ;
        // Retour page de connexion
        Intent monIntent = new Intent(app, ConnexionActivity.class) ;
        app.startActivity(monIntent) ;
        app.finish() ;
    }
}
