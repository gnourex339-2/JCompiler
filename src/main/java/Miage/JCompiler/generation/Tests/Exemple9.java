package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple9 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #9 : Récursivité ===");

        // 1. TABLE DES SYMBOLES
        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0);
        tds.ajouter("f", Cat.FONCTION, 0);

        // 2. ARBRE SYNTAXIQUE
        Prog prog = new Prog();

        // --- FONCTION f(a) ---
        Fonction f = new Fonction("f");
        f.ajouterUnFils(new Idf("a")); // Paramètre 'a'
        
        Bloc blocF = new Bloc();
        
        // si (a <= 0)
        Si si = new Si();
        InferieurEgal infe = new InferieurEgal(); // Nœud Inférieur ou Egal (<=)
        infe.setFilsGauche(new Idf("a"));
        infe.setFilsDroit(new Const(0));
        si.setCondition(infe);
        
        // alors { retour 0 }
        Bloc blocAlors = new Bloc();
        Retour ret0 = new Retour("0");
        ret0.getFils().set(0, new Const(0)); // Fix du null
        blocAlors.ajouterUnFils(ret0);
        si.setBlocAlors(blocAlors);
        
        blocF.ajouterUnFils(si);
        
        // retour a + f(a - 1)   (En dehors du 'si')
        Retour retF = new Retour("res");
        
        Plus plusF = new Plus();
        plusF.setFilsGauche(new Idf("a"));
        
        // Appel de f(a - 1)
        Appel appelF = new Appel("f");
        appelF.ajouterUnFils(new Idf("f")); // Fils 0 = nom de fonction
        
        Moins moinsF = new Moins(); // Calcul du paramètre : a - 1
        moinsF.setFilsGauche(new Idf("a"));
        moinsF.setFilsDroit(new Const(1));
        appelF.ajouterUnFils(moinsF); // Fils 1 = paramètre
        
        plusF.setFilsDroit(appelF);
        
        retF.getFils().set(0, plusF); // Fix du null
        blocF.ajouterUnFils(retF);
        
        f.ajouterUnFils(blocF);
        prog.ajouterUnFils(f);

        // --- FONCTION main() ---
        Fonction main = new Fonction("main");
        Bloc blocMain = new Bloc();
        
        // ecrire( f(6) )
        Ecrire ecr = new Ecrire();
        Appel appelMain = new Appel("f");
        appelMain.ajouterUnFils(new Idf("f")); // Nom
        appelMain.ajouterUnFils(new Const(6)); // Argument '6'
        
        ecr.ajouterUnFils(appelMain);
        blocMain.ajouterUnFils(ecr);
        
        main.ajouterUnFils(blocMain);
        prog.ajouterUnFils(main);

        // 3. GÉNÉRATION
        Generateur gen = new Generateur();
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
    }
}
