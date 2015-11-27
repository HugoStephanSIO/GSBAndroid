package fr.hugya.gsbandroid.modele;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe métier contenant la description d'un frais hors forfait
 * @author Hugo Stéphan, Suriya Sammandamourthy
 */
public class FraisHf  implements Serializable {
	// PROPRIETES :
	// -------------
	private Integer montant ;
	private String motif ;
	private Integer jour ;


	// ACCESSEURS :
	// ------------
	public Integer getMontant() {
		return montant;
	}
	public String getMotif() {
		return motif;
	}
	public Integer getJour() {
		return jour;
	}


	// CONSTRUCTEURS :
	// --------------
	public FraisHf(Integer montant, String motif, Integer jour) {
		this.montant = montant ;
		this.motif = motif ;
		this.jour = jour ;
	}


	// FONCTION OUTILS/AUTRES :
	// ------------------------
	/**
	 * Retourne une version List<String> compatible JSON du frais hors forfait
	 * @return la liste compatible JSON
	 */
	public List<String> convertirFraisHFList () {
		List <String> list = new ArrayList<String>() ;
		list.add(montant.toString()) ;
		list.add(motif) ;
		list.add(jour.toString()) ;
		return list ;
	}
}
