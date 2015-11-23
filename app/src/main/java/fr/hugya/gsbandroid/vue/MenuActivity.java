package fr.hugya.gsbandroid.vue;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.controleur.Controleur;

/**
 * Class qui gère l'activité principale c'est à dire le menu
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class MenuActivity extends AppCompatActivity {
    // PROPRIETES :
    // ------------
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
        setContentView(R.layout.activity_menu);
        Intent monIntent = getIntent () ;
        controle = (Controleur)monIntent.getSerializableExtra("ctrl") ;
        controle.recupererLocal(MenuActivity.this);
        // Chargement des fonctions événementielles
        cmdMenu_clic(((Button)findViewById(R.id.cmdKm)), KmActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdHf)), HfActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdHfRecap)), HfRecapActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdRepas)), RepasActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdNuitee)), NuitActivity.class) ;
        cmdMenu_clic(((Button)findViewById(R.id.cmdEtape)), EtapeActivity.class) ;
        cmdTransfert_clic() ;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
                intent.putExtra("ctrl", controle) ;
                // Ouverture de l'activité
                startActivity(intent);
                finish() ;
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton pour transférer les données à la base distante
     */
    private void cmdTransfert_clic() {
    	findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                controle.syncUp(MenuActivity.this);
            }
        }) ;
    }
}
