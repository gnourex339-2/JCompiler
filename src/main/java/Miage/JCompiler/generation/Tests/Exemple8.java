package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple8 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #8 : Boucle TantQue ===");

        // 1. TABLE DES SYMBOLES
        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0);
        tds.ajouterGlobale("i", 0);

        // 2. ARBRE SYNTAXIQUE
        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc();
        
        // i = 0
        Affectation affI = new Affectation();
        affI.setFilsGauche(new Idf("i"));
        affI.setFilsDroit(new Const(0));
        bloc.ajouterUnFils(affI);

        // tantque (i < 6)
        TantQue tq = new TantQue();
        
        Inferieur inf = new Inferieur(); // Nœud Inférieur (<)
        inf.setFilsGauche(new Idf("i"));
        inf.setFilsDroit(new Const(6));
        tq.setCondition(inf);
        
        Bloc blocTq = new Bloc(); // Le contenu de la boucle
        
        // ecrire(i)
        Ecrire ecr = new Ecrire();
        ecr.ajouterUnFils(new Idf("i"));
        blocTq.ajouterUnFils(ecr);
        
        // i = i + 1
        Affectation affIPlus = new Affectation();
        affIPlus.setFilsGauche(new Idf("i"));
        Plus plus = new Plus();
        plus.setFilsGauche(new Idf("i"));
        plus.setFilsDroit(new Const(1));
        affIPlus.setFilsDroit(plus);
        blocTq.ajouterUnFils(affIPlus);
        
        tq.setBloc(blocTq);
        
        bloc.ajouterUnFils(tq);
        main.ajouterUnFils(bloc);
        prog.ajouterUnFils(main);

        // 3. GÉNÉRATION
        Generateur gen = new Generateur();
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
        TxtAfficheur.afficher(prog);
    }
}
