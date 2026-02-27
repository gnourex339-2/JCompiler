package Miage.JCompiler.TDS;

import Miage.JCompiler.generation.Cat;

public class Symbole {
    public Cat cat;
    public String nom;
    public int rang;
    public int valeur; // <-- AJOUT POUR LES GLOBALES INITIALISÉES

    // Constructeur pour les locales/paramètres
    public Symbole(Cat cat, String nom, int rang) {
        this(cat, nom, rang, 0);
    }

    // Constructeur pour les globales avec valeur
    public Symbole(Cat cat, String nom, int rang, int valeur) {
        this.cat = cat;
        this.nom = nom;
        this.rang = rang;
        this.valeur = valeur;
    }
}