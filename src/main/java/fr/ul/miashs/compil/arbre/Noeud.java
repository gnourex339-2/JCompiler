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

import java.util.List;
import java.util.logging.Logger;

/**
 * Description : Classe qui représente à la fois un noeud et un arbre
 * Un attribut commun à toutes les noeuds est la catégorie (cat) 
 * Noeud est une classe abstraite (pas de constructeur)
 * @author Azim Roussanaly
 * Created at 26 févr. 2019
 */
public abstract class Noeud {
	private static final Logger LOG = Logger.getLogger(Noeud.class.getName());
	//les catégories de noeuds possibles
	public static enum Categories {
			PROG, FONCTION, BLOC,
			AFF, SI,  TQ, ECR, RET,
			PLUS, MOINS, DIV, MUL,
			SUP, INF, SUPE, INFE, EG, DIF,
			IDF, CONST, LIRE, APPEL
	}
	//attributs
	private Categories cat;
	private List<Noeud> fils;
	
	//abstract
	abstract public String  getLabel();
	
	//methodes
	/**
	 * ajouter un fils à un noeud
	 * @param f : fils
	 * @return booléen
	 */
	public boolean ajouterUnFils(Noeud f) {
		return getFils().add(f);
	}
	/**
	 * Ajouter une liste de fils
	 * @param l : liste de fils
	 * @return nombre de fils ajoutés
	 */
	public int ajouterDesFils(List<Noeud> l) {
		int res = 0;
		for (Noeud f : l) {
			if (ajouterUnFils(f)) {
				res++;
			}
		}
		return res;
	}
	/**
	 * tester si c'est une feuille
	 * @return booléen
	 */
	public boolean estFeuille() {
		if ((fils == null) || (fils.isEmpty())){
			return true;
		}
		return false;
	}
	@Override
	public String toString() {
		return getLabel();
	}
	
	
	//setters & getters
	public Categories getCat() {
		return cat;
	}
	public void setCat(Categories cat) {
		this.cat = cat;
	}
	public List<Noeud> getFils() {
		return fils;
	}
	public void setFils(List<Noeud> fils) {
		this.fils = fils;
	}

}
