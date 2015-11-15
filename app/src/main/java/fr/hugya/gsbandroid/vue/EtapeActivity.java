package fr.hugya.gsbandroid.vue;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.controleur.Global;
import fr.hugya.gsbandroid.modele.Serializer;

/**
 * Classe gérant l'activité concernant la saisie des frais d'étapes
 * @author Hugo Stéphan
 */
public class EtapeActivity extends AppCompatActivity {
    // PROPRIETEES :
    // -------------
    // informations affichées dans l'activity
    private int annee ;
    private int mois ;
    private int qte ;
    private int wt;


    // FONCTIONS REDEFINIES :
    // ----------------------
    /**
     * Fonction appelée au lancement de l'activity, réglages et lancement des fonctions événementielles
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_etape);
        // modification de l'affichage du DatePicker
        Global.changeAfficheDate((DatePicker) findViewById(R.id.datEtape)) ;
        // valorisation des propriétés
        valoriseProprietes() ;
        ((EditText)findViewById(R.id.txtEtape)).setKeyListener(null);
        // chargement des méthodes événementielles
        imgReturn_clic() ;
        cmdValider_clic() ;
        cmdPlus_clic() ;
        cmdMoins_clic() ;
        dat_clic() ;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_etape, menu);
        return true;
    }


    // FONCTIONS EVENEMENTIELLES
    // -------------------------
    /**
     * Fonction événementielle appelée en cas de clic sur l'image pour retourner au menu
     */
    private void imgReturn_clic() {
        findViewById(R.id.imgEtapeReturn).setOnClickListener(new ImageView.OnClickListener() {
            public void onClick(View v) {
                Global.retourMenu(EtapeActivity.this);
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur l'image pour retourner au menu
     */
    private void cmdValider_clic() {
        findViewById(R.id.cmdEtapeValider).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                Serializer.serialize(Global.filename, Global.listFraisMois, EtapeActivity.this) ;
                Toast.makeText(EtapeActivity.this,"Etapes enregistrées",Toast.LENGTH_SHORT).show () ;
                Global.retourMenu(EtapeActivity.this);
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton plus : ajout de 1 étape
     */
    private void cmdPlus_clic() {
        findViewById(R.id.cmdEtapePlus).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                qte+=1 ;
                Global.enregNewQte(((EditText) findViewById(R.id.txtEtape)), annee, mois, qte, "etape");
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de clic sur le bouton moins : enléve 1 étape si c'est possible
     */
    private void cmdMoins_clic() {
        findViewById(R.id.cmdEtapeMoins).setOnClickListener(new Button.OnClickListener() {
            public void onClick(View v) {
                qte = Math.max(0, qte-1) ; // suppression de 1 si possible
                Global.enregNewQte(((EditText) findViewById(R.id.txtEtape)), annee, mois, qte, "etape");
            }
        }) ;
    }
    /**
     * Fonction événementielle appelée en cas de changement de date : mise à jour de l'affichage de la qte
     */
    private void dat_clic() {
        final DatePicker uneDate = (DatePicker)findViewById(R.id.datEtape) ;
        uneDate.init(uneDate.getYear(), uneDate.getMonth(), uneDate.getDayOfMonth(), new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                valoriseProprietes();
            }
        });
    }

    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Valorisation des propriétés avec les informations affichées
     */
    private void valoriseProprietes() {
        annee = ((DatePicker)findViewById(R.id.datEtape)).getYear() ;
        mois = ((DatePicker)findViewById(R.id.datEtape)).getMonth() + 1 ;
        // récupération de la qte correspondant au mois actuel
        qte = 0 ;
        int key = annee*100+mois ;
        if (Global.listFraisMois.containsKey(key)) {
            qte = Global.listFraisMois.get(key).getEtape() ;
        }
        ((EditText)findViewById(R.id.txtEtape)).setText(String.valueOf(qte)) ;
    }
}