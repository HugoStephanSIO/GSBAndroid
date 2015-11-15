package fr.hugya.gsbandroid.modele;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.BaseAdapter;

import fr.hugya.gsbandroid.R;
import fr.hugya.gsbandroid.controleur.Global;

/**
 * Adapter pour la liste des frais hors forfait d'un mois donné
 * @author Hugo Stéphan
 */
public class FraisHfAdapter extends BaseAdapter {
    // PROPRIETEES :
    // -------------
	ArrayList<FraisHf> lesFrais ; // liste des frais du mois
	LayoutInflater inflater ;
	Integer key ;  // annee et mois (clé dans la liste)
	Context context ; // contexte pour gérer la sérialisation
	
	/**
	 * Constructeur de l'adapter pour valoriser les propriétés
	 * @param context
	 * @param lesFrais
	 * @param key
	 */
	public FraisHfAdapter(Context context, ArrayList<FraisHf> lesFrais, Integer key) {
		inflater = LayoutInflater.from(context) ;
		this.lesFrais = lesFrais ;
		this.key = key ;
		this.context = context ;
	}
	
	/**
	 * retourne le nombre d'éléments de la listview
	 */
	@Override
	public int getCount() {
		return lesFrais.size() ;
	}

	/**
	 * retourne l'item de la listview à un index précis
	 */
	@Override
	public Object getItem(int index) {
		return lesFrais.get(index) ;
	}

	/**
	 * retourne l'index de l'élément actuel
	 */
	@Override
	public long getItemId(int index) {
		return index;
	}

	/**
	 * structure contenant les éléments d'une ligne
	 */
	private class ViewHolder {
		TextView txtListJour ;
		TextView txtListMontant ;
		TextView txtListMotif ;
        ImageView imgSuppr ;
	}
	
	/**
	 * Affichage dans la liste
	 */
	@Override
	public View getView(int index, View convertView, ViewGroup parent) {
		ViewHolder holder ;
		if (convertView == null) {
			holder = new ViewHolder() ;
			convertView = inflater.inflate(R.layout.layout_liste, null) ;
            // Création éventuelle des différents éléments dans la liste
			holder.txtListJour = (TextView)convertView.findViewById(R.id.txtListJour) ;
			holder.txtListMontant = (TextView)convertView.findViewById(R.id.txtListMontant) ;
			holder.txtListMotif = (TextView)convertView.findViewById(R.id.txtListMotif) ;
            holder.imgSuppr = (ImageView)convertView.findViewById(R.id.imgListSuppr) ;
			convertView.setTag(holder) ;
		}else{
			holder = (ViewHolder)convertView.getTag();
		}
        // Remplissage des éléments de la liste
		holder.txtListJour.setText(lesFrais.get(index).getJour().toString()) ;
		holder.txtListMontant.setText(lesFrais.get(index).getMontant().toString()) ;
		holder.txtListMotif.setText(lesFrais.get(index).getMotif()) ;
        holder.imgSuppr.setTag(index) ;

        // Fonction événementielle appelée en cas de clique sur le bouton supprimer
        holder.imgSuppr.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                int index = (Integer)v.getTag() ;
                lesFrais.remove(index) ;
                Global.listFraisMois.get(key).setLesFraisHf(lesFrais) ;
                notifyDataSetChanged();
            }
        });
		return convertView ;
	}
}
