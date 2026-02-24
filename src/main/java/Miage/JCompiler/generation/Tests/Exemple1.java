package Miage.JCompiler.generation.Tests;

import fr.ul.miashs.compil.arbre.*;
import Miage.JCompiler.generation.Generateur;
import Miage.JCompiler.generation.Cat;
import Miage.JCompiler.TDS.Tds;

public class Exemple1 {

    public static void main(String[] args) {
        System.out.println("=== TEST EXEMPLE #1 : Programme minimal ===");

        
        Prog prog = new Prog();
        Fonction main = new Fonction("main");
        Bloc bloc = new Bloc(); // Le bloc est vide, aucune instruction !
        
        prog.ajouterUnFils(main);
        main.ajouterUnFils(bloc);

        
        // {nom = main ; type = void ; cat = fonction}
        Tds tds = new Tds();
        tds.ajouter("main", Cat.FONCTION, 0); 
        
        Generateur gen = new Generateur();
        
        // On passe l'arbre ET la TDS au générateur
        String codeBeta = gen.generer(prog, tds); 
        
        System.out.println(codeBeta);
        TxtAfficheur.afficher(prog); 
    }
}