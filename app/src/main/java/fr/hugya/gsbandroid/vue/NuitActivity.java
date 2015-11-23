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

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.controleur.Controleur;

/**
 * Classe gérant l'activité concernant la saisie des frais de nuitée
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class NuitActivity extends AppCompatActivity {
    // PROPRIETES :
    // -------------
    // informations affichées dans l'activity
    private int annee ;
    private int mois ;
    private int qte ;
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
        setContentView(R.layout.activity_nuit);
        // Modification de l'affichage du DatePicker
        controle = (Controleur)getIntent().getSerializableExtra("ctrl") ;
        controle.changeAfficheDate((DatePicker) findViewById(R.id.datNuit)) ;
        // Valorisation éventuelle du champ d'entrée de text et impossibilité de l'éditer directement
        valoriserProprietes() ;
        // On empêche l'utilisateur de modifier le champ de texte sans passé par les boutons +/-
        ((EditText)findViewById(R.id.txtNuit)).setKeyListener(null);
        // Lancement des fonctions événementielles
        imgReturn_clic() ;
        cmdValider_clic() ;
        cmdPlus_clic() ;
        cmdMoins_clic() ;
        dat_clic() ;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_nuit, menu);
        return true;
    }


    // FONCTIONS EVENEMENTIELLES :
    // ---------------------------
    /**
     * Fonction événementielle appelée en cas de clic sur l'image pour retourner au menu
     */
    private void imgReturn_clic() {
        findViewById(R.id.imgNuitReturn).setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                controle.retourMenu(NuitActivity.this);
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur l'image pour retourner au menu
     */
    private void cmdValider_clic() {
        findViewById(R.id.cmdNuitValider).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                controle.enregistrerLocal(NuitActivity.this);
                Toast.makeText(NuitActivity.this, "Nuitées enregistrées", Toast.LENGTH_SHORT).show() ;
                controle.retourMenu(NuitActivity.this);
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton plus : ajout de 1 nuitée
     */
    private void cmdPlus_clic() {
        findViewById(R.id.cmdNuitPlus).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                qte+=1 ;
                controle.enregNewQte(((EditText) findViewById(R.id.txtNuit)), annee, mois, qte, "nuit");
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton moins : enléve 1 nuitée si c'est possible
     */
    private void cmdMoins_clic() {
        findViewById(R.id.cmdNuitMoins).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                qte = Math.max(0, qte - 1); // suppression de 1 si possible
                controle.enregNewQte(((EditText) findViewById(R.id.txtNuit)), annee, mois, qte, "nuit");
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de changement de date : mise à jour de l'affichage de la qte
     */
    private void dat_clic() {
        final DatePicker uneDate = (DatePicker)findViewById(R.id.datNuit) ;
        uneDate.init(uneDate.getYear(), uneDate.getMonth(), uneDate.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                valoriserProprietes();
            }
        });
    }


    // FONCTIONS OUTLS/AUTRES :
    // ------------------------
    /**
     * Fonction utilisée pour récupérer les informations
     */
    private void valoriserProprietes () {
        annee = ((DatePicker)findViewById(R.id.datNuit)).getYear() ;
        mois = ((DatePicker)findViewById(R.id.datNuit)).getMonth() + 1;
        qte = controle.valoriseProprietes(((DatePicker) findViewById(R.id.datNuit)), ((EditText) findViewById(R.id.txtNuit)), "nuit");
    }
}
