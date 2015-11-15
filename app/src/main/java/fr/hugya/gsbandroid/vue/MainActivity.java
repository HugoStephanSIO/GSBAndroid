package fr.hugya.gsbandroid.vue;

import java.util.Hashtable;

//import org.apache.http.client.ClientProtocolException;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.json.JSONObject;

import android.os.Bundle;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

import fr.hugya.gsbandroid.modele.FraisMois;
import fr.hugya.gsbandroid.controleur.Global;
import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.modele.Serializer;

/**
 * Class qui gère l'activité principale c'est à dire le menu
 * @author Hugo Stéphan
 */
public class MainActivity extends AppCompatActivity {
    // FONCTIONS REDEFINIES :
    // ----------------------
    /**
     * Fonction appelée au lancement de l'activity, réglages et lancement des fonctions événementielles
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // récupération des informations sérialisées
        recupSerialize() ;
        // chargement des fonctions événementielles
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
                // ouvre l'activité
                Intent intent = new Intent(MainActivity.this, classe);
                startActivity(intent);
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton pour transférer les données à la base distante
     */
    private void cmdTransfert_clic() {
    	findViewById(R.id.cmdTransfert).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // envoi les informations sérialisées vers le serveur
                // ---------------------------
                // /!\ A FAIRE /!\
                // ---------------------------
            }
        }) ;
    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Récupére la sérialisation si elle existe
     */
    private void recupSerialize() {
        Global.listFraisMois = (Hashtable<Integer, FraisMois>) Serializer.deSerialize(Global.filename, MainActivity.this) ;
        // si rien n'a été récupéré, il faut créer la liste
        if (Global.listFraisMois==null) {
            Global.listFraisMois = new Hashtable<>() ;
        }
    }
}
