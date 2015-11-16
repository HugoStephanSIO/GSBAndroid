package fr.hugya.gsbandroid.controleur;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Hashtable;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.modele.FraisMois;
import fr.hugya.gsbandroid.modele.Serializer;
import fr.hugya.gsbandroid.vue.MainActivity;

/**
 * Classe controleur contenant diverses fonctions utilitaires
 * @author Hugo Stéphan
 */
public class Controleur implements Serializable {
	public Hashtable<Integer, FraisMois> getListFraisMois() {
		return listFraisMois;
	}

	// PROPRIETEES :
    // -------------
	// tableau d'informations mémorisées
	private Hashtable<Integer, FraisMois> listFraisMois = new Hashtable<>() ;
	// fichier contenant les informations sérialisées
	private final String filename = "savev1-1.fic" ;

	public Controleur (Context context) {
		listFraisMois = (Hashtable<Integer, FraisMois>) Serializer.deSerialize(filename, context) ;
		// si rien n'a été récupéré, il faut créer la liste
		if (listFraisMois==null) {
			listFraisMois = new Hashtable<>();
		}
	}
	public void enregistrerLocal (Context context) {
		// Enregistrement de l'état des frais
		Serializer.serialize(filename, listFraisMois, context) ;
	}
	/**
	 * Fonction qui permet de retourner au menu appelée dans toutes les sous activity)
	 */
	public void retourMenu (AppCompatActivity a) {
		Intent monIntent = new Intent(a, MainActivity.class) ;
		monIntent.putExtra("ctrl", this) ;
		a.startActivity(monIntent) ;
		a.finish() ;
	}
    /**
     * Enregistrement dans la zone de texte et dans la liste de la nouvelle qte, à la date choisie
     */
    public void enregNewQte(EditText txtRepas, int annee, int mois, int qte, String typeFrais) {
        // enregistrement dans la zone de texte
        txtRepas.setText(String.valueOf(qte)) ;
        // enregistrement dans la liste
        int key = annee*100+mois ;
        if (!listFraisMois.containsKey(key)) {
            // creation du mois et de l'annee s'ils n'existent pas déjà
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
	 * Modification de l'affichage de la date (juste le mois et l'année, sans le jour)
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
				} else { //Older SDK versions
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
		//    }
		//} catch (SecurityException e) {
		//    Log.d("ERROR", e.getMessage());
		//} catch (IllegalArgumentException e) {
		 //   Log.d("ERROR", e.getMessage());
		//} catch (IllegalAccessException e) {
		 //   Log.d("ERROR", e.getMessage());
	//	}
	}
	
}
