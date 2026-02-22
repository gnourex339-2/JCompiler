package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;

public class Exemple7 {

    public static void main(String[] args) {
        testExemple7();
        System.out.println("\n--------------------------------------------------\n");
    }

    /**
     * Exemple #7 : Conditionnelle (Si)
     * Basé sur la page 13 du document de projet.
     * Logique : 
     * if (a > b) {
     * x = 1000;
     * } else {
     * x = 2000;
     * }
     */
    private static void testExemple7() {
        System.out.println("=== TEST EXEMPLE #7 : Conditionnelle (Si) ===");

        // 1. Racine du programme [cite: 172]
        Prog prog = new Prog();

        // 2. Fonction main [cite: 176]
        Fonction main = new Fonction("main");
        Bloc blocMain = new Bloc();
        main.ajouterUnFils(blocMain);

        // 3. Nœud Si (Conditionnelle) [cite: 177, 180]
        Si si = new Si();
        
        // --- Branche 1 : La Condition (a > b) ---
        // On utilise Superieur comme illustré dans l'arbre [cite: 173]
        Superieur condition = new Superieur();
        condition.ajouterUnFils(new Idf("a")); // [cite: 171]
        condition.ajouterUnFils(new Idf("b")); //
        
        // --- Branche 2 : Le Bloc "Alors" (Si vrai) ---
        Bloc blocAlors = new Bloc(); // [cite: 178]
        Affectation affAlors = new Affectation(); // [cite: 179]
        affAlors.ajouterUnFils(new Idf("x")); // [cite: 184]
        affAlors.ajouterUnFils(new Const(1000)); // [cite: 185]
        blocAlors.ajouterUnFils(affAlors);

        // --- Branche 3 : Le Bloc "Sinon" (Si faux) ---
        Bloc blocSinon = new Bloc(); // [cite: 181]
        Affectation affSinon = new Affectation(); // [cite: 182]
        affSinon.ajouterUnFils(new Idf("x"));
        affSinon.ajouterUnFils(new Const(2000)); // [cite: 186]
        blocSinon.ajouterUnFils(affSinon);

        // Assemblage du Si : Condition, Bloc Alors, Bloc Sinon [cite: 170]
        si.ajouterUnFils(condition); 
        si.ajouterUnFils(blocAlors);  
        si.ajouterUnFils(blocSinon);  

        blocMain.ajouterUnFils(si);
        prog.ajouterUnFils(main);

        // 4. Génération du code assembleur 
        Generateur gen = new Generateur();
        String asm = gen.generer(prog);

        System.out.println(asm);
    }
}