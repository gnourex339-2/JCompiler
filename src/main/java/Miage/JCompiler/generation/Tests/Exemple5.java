package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple5 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #5 : Ecriture directe ===");

        // 1. TABLE DES SYMBOLES
        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0);
        tds.ajouterGlobale("a", 100);
        tds.ajouterGlobale("b", 170);

        // 2. ARBRE SYNTAXIQUE
        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc();
        
        prog.ajouterUnFils(main);
        main.ajouterUnFils(bloc);

        // Instruction unique : ecrire( (a*2) + ((b-5)/3) )
        Ecrire ecr = new Ecrire();
        
        Plus plus = new Plus();
        
        // Gauche : a * 2
        Multiplication mul = new Multiplication();
        mul.setFilsGauche(new Idf("a"));
        mul.setFilsDroit(new Const(2));
        plus.setFilsGauche(mul);
        
        // Droite : (b - 5) / 3
        Division div = new Division();
        Moins moins = new Moins();
        moins.setFilsGauche(new Idf("b"));
        moins.setFilsDroit(new Const(5));
        div.setFilsGauche(moins);
        div.setFilsDroit(new Const(3));
        
        plus.setFilsDroit(div);
        
        // On attache tout le gros calcul directement à "ecrire"
        ecr.ajouterUnFils(plus);
        
        bloc.ajouterUnFils(ecr); // Ajout au bloc

        // 3. GÉNÉRATION
        Generateur gen = new Generateur();
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
        TxtAfficheur.afficher(prog);
    }
}