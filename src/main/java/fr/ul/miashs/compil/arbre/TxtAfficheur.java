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
 * Classe qui permet d'afficher proprement un arbre
 * @author azim
 *
 */
public class TxtAfficheur {

	/**
	 * formatte le Noeud arbre
	 * @param arbre : racine de l'arbre à afficher
	 * @return String
	 */
	public static String formatter(Noeud arbre) {
		return formatter(arbre, 0);
	}

	/**
	 * affiche le Noeud arbre
	 * @param arbre
	 */
	public static void afficher(Noeud arbre) {
		System.out.println(formatter(arbre));
	}
	
	/**
	 * méthode récursive
	 * @param n : noeud
	 * @param niveau : indentation
	 * @return String
	 */
	private static String formatter(Noeud n, int niveau) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < niveau; i++) {
			if (i == (niveau - 1)) {
				buf.append("└─");				
			}else {
				buf.append("  ");				
			}
		}
		buf.append(n.getLabel());
		buf.append("\n");
		if (!n.estFeuille()) {
			for (Noeud x : n.getFils()) {
				buf.append(formatter(x, niveau + 1));
			}
		}
		return buf.toString();
	}

}
