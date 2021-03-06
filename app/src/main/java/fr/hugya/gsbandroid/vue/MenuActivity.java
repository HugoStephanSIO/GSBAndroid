package fr.hugya.gsbandroid.vue;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.io.Serializable;

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.controleur.Controleur;

/**
 * Class qui gère l'activité principale c'est à dire le menu
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class MenuActivity extends AppCompatActivity implements Serializable {
    // PROPRIETES :
    // ------------
    private Controleur controle ;
    private ProgressDialog pDial ;
    private final String CLASS_TAG = this.getClass().getName() ;
    private boolean charging = false ;


    // FONCTIONS REDEFINIES :
    // ----------------------
    /**
     * Fonction appelée au lancement de l'activity, réglages et lancement des fonctions événementielles
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState) ;
        setContentView(R.layout.activity_menu) ;
        Intent monIntent = getIntent () ;
        controle = (Controleur)monIntent.getSerializableExtra("ctrl") ;
        controle.recupererLocal(MenuActivity.this) ;

        // Chargement des fonctions événementielles
        cmdMenu_clic(((Button)findViewById(R.id.cmdKm)), KmActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdHf)), HfActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdHfRecap)), HfRecapActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdRepas)), RepasActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdNuitee)), NuitActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdEtape)), EtapeActivity.class) ;
        cmdTransfert_clic() ;
        cmdDeconnexion_clic() ;

        // Si des modifs ont été faites en locales et nécessitent une syncUp, on affiche un message
        if (controle.getModif()) {
            (findViewById(R.id.lblModif)).setVisibility(View.VISIBLE) ;
        }
        else {
            (findViewById(R.id.lblModif)).setVisibility(View.GONE) ;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu) ;
        return true ;
    }


    // FONCTIONS EVENEMENTIELLES
    // -------------------------
    /**
     * Fonction événementielle appelée en cas de clic sur un bouton du menu : ouverture de l'activité correspondante
     */
    private void cmdMenu_clic(Button button, final Class classe) {
    	button.setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // Transmission du controleur à l'activité secondaire
                Intent intent = new Intent(MenuActivity.this, classe);
                intent.putExtra("ctrl", controle);
                // Ouverture de l'activité
                startActivity(intent);
                finish();
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton pour transférer les données à la base distante
     */
    private void cmdTransfert_clic() {
    	findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                startChargement();
                controle.setApp(MenuActivity.this);
                controle.syncUp(MenuActivity.this);
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton de déconnexion
     */
    private void cmdDeconnexion_clic() {
        findViewById(R.id.cmdDeconnexion).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                controle.setApp(MenuActivity.this);
                controle.deconnexion();
            }
        });
    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Fonction qui initie un chargement (de synchronisation) via une ProgressDialog
     */
    public void startChargement () {
        if (!charging) { // WEIRD ISN'T IT
            Log.d(CLASS_TAG, "start Chargement");
            pDial = new ProgressDialog(MenuActivity.this);
            pDial.setMessage("Synchronisation en cours...");
            pDial.show();
            charging = true ;
        }
    }
    /**
     * Fonction qui met fin à un éventuel chargement en cours
     */
    public void endChargement () {
        Log.d(CLASS_TAG, "endChargement") ;
        if(pDial != null && pDial.isShowing()) {
            Log.d(CLASS_TAG, "pDial!=null&showing") ;
            pDial.dismiss() ;
            pDial = null ;
            charging = false ;
        }
    }
    /**
     * Fonction rafraîchit l'affichage de l'activité pour mettre à jour le message de modif
     */
    public void refresh () {
        Intent intent = getIntent();
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
