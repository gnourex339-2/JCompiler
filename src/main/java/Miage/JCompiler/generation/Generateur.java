package Miage.JCompiler.generation;

import Miage.JCompiler.TDS.Tds;
import fr.ul.miashs.compil.arbre.*;
import java.util.List;
import java.util.HashSet;

public class Generateur {

    private StringBuilder asm;
    private Tds tds;
    private int labelCounter = 0;
    // Pour éviter de déclarer deux fois la même variable (ex: i = 10; i = 20;)
    private HashSet<String> globalesDeclarees = new HashSet<>();

    public Generateur() {
        this.asm = new StringBuilder();
        this.tds = new Tds();
    }

    public String generer(Noeud arbre) {
        asm = new StringBuilder();
        tds = new Tds(); // Reset TDS
        globalesDeclarees.clear(); //On vide la liste pour repartir à zéro
        
        // Init standard Beta
        asm.append(".include beta.uasm\n");
        asm.append(".options tty\n"); 
        asm.append("\tCMOVE(pile, SP)\n");
        asm.append("\tBR(debut)\n\n");

        // 1. Collecte des variables globales
        asm.append("| --- VARIABLES GLOBALES ---\n");
        
        // C'est ici qu'on lance la recherche dans l'arbre
        collecterGlobales(arbre); // <--- AJOUT : Appel de la méthode
        
        asm.append("\n");

        // 2. Point d'entrée
        asm.append("debut:\n");
        asm.append("\tCALL(main)\n");
        asm.append("\tHALT()\n\n");

        // 3. Génération récursive
        genererNoeud(arbre);

        // Zone de pile
        asm.append("\n| --- PILE ---\n");
        asm.append("pile:\n");
        
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
            if (n.getFils() != null && !n.getFils().isEmpty()) {
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
                rangActuel = detecterLocales(f, rangActuel);
            }
        }
        
        return rangActuel;
    }

    // --- INSTRUCTIONS ---

    private void genererBloc(Bloc b) {
        if (b.getFils() != null) {
            for (Noeud fils : b.getFils()) {
                genererNoeud(fils);
            }
        }
    }

    private void genererAffectation(Affectation a) {
        asm.append("\t| Affectation\n");
        
        if (a.getFils() == null || a.getFils().size() < 2) return;
        
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
            int offset = (1 + sym.rang) * 4;
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
        if (n.getFils() != null && !n.getFils().isEmpty()) {
            genererExpression(n.getFils().get(0));
            asm.append("\tPOP(R0)\n");
            asm.append("\tWRINT()\n"); 
        }
    }

    private void collecterGlobales(Noeud n) {
        if (n == null) return;

        // Si on trouve une affectation (AFF)
        if (n.getCat() == Noeud.Categories.AFF) {
            // Vérifier que getFils() n'est pas null ET n'est pas vide
            if (n.getFils() != null && !n.getFils().isEmpty() 
                && n.getFils().get(0).getCat() == Noeud.Categories.IDF) {
                
                String nomVar = ((Idf)n.getFils().get(0)).getValeur().toString();
                
                // Si pas encore déclarée, on l'écrit dans le code assembleur
                if (!globalesDeclarees.contains(nomVar)) {
                    asm.append(nomVar).append(": LONG(0)\n");
                    globalesDeclarees.add(nomVar);
                    
                    // On l'ajoute aussi à la TDS comme GLOBAL
                    tds.ajouter(nomVar, Cat.GLOBAL, 0); 
                }
            }
        }
        
        // Parcourir récursivement les fils (avec vérification null)
        if (n.getFils() != null) {
            for (Noeud fils : n.getFils()) {
                collecterGlobales(fils);
            }
        }
    }

    // --- EXPRESSIONS ---

    private void genererExpression(Noeud n) {
        if (n == null) return;

        switch (n.getCat()) {
            case CONST:
                int val = ((Const) n).getValeur();
                asm.append("\tCMOVE(").append(val).append(", R0)\n");
                asm.append("\tPUSH(R0)\n");
                break;

            case IDF:
                String nom = ((Idf) n).getValeur().toString();
                Symbole sym = tds.chercher(nom);
                if (sym == null) {
                     // Défaut Global
                     asm.append("\tLD(").append(nom).append(", R0)\n");
                } else {
                    if (sym.cat == Cat.GLOBAL) {
                        asm.append("\tLD(").append(sym.nom).append(", R0)\n");
                    } else if (sym.cat == Cat.LOCAL) {
                        int offset = (1 + sym.rang) * 4;
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
            // Ajouter les autres comparaisons au besoin
            
            default: break;
        }
    }
    
    private void générerOp(Noeud n, String op) {
        if (n.getFils() != null && n.getFils().size() >= 2) {
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