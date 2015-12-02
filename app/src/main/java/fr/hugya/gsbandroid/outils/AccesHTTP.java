package fr.hugya.gsbandroid.outils;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;

import fr.hugya.gsbandroid.controleur.Controleur;

/**
 * Classe utilitaire, de type thread qui s'éxécute en parallèle (AsyncThread) de façon à ne pas interrompre le processus principal,
 * pour simplifier l'accès aux pages PHP et donc à la BDD distante
 * @author Hugo Stéphan, Suriya Sammandamourthy, Emds
 */
public class AccesHTTP extends AsyncTask<String, Integer, Long> {
	// PROPRIETES :
	// ------------
	public String ret="";
	private ArrayList<NameValuePair> parametres;
	public AsyncResponse delegate=null;
    private final String CLASS_TAG = this.getClass().getName() ;


	// CONSTRUCTEURS :
	// ---------------
	public AccesHTTP(){
		parametres = new ArrayList<>();
	}


	// FONCTIONS OUTILS/AUTRES :
	// -------------------------
	/**
	 * Ajout d'un paramètre à l'envoi HTTP sous la forme d'un couple clef(nom)/valeur, récupérable sur le serveur, en PHP :
	 * $_REQUEST[nom] = valeur
	 * @param nom
	 * @param valeur
	 */
	public void addParam(String nom, String valeur){
		parametres.add(new BasicNameValuePair(nom,valeur));
	}
	/**
	 * Fonction de connexion au serveur en tache de fond
	 * @param urls
	 * @return
	 */
	@Override
	protected Long doInBackground(String... urls)  {
		HttpClient cnxHttp = new DefaultHttpClient();
		HttpPost paramCnx = new HttpPost(urls[0]);
		// Tentative de connexion
		try {
			paramCnx.setEntity(new UrlEncodedFormEntity(parametres));
			HttpResponse reponse = cnxHttp.execute(paramCnx);
			ret = EntityUtils.toString(reponse.getEntity());

		} catch (ClientProtocolException e) {
			Log.d(CLASS_TAG, "Erreur ClientProtocol : " + e.toString()) ;
		} catch (IOException e) { // Connexion refusée
			Log.d(CLASS_TAG, "Erreur IOException : " + e.toString());
            Controleur.erreurConnexion = "Connexion au serveur " + urls[0] + " impossible !" ;
		}
		return null;
	}
	/**
	 * Au moment du retour du serveur, on termine le processus asynchrone qui gère la connexion au serveur
	 * @param result
	 */
	@Override
	protected void onPostExecute(Long result) {
		// ret contient l'information récupérée
		delegate.processFinish(this.ret.toString());
	}
}
