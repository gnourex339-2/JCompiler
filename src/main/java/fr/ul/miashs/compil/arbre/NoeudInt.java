/*
 *  License and Copyright:
 *
 *  This file is part of arbre  project.
 *
 * MIT License:
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * Copyright 2025 by IDMC, Université de Lorraine (azim)
 * All right reserved
 *
 */
package fr.ul.miashs.compil.arbre;

/**
 * Description :
 * Classe non publique des noeuds n'ayant pas d'attributs supplémentaires
 * Exemples : CONST, SI, TQ
 * @author Azim Roussanaly
 * Created at 28 févr. 2019
 */
class NoeudInt extends Noeud {
	//attribut
	private int valeur;
	//constructeur
	protected NoeudInt() {
		setValeur(0);
	}
	/* (non-Javadoc)
	 * @see fr.ul.miashs.api.arbre.Noeud#getLabel()
	 */
	@Override
	public String getLabel() {
		StringBuilder buf = new StringBuilder();
		buf.append(getCat());
		buf.append("/");
		buf.append(valeur);
		return buf.toString();
	}
	//setters & getters
	public int getValeur() {
		return valeur;
	}
	public void setValeur(int valeur) {
		this.valeur = valeur;
	}

}
