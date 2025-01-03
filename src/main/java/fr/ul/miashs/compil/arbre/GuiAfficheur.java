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

import javax.swing.JDialog;
import javax.swing.JTree;
import javax.swing.JScrollPane;
import javax.swing.tree.DefaultMutableTreeNode;

public class GuiAfficheur {
    /**
     * affiche le Noeud arbre
     * @param n : la racine d'un arbre
     */
    public static void afficher(Noeud n) {
        JTree tree = new JTree(convertir(n));
        JScrollPane scrollPane = new JScrollPane(tree);
        JDialog dialog = new JDialog();
        dialog.setTitle("Affichage arbre");
        dialog.setSize(300, 400);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.add(scrollPane);
        dialog.setVisible(true);
    }

    /**
     * Convertir Noeud en DefaultMutableTreeNode
     * @param noeud
     * @return DefaultMutableTreeNode
     */
    public static DefaultMutableTreeNode convertir(Noeud noeud) {
        if (noeud == null) {return null;}
        DefaultMutableTreeNode res = new DefaultMutableTreeNode(noeud.toString());
        try {
            for (Noeud f: noeud.getFils()) {
                res.add(convertir(f));
            }
        } catch (Exception e) {
            //skip
        }
        return res;
    }
}

