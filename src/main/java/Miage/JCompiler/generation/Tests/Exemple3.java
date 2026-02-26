package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple3 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #3 : Expressions ===");

        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0);
        tds.ajouterGlobale("x", 0);
        tds.ajouterGlobale("a", 100);
        tds.ajouterGlobale("b", 170);

        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc();
        
        prog.ajouterUnFils(main);
        main.ajouterUnFils(bloc);

        // Construction de l'expression : x = (a * 2) + ((3 - b) / 5)
        Affectation aff = new Affectation();
        aff.setFilsGauche(new Idf("x")); 
        
        Plus plus = new Plus();
        
        // Sous-arbre gauche du PLUS : a * 2
        Multiplication mul = new Multiplication();
        mul.setFilsGauche(new Idf("a"));  
        mul.setFilsDroit(new Const(2));   
        plus.setFilsGauche(mul);            
        
        // Sous-arbre droit du PLUS : (3 - b) / 5
        Division div = new Division();
        
        Moins moins = new Moins();
        moins.setFilsGauche(new Const(3));  
        moins.setFilsDroit(new Idf("b"));    
        
        div.setFilsGauche(moins);            
        div.setFilsDroit(new Const(5));      
        
        plus.setFilsDroit(div);              
        
        aff.setFilsDroit(plus); 
        bloc.ajouterUnFils(aff);

        // 3. GÉNÉRATION
        Generateur gen = new Generateur();
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
        
        // L'affichage de l'arbre
        TxtAfficheur.afficher(prog);
    }
}