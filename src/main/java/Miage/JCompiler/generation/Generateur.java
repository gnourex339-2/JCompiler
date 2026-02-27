package Miage.JCompiler.generation;

import Miage.JCompiler.TDS.Symbole;
import Miage.JCompiler.TDS.Tds;
import fr.ul.miashs.compil.arbre.*;
import java.util.List;
//import java.util.HashSet;

public class Generateur {

    private StringBuilder asm;
    private Tds tds;
    private int labelCounter = 0;
    private boolean besoinIntIO = false;


    public Generateur() {
        this.asm = new StringBuilder();
        this.tds = new Tds();
    }
    private boolean arbreContient(Noeud n, Noeud.Categories cat) {
        if (n == null) return false;
        if (n.getCat() == cat) return true;
        if (n.getFils() != null) {
            for (Noeud f : n.getFils()) {
                if (f != null && arbreContient(f, cat)) return true;
            }
        }
        return false;
    }

    public String generer(Noeud arbre, Tds tdsEntree) {
        this.asm = new StringBuilder();
        this.tds = tdsEntree; // <-- On utilise la TDS fournie !
        this.labelCounter = 0;


        besoinIntIO = arbreContient(arbre, Noeud.Categories.ECR)
                || arbreContient(arbre, Noeud.Categories.LIRE);


        asm.append(".include beta.uasm\n");
        if (besoinIntIO) {
            asm.append(".include intio.uasm\n");
        }
        asm.append(".options tty\n");
        asm.append("\tCMOVE(pile, SP)\n");
        asm.append("\tBR(debut)\n\n");

        // --- VARIABLES GLOBALES (D'après la TDS) ---
        asm.append("| --- VARIABLES GLOBALES ---\n");
        
        // On parcourt la TDS pour déclarer les globales
        for (Symbole sym : tds.getTable().values()) {
            if (sym.cat == Cat.GLOBAL && !sym.nom.equals("main")) {
                asm.append(sym.nom).append(": LONG(").append(sym.valeur).append(")\n");
            }
        }
        
        asm.append("\n");

        // 2. Point d'entrée
        asm.append("debut:\n");
        asm.append("\tCALL(main)\n");
        asm.append("\tHALT()\n\n");

        // 3. Génération récursive
        genererNoeud(arbre);

        // Zone de pile
        asm.append("\n| --- PILE ---\n");
        asm.append("pile: STORAGE(256)\n");
        
        return asm.toString();
    }
    
    private void genererNoeud(Noeud n) {
        if (n == null) return;

        switch (n.getCat()) {
            case PROG:
                if (n.getFils() != null) {
                    for (Noeud fils : n.getFils()) genererNoeud(fils);
                }
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
            case SI:
                genererSi((Si) n);
                break;
            case TQ: 
                genererTantQue((TantQue) n);
                break;
            case RET: 
                genererRetour((Retour) n);
                break;
            case ECR: 
                genererEcrire(n);
                break;
            // Cas des expressions (si elles sont utilisées comme instruction seule, ex: appel void)
            case APPEL:
                 genererExpression(n);
                 asm.append("\tPOP(R0)\n"); // On vide la pile si retour ignoré
                 break;
            default:
                break; 
        }
    }

    // --- GESTION DES FONCTIONS ---

    private void genererFonction(Fonction f) {
        String nomFonction = f.getValeur().toString();
        asm.append("\n| --- Fonction ").append(nomFonction).append(" ---\n");
        asm.append(nomFonction).append(":\n");

        // Prologue
        asm.append("\tPUSH(LP)\n");
        asm.append("\tPUSH(BP)\n");
        asm.append("\tMOVE(SP, BP)\n");
        
        // Mise à jour TDS (nouveau scope)
        tds.nettoyerLocales(); 
        
        // 1. Identifier les paramètres (Fils avant le BLOC)
        int nbParams = 0;
        List<Noeud> fils = f.getFils();
        Bloc corps = null;
        
        if (fils != null) {
            for(Noeud enfant : fils) {
                if (enfant instanceof Bloc) {
                    corps = (Bloc) enfant;
                    break; 
                }
                if (enfant.getCat() == Noeud.Categories.IDF) {
                    String nomParam = ((Idf)enfant).getValeur().toString();
                    tds.ajouter(nomParam, Cat.PARAM, nbParams);
                    nbParams++;
                }
            }
        }
        tds.setContexteFonction(nbParams);

        // 2. Identifier les locales (Exploration sommaire du corps)
        int nbLocales = detecterLocales(corps, 0);
        
        asm.append("\tALLOCATE(").append(nbLocales).append(")\n");

        // Génération du corps
        if (corps != null) {
            genererBloc(corps);
        }

        // Epilogue par défaut
        asm.append("\tDEALLOCATE(").append(nbLocales).append(")\n");
        asm.append("\tPOP(BP)\n");
        asm.append("\tPOP(LP)\n");
        asm.append("\tRTN()\n");
    }
    
