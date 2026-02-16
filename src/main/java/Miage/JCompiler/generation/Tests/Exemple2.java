package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;

public class Exemple2 {

    public static void main(String[] args) {
        testExemple2();
        System.out.println("\n--------------------------------------------------\n");
       
    }

    /**
     * Exemple #2 : Variables Globales
     * Code :
     * main() {
     *    i = 10;
     *    j = 20;
     *    k = 0;
     *    l = 0;
     * }
     */
    private static void testExemple2() {
        System.out.println("=== TEST EXEMPLE #2 : Variables Globales ===");

        // Construction de l'arbre
        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc();
        
        prog.ajouterUnFils(main);
        main.ajouterUnFils(bloc);

        // Instruction 1: i = 10
        Affectation aff1 = new Affectation();
        aff1.setFilsGauche(new Idf("i"));
        aff1.setFilsDroit(new Const(10));
        bloc.ajouterUnFils(aff1);

        // Instruction 2: j = 20
        Affectation aff2 = new Affectation();
        aff2.setFilsGauche(new Idf("j"));
        aff2.setFilsDroit(new Const(20));
        bloc.ajouterUnFils(aff2);


        // Instruction 3: k = 0
        Affectation aff3 = new Affectation();
        aff3.setFilsGauche(new Idf("k"));
        aff3.setFilsDroit(new Const(0));
        bloc.ajouterUnFils(aff3);

        // Instruction 4: l = 0
        Affectation aff4 = new Affectation();
        aff4.setFilsGauche(new Idf("l"));
        aff4.setFilsDroit(new Const(0));
        bloc.ajouterUnFils(aff4);

        // Génération
        Generateur gen = new Generateur();
        String asm = gen.generer(prog);

        System.out.println(asm);
    }
    
}
