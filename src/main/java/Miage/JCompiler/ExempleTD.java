
package Miage.JCompiler;

import fr.ul.miashs.compil.arbre.*;

/**
 * Exemple : Génération de code assembleur BETA
 * Expression : x = a*2 + (b-5)/3
 */
public class ExempleTD {
    public static void main(String[] args) {
        System.out.println("=== Construction de l'arbre pour: x = a*2 + (b-5)/3 ===\n");

        // Création des noeuds
        Prog prog = new Prog();
        Fonction principal = new Fonction("main");

        // x = a*2 + (b-5)/3
        Affectation aff = new Affectation();
        Idf x = new Idf("x");

        // a*2 + (b-5)/3
        Plus plus = new Plus();

        // a*2
        Multiplication mul = new Multiplication();
        Idf a = new Idf("a");
        Const c2 = new Const(2);

        // (b-5)/3
        Division div = new Division();
        Const c3 = new Const(3);

        // b-5
        Moins moins = new Moins();
        Idf b = new Idf("b");
        Const c5 = new Const(5);

        // Construction de l'arbre
        prog.ajouterUnFils(principal);
        principal.ajouterUnFils(aff);

        aff.setFilsGauche(x);
        aff.setFilsDroit(plus);

        plus.setFilsGauche(mul);
        plus.setFilsDroit(div);

        mul.setFilsGauche(a);
        mul.setFilsDroit(c2);

        div.setFilsGauche(moins);
        div.setFilsDroit(c3);

        moins.setFilsGauche(b);
        moins.setFilsDroit(c5);

        // Affichage de l'arbre
        System.out.println("--- Arbre abstrait ---");
        TxtAfficheur.afficher(prog);

        // Génération du code assembleur
        System.out.println("\n--- Code assembleur BETA généré ---\n");
        generateurAssembleur generateur = new generateurAssembleur();
        String codeAssembleur = generateur.generer(prog);
        System.out.println(codeAssembleur);

        GuiAfficheur.afficher(prog);
    }
}