    // Parcours récursif pour trouver les variables locales (AFF -> IDF)
    private int detecterLocales(Noeud n, int rangActuel) {
    if (n == null) return rangActuel;
    
    if (n.getCat() == Noeud.Categories.AFF) {
        if (n.getFils() != null && !n.getFils().isEmpty() 
            && n.getFils().get(0) != null) {  // ← AJOUT
            
            Noeud gauche = n.getFils().get(0);
            if (gauche.getCat() == Noeud.Categories.IDF) {
                String nom = ((Idf)gauche).getValeur().toString();
                if (tds.chercher(nom) == null) {
                    tds.ajouter(nom, Cat.LOCAL, rangActuel);
                    rangActuel++;
                }
            }
        }
    }
    
    if (n.getFils() != null) {
        for (Noeud f : n.getFils()) {
            if (f != null) {  // ← AJOUT
                rangActuel = detecterLocales(f, rangActuel);
            }
        }
    }
    
    return rangActuel;
}

    // --- INSTRUCTIONS ---

    private void genererBloc(Bloc b) {
    if (b.getFils() != null) {
        for (Noeud fils : b.getFils()) {
            if (fils != null) {  // ← AJOUT
                genererNoeud(fils);
            }
        }
    }
}

    private void genererAffectation(Affectation a) {
    asm.append("\t| Affectation\n");
    
    // Vérifier que les fils existent et ne sont pas null
    if (a.getFils() == null || a.getFils().size() < 2 
        || a.getFils().get(0) == null || a.getFils().get(1) == null) {
        return;
    }
    
    Idf idf = (Idf) a.getFils().get(0);
    Noeud expression = a.getFils().get(1);

    genererExpression(expression); // Résultat empilé

    Symbole sym = tds.chercher(idf.getValeur().toString());
    if (sym == null) {
         // Cas global par défaut
         sym = new Symbole(Cat.GLOBAL, idf.getValeur().toString(), 0);
         // On devrait l'ajouter à la section DATA normalement
    }
    
    asm.append("\tPOP(R0)\n");

    if (sym.cat == Cat.GLOBAL) {
        asm.append("\tST(R0, ").append(sym.nom).append(")\n");
    } 
    else if (sym.cat == Cat.LOCAL) {
        int offset = sym.rang * 4;
        asm.append("\tPUTFRAME(R0, ").append(offset).append(")\n");
    } 
    else if (sym.cat == Cat.PARAM) {
        int nbParam = tds.getNbParamFonctionCourante();
        int offset = -(2 + nbParam - sym.rang) * 4;
        asm.append("\tPUTFRAME(R0, ").append(offset).append(")\n");
    }
}

    private void genererSi(Si n) {
        int num = ++labelCounter;
        String labelSinon = "sinon_" + num;
        String labelFin = "fsi_" + num;

        asm.append("\t| Si ").append(num).append("\n");
        genererExpression(n.getCondition()); 
        asm.append("\tPOP(R0)\n");
        asm.append("\tBF(R0, ").append(labelSinon).append(")\n");

        genererBloc(n.getBlocAlors());
        asm.append("\tBR(").append(labelFin).append(")\n");

        asm.append(labelSinon).append(":\n");
        if (n.getBlocSinon() != null) { 
             genererBloc(n.getBlocSinon());
        }
        asm.append(labelFin).append(":\n");
    }

    private void genererTantQue(TantQue n) {
        int num = ++labelCounter;
        String labelDebut = "tq_" + num;
        String labelFin = "ftq_" + num;

        asm.append("\t| TantQue ").append(num).append("\n");
        asm.append(labelDebut).append(":\n");
        genererExpression(n.getCondition());
        asm.append("\tPOP(R0)\n");
        asm.append("\tBF(R0, ").append(labelFin).append(")\n");

        genererBloc(n.getBloc());
        asm.append("\tBR(").append(labelDebut).append(")\n");
        asm.append(labelFin).append(":\n");
    }

    private void genererRetour(Retour r) {
        if (r.getFils() != null && !r.getFils().isEmpty() && r.getFils().get(0) != null) {
            genererExpression(r.getFils().get(0));
            asm.append("\tPOP(R0)\n");
        }
        // Retour simplifié
        asm.append("\tMOVE(BP, SP)\n");
        asm.append("\tPOP(BP)\n");
        asm.append("\tPOP(LP)\n");
        asm.append("\tRTN()\n");
    }
    
    private void genererEcrire(Noeud n) {
    if (n.getFils() != null && !n.getFils().isEmpty() 
        && n.getFils().get(0) != null) {  
        genererExpression(n.getFils().get(0));
        asm.append("\tPOP(R0)\n");
        asm.append("\tWRINT()\n"); 
    }
}


