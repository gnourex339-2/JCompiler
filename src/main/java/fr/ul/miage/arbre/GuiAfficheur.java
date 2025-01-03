package fr.ul.miage.arbre;

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

