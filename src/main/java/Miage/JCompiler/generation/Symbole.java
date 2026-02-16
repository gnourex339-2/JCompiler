package Miage.JCompiler.generation;

public class Symbole {
    public Cat cat;
    public String nom;
    public int rang;     // Le rang (0, 1, 2...) pour calculer l'offset
    
    public Symbole(Cat cat, String nom, int rang) {
        this.cat = cat;
        this.nom = nom;
        this.rang = rang;
    }
}