    // --- EXPRESSIONS ---

private void genererExpression(Noeud n) {
        if (n == null) return;

        switch (n.getCat()) {
            // CAS CONSTANTE
            case CONST:
                // Attention : Il faut caster vers (Const), pas (NoeudInt)
                int val = ((Const) n).getValeur(); 
                asm.append("\tCMOVE(").append(val).append(", R0)\n");
                asm.append("\tPUSH(R0)\n");
                break;

            // CAS VARIABLE (LECTURE)
            case IDF:
                String nom = ((Idf) n).getValeur().toString();
                Symbole sym = tds.chercher(nom);
                
                // Si la variable n'est pas dans la TDS (cas global par défaut)
                if (sym == null) {
                    asm.append("\tLD(").append(nom).append(", R0)\n");
                } else {
                    // Logique selon le type
                    if (sym.cat == Cat.GLOBAL) {
                        asm.append("\tLD(").append(sym.nom).append(", R0)\n");
                    } else if (sym.cat == Cat.LOCAL) {
                        int offset = sym.rang * 4;
                        asm.append("\tGETFRAME(").append(offset).append(", R0)\n");
                    } else if (sym.cat == Cat.PARAM) {
                        int nbParam = tds.getNbParamFonctionCourante();
                        int offset = -(2 + nbParam - sym.rang) * 4;
                        asm.append("\tGETFRAME(").append(offset).append(", R0)\n");
                    }
                }
                asm.append("\tPUSH(R0)\n");
                break;

            case APPEL:
                List<Noeud> args = n.getFils();
                // On suppose que le fils 0 est l'IDF (nom fonction)
                if (args != null && !args.isEmpty() && args.get(0).getCat() == Noeud.Categories.IDF) {
                    String funcName = ((Idf)args.get(0)).getValeur().toString();
                    // Empiler arguments
                    for (int i = 1; i < args.size(); i++) {
                        genererExpression(args.get(i));
                    }
                    asm.append("\tCALL(").append(funcName).append(")\n");
                    // Nettoyer arguments
                    if (args.size() > 1) {
                        asm.append("\tDEALLOCATE(").append(args.size() - 1).append(")\n");
                    }
                    asm.append("\tPUSH(R0)\n");
                }
                break;

            case PLUS: générerOp(n, "ADD"); break;
            case MOINS: générerOp(n, "SUB"); break;
            case MUL: générerOp(n, "MUL"); break;
            case DIV: générerOp(n, "DIV"); break;
            
            case INF: générerComp(n, "CMPLT"); break;
            case EG: générerComp(n, "CMPEQ"); break;

            // 2. Inférieur ou Egal (<=) : A <= B
            case INFE: 
                générerComp(n, "CMPLE"); 
                break;

            // 4. Supérieur (>) : A > B <=> B < A
            case SUP: 
                // On génère les fils normalement
                genererExpression(n.getFils().get(0)); // Gauche (A)
                genererExpression(n.getFils().get(1)); // Droite (B)
                asm.append("\tPOP(R2)\n"); // R2 = B
                asm.append("\tPOP(R1)\n"); // R1 = A
                // On inverse les registres dans la comparaison : CMPLT(R2, R1) => B < A
                asm.append("\tCMPLT(R2, R1, R0)\n"); 
                asm.append("\tPUSH(R0)\n");
                break;

            // 5. Supérieur ou Egal (>=) : A >= B <=> B <= A
            case SUPE: 
                genererExpression(n.getFils().get(0)); // Gauche (A)
                genererExpression(n.getFils().get(1)); // Droite (B)
                asm.append("\tPOP(R2)\n"); // R2 = B
                asm.append("\tPOP(R1)\n"); // R1 = A
                // On inverse les registres : CMPLE(R2, R1) => B <= A
                asm.append("\tCMPLE(R2, R1, R0)\n"); 
                asm.append("\tPUSH(R0)\n");
                break;

            // 6. Différent (!=) : !(A == B)
            case DIF:
                genererExpression(n.getFils().get(0));
                genererExpression(n.getFils().get(1));
                asm.append("\tPOP(R2)\n");
                asm.append("\tPOP(R1)\n");
                // On teste l'égalité
                asm.append("\tCMPEQ(R1, R2, R0)\n"); 
                // On inverse le résultat (Si R0==0 alors R0=1, sinon R0=0)
                // R31 vaut toujours 0 en Beta
                asm.append("\tCMPEQ(R0, R31, R0)\n"); 
                asm.append("\tPUSH(R0)\n");
                break;
                
            case LIRE:
            asm.append("\tRDINT()\n"); // Lit un entier tapé au clavier et le met dans R0
            asm.append("\tPUSH(R0)\n"); // Empile le résultat comme n'importe quelle expression
            break;
            
            default: break;
        }
    }
    
    private void générerOp(Noeud n, String op) {
    if (n.getFils() != null && n.getFils().size() >= 2
        && n.getFils().get(0) != null && n.getFils().get(1) != null) {  // ← AJOUT
        genererExpression(n.getFils().get(0));
        genererExpression(n.getFils().get(1));
        asm.append("\tPOP(R2)\n");
        asm.append("\tPOP(R1)\n");
        asm.append("\t").append(op).append("(R1, R2, R0)\n");
        asm.append("\tPUSH(R0)\n");
    }
}
    
    private void générerComp(Noeud n, String op) {
        générerOp(n, op); 
    }
}