package fr.hugya.gsbandroid.vue;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.Serializable;
import java.util.Hashtable;

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.controleur.Controleur;

/**
 * Classe gérant l'activité concernant l'identification de l'utilisateur
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class ConnexionActivity extends AppCompatActivity implements Serializable {
    // PROPRIETES :
    // ------------
    private Controleur controle ;


    // FONCTIONS REDEFINIES :
    // ----------------------
    /**
     * Fonction appelée au lancement de l'activity, vérifie si un profil est enregistré en local, si oui lance le menu, sinon
     * affiche le formulaire de connexion et lancement des fonctions événementielles
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        // Création du controleur qui vérifie si un profil a été enregistré en local
        controle = new Controleur (ConnexionActivity.this) ;
        if (controle.estCo()) { // Si oui on ouvre directement le menu
            controle.syncDown(ConnexionActivity.this) ;
            controle.retourMenu (ConnexionActivity.this) ;
        }
        // Fonction événementielle
        cmdValider_clic () ;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_connexion, menu);
        return true;
    }


    // FONCTIONS EVENEMENTIELLES :
    // ---------------------------
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton valider
     */
    private void cmdValider_clic() {
        findViewById(R.id.cmdConnexionValider).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                // Récupération des saisies utilisateurs et mise en forme en dictionnaire
                String login = ((EditText) findViewById(R.id.txtLogin)).getText().toString();
                String password = ((EditText) findViewById(R.id.txtPassword)).getText().toString();
                Hashtable<String,String> tab = new Hashtable<String,String>();
                tab.put("login", login);
                tab.put("password", password);
                // Tentative d'identification
                controle.identifierUtilisateurDistant(tab, ConnexionActivity.this);
                return ;
            }
        });
    }
}
