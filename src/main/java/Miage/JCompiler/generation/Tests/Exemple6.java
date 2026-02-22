package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;

public class Exemple6 {

    public static void main(String[] args) {
        testExemple6();
        System.out.println("\n--------------------------------------------------\n");
    }

    private static void testExemple6() {
        System.out.println("=== TEST EXEMPLE #6 : Variables locales et parametres ===");

        Prog prog = new Prog();

        // --- FONCTION f(a, b) ---
        // Selon le schéma p.11, f a deux paramètres (a, b) et une variable locale (res) [cite: 163, 167]
        Fonction f = new Fonction("f");
        f.ajouterUnFils(new Idf("a")); // Paramètre 0 
        f.ajouterUnFils(new Idf("b")); // Paramètre 1 [cite: 165]
        
        Bloc blocF = new Bloc();
        f.ajouterUnFils(blocF);

        // res = (a * 2) + ((b - 5) / 3) [cite: 146, 151, 157, 161]
        Affectation affRes = new Affectation();
        affRes.setFilsGauche(new Idf("res"));

        Plus plus = new Plus();
        
        // (a * 2)
        Multiplication mul = new Multiplication();
        mul.setFilsGauche(new Idf("a"));
        mul.setFilsDroit(new Const(2));

        // ((b - 5) / 3)
        Division div = new Division();
        Moins moins = new Moins();
        moins.setFilsGauche(new Idf("b"));
        moins.setFilsDroit(new Const(5));
        div.setFilsGauche(moins);
        div.setFilsDroit(new Const(3));

        plus.setFilsGauche(mul);
        plus.setFilsDroit(div);
        affRes.setFilsDroit(plus);
        blocF.ajouterUnFils(affRes);

        // return res [cite: 145, 148]
        Retour retour = new Retour(new Idf("res")); // Utilise le constructeur avec l'expression à retourner
        blocF.ajouterUnFils(retour);

        // --- FONCTION main ---
        Fonction main = new Fonction("main");
        Bloc blocMain = new Bloc();
        main.ajouterUnFils(blocMain);

        // ecrire(f(a, b)) 
        // Note : p.11 montre l'appel avec les globales 'a' et 'b' (ou 'c' selon l'interprétation du schéma) [cite: 153, 154, 164]
        Ecrire ecrire = new Ecrire();
        Appel appelF = new Appel("f"); 
        
        // ARGUMENTS de l'appel : on ajoute directement les expressions des paramètres
        appelF.ajouterUnFils(new Idf("a")); // Argument 1 : globale a 
        appelF.ajouterUnFils(new Idf("b")); // Argument 2 : globale b 
        
        ecrire.setLeFils(appelF);
        blocMain.ajouterUnFils(ecrire);

        prog.ajouterUnFils(f);
        prog.ajouterUnFils(main);

        // --- GÉNÉRATION ---
        Generateur gen = new Generateur();
        String asm = gen.generer(prog);

        System.out.println(asm);
    }
}