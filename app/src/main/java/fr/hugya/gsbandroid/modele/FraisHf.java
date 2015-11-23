package fr.hugya.gsbandroid.modele;

import java.io.Serializable;

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
}
