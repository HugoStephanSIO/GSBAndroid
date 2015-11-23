package fr.hugya.gsbandroid.outils;

import android.os.AsyncTask;

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
import java.util.Hashtable;

/**
 * Classe utilitaire de type thread pour simplifier l'accès aux pages PHP et donc à la BDD distante
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class AccesHTTP extends AsyncTask<String, Integer, Long> {
	// PROPRIETES :
	// -------------
	public String ret=""; // Enregistre le retour du serveur
	private ArrayList<NameValuePair> parametres; // Paramétres éventuels à envoyer


    // CONSTRUCTEURS :
    // --------------
	public AccesHTTP(){
		parametres = new ArrayList<NameValuePair>();
	}


    // FONCTIONS REDEFINIES :
    // ----------------------
    /**
     * Fonction qui gère la connexion au serveur à proprement parler en arrière plan
     * @param urls
     * @return
     */
	@Override
	protected Long doInBackground(String... urls) {
		// TODO Auto-generated method stub
		
		HttpClient cnxHttp = new DefaultHttpClient();
		HttpPost paramCnx = new HttpPost(urls[0]);
		
		try {
		    paramCnx.setEntity(new UrlEncodedFormEntity(parametres));
		    HttpResponse reponse = cnxHttp.execute(paramCnx);
		    ret = EntityUtils.toString(reponse.getEntity());
		
		} catch (ClientProtocolException e) {
		    // TODO Auto-generated catch block
		} catch (IOException e) {
		    // TODO Auto-generated catch block
		}
		return null;
	}
	/**
	 * Fonction à redéfinir dans la classe qui l'appelle pour définir le code qui s'éxécute au moment de la transmission au serveur
	 * @param result
	 */
	@Override
	protected void onPostExecute(Long result) {

    }


    // FONCTIONS OUTILS/AUTRES :
    // -------------------------
    /**
     * Ajoute un paramètre à transmettre à la page PHP serveur
     * @param nom
     * @param valeur
     */
    public void addParam(String nom, String valeur){
        parametres.add(new BasicNameValuePair(nom,valeur));
    }
	/**
	 * Surcharge
	 * @param id
	 */
	public void addParam(Hashtable<String,String> id) {
		addParam("login", id.get("login"));
		addParam("password", id.get("password"));
	}
}
