package fr.hugya.gsbandroid.controleur;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Hashtable;

import fr.hugya.gsbandroid.modele.AccesDistant;
import fr.hugya.gsbandroid.modele.AccesLocal;
import fr.hugya.gsbandroid.modele.FraisMois;
import fr.hugya.gsbandroid.vue.MenuActivity;

/**
 * Classe controleur faisant le lien entre les vues et le modèle (les données) en utilisant les outils
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class Controleur implements Serializable { // Serializable pour pouvoir être transmis entre les activitées
	// PROPRIETES :
    // ------------
	// Objet d'accès local
	private AccesLocal accesLocal ;
	// Objet d'accès distant
	private AccesDistant accesDistant ;
    // Logs de l'utilisateur
    private Hashtable<String,String> id ;
    public Hashtable<String,String> getId () {return id ;}
    // Statut de la connexion
    private boolean estCo = false ;
    public boolean estCo () { return estCo ; }
    // Tableau d'informations mémorisées en local
    private Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<>() ;
    public Hashtable<Integer, FraisMois> getListFraisMois() {
        return listFraisMois;
    }


    // CONSTRUCTEURS :
    // --------------
    public Controleur (Context context) {
        // Initialisation des objets du modéle
		accesDistant = new AccesDistant() ;
		accesLocal = new AccesLocal () ;
        // Si un profil est enregistré en local
        if(accesLocal.recupererProfilLocal(context)) {
            // Mise à jour du statut de la connexion et des logs actuel de l'utilisateur
            estCo = true ;
            id = accesLocal.getId() ;
            // Synchronisation descendante
            syncDown(context);
        }
	}


	// FONCTIONS OUTILS/AUTRES :
	// -------------------------
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
	}
    /**
     * Encapsulation de la procédure accesLocal.enregistrerUtilisateurLocal
     * @param context
     * @param tab identifiants de l'utilisateur à enregistrer localement
     */
    public void enregistrerUtilisateurLocal (Context context, Hashtable<String,String> tab) {
        accesLocal.enregistrerUtilisateurLocal(context, tab);
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
		a.startActivity(monIntent) ;
		a.finish() ;
	}
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
     * Fonction qui tente d'identifier l'utilisateur à partir des logs transmis en arguments
     * @param logs "login"/login "password/password
     */
    public void identifierUtilisateurDistant (Hashtable<String,String> logs) {
        // Tentative de connexion de l'utilisateur
        accesDistant.identifierUtilisateur(logs);
        // Mise à jour éventuelle des infos interne au controleur
        estCo = accesDistant.estCo() ;
        if (estCo){ // Si la connexion a réussie
            id = logs ;
        }
    }
    /**
     * Surcharge
     * @param login
     * @param password
     */
    public void identifierUtilisateurDistant (String login, String password) {
        Hashtable<String,String> logs = new Hashtable<String,String>();
        logs.put("login", login);
        logs.put("password", password);
        // Tentative de connexion de l'utilisateur
        this.identifierUtilisateurDistant(logs);
    }
    /**
     * Fonction qui récupére les données de frais de l'utilisateur
     * - implique une connexion au préalable
     * @param context
     */
    public void syncDown (Context context) {
        // On ne peut faire une synchronisation descendante qu'en étant correctement loggé
        if (id == null || id.size()!=2) {
            return ;
        }
		if (!estCo) {
			return ;
		}
        listFraisMois = accesDistant.recupererDistant (id) ;
        enregistrerLocal(context);
    }
    /**
     * Fonction qui envoie les données enregistrées localement à la base distante
     * @param context
     */
    public void syncUp (Context context) {
        // On ne peut faire une synchronisation montante qu'en étant correctement loggé
        if (id == null || id.size()!=2) {
            return ;
        }
        if (!estCo) {
            return ;
        }
        accesDistant.enregistrerDistant (listFraisMois) ;
    }
}
