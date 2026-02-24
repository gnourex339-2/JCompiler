package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple6 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #6 : Locales et Parametres ===");

        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0);
        tds.ajouter("f", Cat.FONCTION, 0);
        tds.ajouterGlobale("a", 100);
        tds.ajouterGlobale("c", 170);

        Prog prog = new Prog();

        // --- FONCTION f(a, b) ---
        Fonction f = new Fonction("f");
        f.ajouterUnFils(new Idf("a")); 
        f.ajouterUnFils(new Idf("b")); 
        
        Bloc blocF = new Bloc();
        
        Affectation aff = new Affectation();
        aff.setFilsGauche(new Idf("res")); 
        
        Plus plus = new Plus();
        Multiplication mul = new Multiplication();
        mul.setFilsGauche(new Idf("a"));
        mul.setFilsDroit(new Const(2));
        plus.setFilsGauche(mul);
        
        Division div = new Division();
        Moins moins = new Moins();
        moins.setFilsGauche(new Idf("b"));
        moins.setFilsDroit(new Const(5));
        div.setFilsGauche(moins);
        div.setFilsDroit(new Const(3));
        plus.setFilsDroit(div);
        
        aff.setFilsDroit(plus);
        blocF.ajouterUnFils(aff);
        
        // ---  RETOUR ---
        Retour ret = new Retour("res");
        ret.getFils().set(0, new Idf("res")); // On remplace le 'null' à l'index 0
        blocF.ajouterUnFils(ret);
        
        f.ajouterUnFils(blocF); 
        prog.ajouterUnFils(f);  

        // --- FONCTION main() ---
        Fonction main = new Fonction("main");
        Bloc blocMain = new Bloc();
        
        Ecrire ecr = new Ecrire();
        
        // --- L'APPEL ---
        Appel appel = new Appel("f"); 
        appel.ajouterUnFils(new Idf("f")); // Fils 0 = nom de la fonction
        appel.ajouterUnFils(new Idf("a")); // Fils 1 = param 1
        appel.ajouterUnFils(new Idf("c")); // Fils 2 = param 2
        
        ecr.ajouterUnFils(appel);
        blocMain.ajouterUnFils(ecr);
        
        main.ajouterUnFils(blocMain);
        prog.ajouterUnFils(main);

        Generateur gen = new Generateur();
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
        TxtAfficheur.afficher(prog);
    }
}