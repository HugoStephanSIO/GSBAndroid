package fr.hugya.gsbandroid.vue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.Serializable;

import fr.hugya.gsbandroid.modele.FraisMois;
import fr.hugya.gsbandroid.controleur.Controleur;
import fr.hugya.gsbandroid.R;

/**
 * Classe gérant l'activité la saisie de frais hors forfaits
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class HfActivity extends AppCompatActivity implements Serializable {
	// PROPRIETES :
	// -------------
    private Controleur controle ;


    // FONCTIONS REDEFINIES :
    // ----------------------
    /**
     * Fonction appelée au lancement de l'activity, réglages et lancement des fonctions événementielles
     * @param savedInstanceState
     */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_hf);
        controle = (Controleur)getIntent().getSerializableExtra("ctrl") ;
		// mise à 0 du montant
		((EditText)findViewById(R.id.txtHf)).setText("0") ;
        // chargement des méthodes événementielles
		imgReturn_clic() ;
		cmdAjouter_clic() ;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.hf, menu);
		return true;
	}


    // FONCTIONS EVENEMENTIELLES
    // -------------------------
    /**
     * Fonction événementielle appelée en cas de clic sur l'image pour retourner au menu
     */
    private void imgReturn_clic() {
    	findViewById(R.id.imgHfReturn).setOnClickListener(new ImageView.OnClickListener() {
			public void onClick(View v) {
				controle.retourMenu(HfActivity.this);
			}
		}) ;
    }
    /**
     * Fonction événementelle appelée en cas de clic sur le bouton ajouter : enregistrement dans la liste et sérialisation
     */
    private void cmdAjouter_clic() {
    	findViewById(R.id.cmdHfAjouter).setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				if (Integer.parseInt(((EditText) findViewById(R.id.txtHf)).getText().toString()) == 0) {
					controle.setApp(HfActivity.this);
					controle.message("Veuillez saisir un montant !");
					return ;
				}
				if (((EditText)findViewById(R.id.txtHfMotif)).getText().toString().equals("")) {
					controle.setApp(HfActivity.this);
					controle.message("Veuillez saisir un motif !") ;
					return ;
				}
				enregListe();
				Toast.makeText(HfActivity.this, "Frais hors forfait enregistré", Toast.LENGTH_SHORT).show();
				controle.enregistrerLocal(HfActivity.this);
				controle.retourMenu(HfActivity.this);
			}
		}) ;
    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
	/**
	 * Enregistrement dans la liste du nouveau frais hors forfait
	 */
	private void enregListe() {
		// récupération des informations saisies
		Integer annee = ((DatePicker)findViewById(R.id.datHf)).getYear() ;
		Integer mois = ((DatePicker)findViewById(R.id.datHf)).getMonth() + 1 ;
		Integer jour = ((DatePicker)findViewById(R.id.datHf)).getDayOfMonth() ;
		Integer montant = Integer.parseInt(((EditText)findViewById(R.id.txtHf)).getText().toString()) ;
		String motif = ((EditText)findViewById(R.id.txtHfMotif)).getText().toString() ;
		// enregistrement dans la liste
		Integer key = annee*100+mois ;
		if (!controle.getListFraisMois().containsKey(key)) {
			// creation du mois et de l'annee s'ils n'existent pas déjà
			controle.getListFraisMois().put(key, new FraisMois(annee, mois)) ;
		}
		controle.getListFraisMois().get(key).addFraisHf(montant, motif, jour) ;
	}
}
