package Miage.JCompiler; 

import java.io.FileNotFoundException;
import java.io.FileReader;

// Imports des classes générées par CUP et JFlex
import generated.fr.ul.miashs.compil.Scanner;
import generated.fr.ul.miashs.compil.ParserCup;

// Imports des classes de la librairie d'Arbre et du Générateur
import fr.ul.miashs.compil.arbre.TxtAfficheur;
import Miage.JCompiler.generation.Generateur;

public class Main {

    public static void main(String[] args) {
        
        // 1. Vérification des arguments (il faut un fichier en entrée)
        if (args.length != 1) {
            System.err.println("Usage : java Main <nom_du_fichier.exp>");
            System.exit(1);
        }

        String filename = args[0];
        
        System.out.println("=== COMPILATION DU FICHIER : " + filename + " ===\n");

        try {
            // 2. Initialisation du SCANNER (Analyse Lexicale)
            // Il va lire le fichier texte caractère par caractère
            Scanner scanner = new Scanner(new FileReader(filename));
            
            // 3. Initialisation du PARSER (Analyse Syntaxique)
            // Il prend le scanner en paramètre pour récupérer les Tokens (mots)
            ParserCup parser = new ParserCup(scanner);
            
            // 4. LANCEMENT DE L'ANALYSE !
            // C'est cette méthode qui déclenche la lecture, vérifie la grammaire
            // et exécute notre code Java intégré pour remplir "parser.res" (l'Arbre) 
            // et "parser.tds" (la Table des Symboles).
            parser.parse();
            
            System.out.println("[OK] Analyse lexicale et syntaxique réussie !");
            System.out.println("[OK] Arbre abstrait et TDS construits.\n");
            
            // (Optionnel) Afficher l'arbre pour vérifier visuellement
            System.out.println("--- ARBRE ABSTRAIT ---");
            TxtAfficheur.afficher(parser.res);
            System.out.println("----------------------\n");

            // 5. GÉNÉRATION DE CODE ASSEMBLEUR
            // On appelle le générateur de l'Étape 1 !
            Generateur gen = new Generateur();
            
            // On lui passe l'Arbre et la TDS fraîchement construits par CUP
            String codeBeta = gen.generer(parser.res, parser.tds);
            
            // 6. AFFICHAGE DU RÉSULTAT FINAL
            System.out.println("=== CODE ASSEMBLEUR BETA ===");
            System.out.println(codeBeta);
            
        } catch (FileNotFoundException e) {
            System.err.println("Erreur : Le fichier '" + filename + "' est introuvable.");
        } catch (Exception e) {
            // S'il y a une erreur de syntaxe ou un caractère inconnu, on atterrit ici
            System.err.println("Échec de la compilation !");
            System.err.println(e.getMessage());
        }
    }
}