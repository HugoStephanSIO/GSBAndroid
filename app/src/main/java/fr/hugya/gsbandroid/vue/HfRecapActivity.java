package fr.hugya.gsbandroid.vue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.controleur.Controleur;
import fr.hugya.gsbandroid.modele.FraisHf;
import fr.hugya.gsbandroid.outils.FraisHfAdapter;

/**
 * Classe gérant l'activité concernant l'affichage et la suppresion des frais hors forfaits
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class HfRecapActivity extends AppCompatActivity {
	// PROPRIETES :
	// -------------
	private Controleur controle ;


	// FONCTIONS REDEFINIES :
	// ----------------------
	/**
	 * Constructeur de l'activité : récupération du controleur, affichage de la liste et lancement des fonctions événementielles
	 * @param savedInstanceState
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hf_recap);
		// Récupération du controleur
		controle = (Controleur)getIntent().getSerializableExtra("ctrl") ;
		// Modification de l'affichage du DatePicker
		controle.changeAfficheDate((DatePicker) findViewById(R.id.datHfRecap)) ;
		// Valorisation des propriétés
		afficheListe() ;
        // Chargement des méthodes événementielles
		imgReturn_clic() ;
		dat_clic() ;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hf_recap, menu);
		return true;
	}


	// FONCTIONS EVENEMENTIELLES
	// -------------------------
	/**
	 * Sur la selection de l'image : retour au menu principal
	 */
    private void imgReturn_clic() {
    	findViewById(R.id.imgHfRecapReturn).setOnClickListener(new ImageView.OnClickListener() {
    		public void onClick(View v) {
                controle.enregistrerLocal(HfRecapActivity.this) ;
    			controle.retourMenu(HfRecapActivity.this);
    		}
    	}) ;
    }
    /**
     * Sur le changement de date : mise à jour de l'affichage de la qte
     */
    private void dat_clic() {   	
    	final DatePicker uneDate = (DatePicker)findViewById(R.id.datHfRecap) ;
    	uneDate.init(uneDate.getYear(), uneDate.getMonth(), uneDate.getDayOfMonth(), new OnDateChangedListener() {
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				afficheListe();
			}
		});
    }


	// FONCTIONS OUTILS/AUTRES :
	// -------------------------
	/**
	 * Affiche la liste des frais hors forfaits de la date sélectionnée
	 */
	private void afficheListe() {
		Integer annee = ((DatePicker)findViewById(R.id.datHfRecap)).getYear() ;
		Integer mois = ((DatePicker)findViewById(R.id.datHfRecap)).getMonth() + 1 ;
		// récupération des frais HF pour cette date
		Integer key = annee*100 + mois ;
		ArrayList<FraisHf> liste ;
		if (controle.getListFraisMois().containsKey(key)) {
			liste = controle.getListFraisMois().get(key).getLesFraisHF() ;
		}else{
			liste = new ArrayList<>() ;
			// insertion dans la listview
		}
		ListView listView = (ListView)findViewById(R.id.lstHfRecap) ;
		FraisHfAdapter adapter = new FraisHfAdapter(HfRecapActivity.this, liste, key, controle) ;
		listView.setAdapter(adapter) ;
	}
}
