package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple4 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #4 : Ecriture et Lecture ===");

        // 1. TABLE DES SYMBOLES
        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0);
        tds.ajouterGlobale("res", 0);

        // 2. ARBRE SYNTAXIQUE
        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc();
        
        prog.ajouterUnFils(main);
        main.ajouterUnFils(bloc);

        // Instruction 1 : res = (lire() * 2) + ((lire() - 3) / 5)
        Affectation aff = new Affectation();
        aff.setFilsGauche(new Idf("res")); 
        
        Plus plus = new Plus();
        
        // Gauche : lire() * 2
        Multiplication mul = new Multiplication();
        mul.setFilsGauche(new Lire());      // Utilisation du noeud Lire
        mul.setFilsDroit(new Const(2));
        plus.setFilsGauche(mul);
        
        // Droite : (lire() - 3) / 5
        Division div = new Division();
        Moins moins = new Moins();
        moins.setFilsGauche(new Lire());    // Utilisation du noeud Lire
        moins.setFilsDroit(new Const(3));
        div.setFilsGauche(moins);
        div.setFilsDroit(new Const(5));
        
        plus.setFilsDroit(div);
        aff.setFilsDroit(plus); 
        
        bloc.ajouterUnFils(aff); // Ajout de l'affectation au bloc

        // Instruction 2 : ecrire(res)
        Ecrire ecr = new Ecrire();
        
        ecr.ajouterUnFils(new Idf("res")); 
        bloc.ajouterUnFils(ecr); // Ajout de l'écriture au bloc

        // 3. GÉNÉRATION
        Generateur gen = new Generateur();
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
        TxtAfficheur.afficher(prog);
    }
}
