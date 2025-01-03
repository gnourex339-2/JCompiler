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

import java.util.ArrayList;

/**
 * Description :
 * @author Azim Roussanaly
 * Created at 28 févr. 2019
 */
public class Si extends NoeudInt {
	static int compteur = 0;
	//constructeurs
	public Si() {
		this(compteur);
		compteur++;
	}
	
	public Si(int valeur) {
		setValeur(valeur);
		setCat(Categories.SI);
		setFils(new ArrayList<Noeud>(3));
		this.getFils().add(0, null);// 0 = condition
		this.getFils().add(1, new Bloc());// 1= alors (vide par défaut)
		this.getFils().add(2, new Bloc());// 2 = sinon
	}
	//methodes
	/**
	 * @return la condition
	 */
	public Noeud getCondition() {
		return getFils().get(0);
	}
	/**
	 * @return le bloc si vrai
	 */
	public Bloc getBlocAlors() {
		return (Bloc) getFils().get(1);
	}
	/**
	 * @return le bloc si faux
	 */
	public Bloc getBlocSinon() {
		return (Bloc) getFils().get(2);
	}
	/**
	 * Ajoute une condition
	 */
	public void setCondition(Noeud n) {
		getFils().set(0, n);
	}
	/**
	 * Ajoute un bloc si vrai
	 */
	public void setBlocAlors(Bloc n) {
		getFils().set(1, n);
	}
	/**
	 * Ajoute un bloc si faux
	 */
	public void setBlocSinon(Bloc n) {
		getFils().set(2, n);
	}
}
