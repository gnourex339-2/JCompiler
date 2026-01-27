
package Miage.JCompiler;
import fr.ul.miashs.compil.arbre.*;



import java.util.HashMap;
import java.util.Map;

/**
 * Générateur de code assembleur BETA à partir d'un arbre abstrait
 * @author Amine
 */

public class generateurAssembleur {

    // Buffer pour stocker le code généré
    private StringBuilder codeGlobales;
    private StringBuilder codeFonctions;
    private StringBuilder codeMain;

    // Table des variables globales (nom -> adresse)
    private Map<String, String> variablesGlobales;

    // Compteur pour les étiquettes uniques
    private int compteurEtiquettes;

    /**
     * Constructeur
     */
    public generateurAssembleur() {
        codeGlobales = new StringBuilder();
        codeFonctions = new StringBuilder();
        codeMain = new StringBuilder();
        variablesGlobales = new HashMap<>();
        compteurEtiquettes = 0;
    }

    /**
     * Génère le code assembleur BETA complet à partir de l'arbre
     *
     * @param arbre : racine de l'arbre (noeud PROG)
     * @return le code assembleur sous forme de String
     */
    public String generer(Noeud arbre) {
        StringBuilder code = new StringBuilder();

        // En-tête du programme
        code.append(".include beta.uasm\n");
        code.append("\n");
        code.append("| Initialisation de la pile\n");
        code.append("\tCMOVE(pile, SP)\n");
        code.append("\tBR(debut)\n");
        code.append("\n");

        // Parcourir l'arbre pour collecter les variables globales
        collecterVariables(arbre);

        // Générer la section des variables globales
        code.append("| Variables globales\n");
        for (String var : variablesGlobales.keySet()) {
            code.append(var).append(":\tLONG(0)\n");
        }
        code.append("\n");

        // Générer le code des fonctions
        genererNoeud(arbre);
        code.append(codeFonctions.toString());

        // Point d'entrée du programme
        code.append("| Point d'entrée\n");
        code.append("debut:\n");
        code.append("\tCALL(main)\n");
        code.append("\tHALT()\n");
        code.append("\n");

        // Pile
        code.append("| Pile\n");
        code.append("pile:\n");

        return code.toString();
    }

    /**
     * Collecte toutes les variables (IDF) utilisées dans l'arbre
     */
    private void collecterVariables(Noeud n) {
        if (n == null) return;

        if (n.getCat() == Noeud.Categories.IDF) {
            String nom = ((Idf) n).getValeur().toString();
            if (!variablesGlobales.containsKey(nom)) {
                variablesGlobales.put(nom, nom);
            }
        }

        if (n.getFils() != null) {
            for (Noeud fils : n.getFils()) {
                collecterVariables(fils);
            }
        }
    }

    /**
     * Génère le code pour un noeud (dispatch selon la catégorie)
     */
    private void genererNoeud(Noeud n) {
        if (n == null) return;

        switch (n.getCat()) {
            case PROG:
                genererProg(n);
                break;
            case FONCTION:
                genererFonction((Fonction) n);
                break;
            case BLOC:
                genererBloc((Bloc) n);
                break;
            case AFF:
                genererAffectation((Affectation) n);
                break;
            case PLUS:
                genererPlus((Plus) n);
                break;
            case MOINS:
                genererMoins((Moins) n);
                break;
            case MUL:
                genererMultiplication((Multiplication) n);
                break;
            case DIV:
                genererDivision((Division) n);
                break;
            case CONST:
                genererConst((Const) n);
                break;
            case IDF:
                genererIdf((Idf) n);
                break;
            default:
                codeFonctions.append("| Noeud non géré: ").append(n.getCat()).append("\n");
        }
    }

    /**
     * Génère le code pour le noeud PROG
     */
    private void genererProg(Noeud prog) {
        if (prog.getFils() != null) {
            for (Noeud fils : prog.getFils()) {
                genererNoeud(fils);
            }
        }
    }

    /**
     * Génère le code pour une fonction
     */
    private void genererFonction(Fonction f) {
        String nom = f.getValeur().toString();

        codeFonctions.append("| Fonction ").append(nom).append("\n");
        codeFonctions.append(nom).append(":\n");

        // Prologue de la fonction
        codeFonctions.append("\tPUSH(LP)\n");
        codeFonctions.append("\tPUSH(BP)\n");
        codeFonctions.append("\tMOVE(SP, BP)\n");

        // Générer le code du corps de la fonction
        if (f.getFils() != null) {
            for (Noeud fils : f.getFils()) {
                genererNoeud(fils);
            }
        }

        // Épilogue de la fonction
        codeFonctions.append("\tMOVE(BP, SP)\n");
        codeFonctions.append("\tPOP(BP)\n");
        codeFonctions.append("\tPOP(LP)\n");
        codeFonctions.append("\tRTN()\n");
        codeFonctions.append("\n");
    }

