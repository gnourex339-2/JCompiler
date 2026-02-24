package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.TDS.Tds;
import Miage.JCompiler.generation.Generateur;

public class Exemple2 {

    public static void main(String[] args) {
        testExemple2();
    }

    
    private static void testExemple2() {
        System.out.println("=== TEST EXEMPLE #2 : Variables Globales ===");

        // 1. Création de l'Arbre (
    Prog prog = new Prog();
    Fonction main = new Fonction("main");
    Bloc bloc = new Bloc(); // Pas de fils d'affectation ici !
    
    prog.ajouterUnFils(main);
    main.ajouterUnFils(bloc);

    // 2. Création de la TDS
    Tds tds = new Tds();
    // tds.ajouter("main", Cat.FONCTION, 0); 
    tds.ajouterGlobale("i", 10);
    tds.ajouterGlobale("j", 20);
    tds.ajouterGlobale("k", 0);
    tds.ajouterGlobale("l", 0);

    // 3. Appel du générateur en passant l'arbre ET la TDS
    Generateur gen = new Generateur();
    String codeBeta = gen.generer(prog, tds);
    
    System.out.println(codeBeta);
        
    //Affichage de l'arbre
    TxtAfficheur.afficher(prog); 
    }
}