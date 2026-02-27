
package Miage.JCompiler.TDS;

import Miage.JCompiler.generation.Cat;

import java.util.HashMap;
import java.util.Map;

public class Tds {
    // Une table pour les variables (Nom -> Symbole)
    private Map<String, Symbole> table = new HashMap<>();

    //méthode pour que le générateur puisse parcourir les globales
    public Map<String, Symbole> getTable() {
        return table;
    }

    // méthode pratique pour ajouter une globale
    public void ajouterGlobale(String nom, int valeur) {
        table.put(nom, new Symbole(Cat.GLOBAL, nom, 0, valeur));
    }
    
    // Contexte actuel pour savoir combien de paramètres a la fonction qu'on traite
    private int nbParamFonctionCourante = 0; 

    public void ajouter(String nom, Cat cat, int rang) {
        table.put(nom, new Symbole(cat, nom, rang));
    }

    public Symbole chercher(String nom) {
        return table.get(nom);
    }
    
    public void setContexteFonction(int nbParam) {
        this.nbParamFonctionCourante = nbParam;
    }
    
    public int getNbParamFonctionCourante() {
        return nbParamFonctionCourante;
    }
    
    // Nettoie les variables locales/paramètres quand on sort d'une fonction
    // (On ne garde que les globales)
    public void nettoyerLocales() {
        table.entrySet().removeIf(entry -> entry.getValue().cat != Cat.GLOBAL);
    }
}