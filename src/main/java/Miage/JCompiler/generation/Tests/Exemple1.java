
package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;

public class Exemple1 {

    public static void main(String[] args) {
        testExemple1();
        System.out.println("\n--------------------------------------------------\n");
       
    }

    /**
     * Exemple #1 : Programme minimal
     * Structure :
     * PROG
     *  └── FONCTION "main"
     *       └── BLOC (vide)
     */
    private static void testExemple1() {
        System.out.println("=== TEST EXEMPLE #1 : Programme Minimal ===");
        
        // Construction de l'arbre
        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc();
        
        prog.ajouterUnFils(main);
        main.ajouterUnFils(bloc); // Le corps de la fonction

        // Génération
        Generateur gen = new Generateur();
        String asm = gen.generer(prog);
        
        System.out.println(asm);
    }

    
}