    /**
     * Génère le code pour un bloc
     */
    private void genererBloc(Bloc b) {
        if (b.getFils() != null) {
            for (Noeud fils : b.getFils()) {
                genererNoeud(fils);
            }
        }
    }

    /**
     * Génère le code pour une affectation
     * x = expr  =>  évalue expr (résultat sur pile), puis ST dans x
     */
    private void genererAffectation(Affectation a) {
        codeFonctions.append("\t| Affectation\n");

        // Générer le code de l'expression (fils droit)
        genererNoeud(a.getFilsDroit());

        // Récupérer le nom de la variable (fils gauche)
        Idf idf = (Idf) a.getFilsGauche();
        String nom = idf.getValeur().toString();

        // Dépiler le résultat dans R0 et stocker dans la variable
        codeFonctions.append("\tPOP(R0)\n");
        codeFonctions.append("\tST(R0, ").append(nom).append(")\n");
    }

    /**
     * Génère le code pour une constante
     * Empile la valeur sur la pile
     */
    private void genererConst(Const c) {
        int valeur = c.getValeur();
        codeFonctions.append("\tCMOVE(").append(valeur).append(", R0)\n");
        codeFonctions.append("\tPUSH(R0)\n");
    }

    /**
     * Génère le code pour un identificateur (variable)
     * Charge la valeur de la variable et l'empile
     */
    private void genererIdf(Idf idf) {
        String nom = idf.getValeur().toString();
        codeFonctions.append("\tLD(").append(nom).append(", R0)\n");
        codeFonctions.append("\tPUSH(R0)\n");
    }

    /**
     * Génère le code pour l'addition
     */
    private void genererPlus(Plus p) {
        // Générer le code des deux opérandes
        genererNoeud(p.getFilsGauche());
        genererNoeud(p.getFilsDroit());

        // Dépiler les deux opérandes et additionner
        codeFonctions.append("\tPOP(R1)\t\t| opérande droit\n");
        codeFonctions.append("\tPOP(R0)\t\t| opérande gauche\n");
        codeFonctions.append("\tADD(R0, R1, R2)\n");
        codeFonctions.append("\tPUSH(R2)\n");
    }

    /**
     * Génère le code pour la soustraction
     */
    private void genererMoins(Moins m) {
        // Générer le code des deux opérandes
        genererNoeud(m.getFilsGauche());
        genererNoeud(m.getFilsDroit());

        // Dépiler les deux opérandes et soustraire
        codeFonctions.append("\tPOP(R1)\t\t| opérande droit\n");
        codeFonctions.append("\tPOP(R0)\t\t| opérande gauche\n");
        codeFonctions.append("\tSUB(R0, R1, R2)\n");
        codeFonctions.append("\tPUSH(R2)\n");
    }

    /**
     * Génère le code pour la multiplication
     */
    private void genererMultiplication(Multiplication m) {
        // Générer le code des deux opérandes
        genererNoeud(m.getFilsGauche());
        genererNoeud(m.getFilsDroit());

        // Dépiler les deux opérandes et multiplier
        codeFonctions.append("\tPOP(R1)\t\t| opérande droit\n");
        codeFonctions.append("\tPOP(R0)\t\t| opérande gauche\n");
        codeFonctions.append("\tMUL(R0, R1, R2)\n");
        codeFonctions.append("\tPUSH(R2)\n");
    }

    /**
     * Génère le code pour la division
     */
    private void genererDivision(Division d) {
        // Générer le code des deux opérandes
        genererNoeud(d.getFilsGauche());
        genererNoeud(d.getFilsDroit());

        // Dépiler les deux opérandes et diviser
        codeFonctions.append("\tPOP(R1)\t\t| opérande droit (diviseur)\n");
        codeFonctions.append("\tPOP(R0)\t\t| opérande gauche (dividende)\n");
        codeFonctions.append("\tDIV(R0, R1, R2)\n");
        codeFonctions.append("\tPUSH(R2)\n");
    }
}