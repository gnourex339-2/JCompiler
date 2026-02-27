package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple7 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #7 : Conditionnelle Si ===");

        // 1. TABLE DES SYMBOLES
        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0);
        tds.ajouterGlobale("a", 1);
        tds.ajouterGlobale("b", 2);
        tds.ajouterGlobale("x", 0);

        // 2. ARBRE SYNTAXIQUE
        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc();
        
        prog.ajouterUnFils(main);
        main.ajouterUnFils(bloc);

        // Si (a > b)
        Si noeudSi = new Si();
        
        Superieur condition = new Superieur(); // Supérieur (>)
        condition.setFilsGauche(new Idf("a"));
        condition.setFilsDroit(new Idf("b"));
        noeudSi.setCondition(condition);
        
        // Alors { x = 1000 }
        Bloc blocAlors = new Bloc();
        Affectation affAlors = new Affectation();
        affAlors.setFilsGauche(new Idf("x"));
        affAlors.setFilsDroit(new Const(1000));
        blocAlors.ajouterUnFils(affAlors);
        noeudSi.setBlocAlors(blocAlors);
        
        // Sinon { x = 2000 }
        Bloc blocSinon = new Bloc();
        Affectation affSinon = new Affectation();
        affSinon.setFilsGauche(new Idf("x"));
        affSinon.setFilsDroit(new Const(2000));
        blocSinon.ajouterUnFils(affSinon);
        noeudSi.setBlocSinon(blocSinon);
        
        bloc.ajouterUnFils(noeudSi);

        // 3. GÉNÉRATION
        Generateur gen = new Generateur();
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
        TxtAfficheur.afficher(prog);
    }
}