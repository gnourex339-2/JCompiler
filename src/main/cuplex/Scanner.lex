package generated.fr.ul.miashs.compil;

import java_cup.runtime.Symbol;

%%

%class Scanner
%public 
%line
%column
%cup

/* --- MACROS --- */
SEP =[ \t\r\n]
ENTIER = [0-9]+
ID = [a-zA-Z_][a-zA-Z0-9_]*
COMMENT_LIGNE = \/\/.*
COMMENT_MULTI = \/\*([^*]|(\*+[^*/]))*\*+\/

%%

/* --- MOTS-CLÉS JASON --- */
"principal" { return new Symbol(Sym.PRINCIPAL); }
"var"       { return new Symbol(Sym.VAR); }
"si"        { return new Symbol(Sym.SI); }
"sinon"     { return new Symbol(Sym.SINON); }
"tantque"   { return new Symbol(Sym.TANTQUE); }
"fonction"  { return new Symbol(Sym.FONCTION); }
"procedure" { return new Symbol(Sym.PROCEDURE); }
"retourner" { return new Symbol(Sym.RETOURNER); }
"lire"      { return new Symbol(Sym.LIRE); }
"ecrire"    { return new Symbol(Sym.ECRIRE); }

/* --- OPÉRATEURS RELATIONNELS --- */
"=="        { return new Symbol(Sym.EG); }
"!="        { return new Symbol(Sym.DIF); }
"<="        { return new Symbol(Sym.INFE); }
">="        { return new Symbol(Sym.SUPE); }
"<"         { return new Symbol(Sym.INF); }
">"         { return new Symbol(Sym.SUP); }

/* --- OPÉRATEURS ARITHMÉTIQUES ET AFFECTATION --- */
"+"         { return new Symbol(Sym.PLUS); }
"-"         { return new Symbol(Sym.MOINS); }
"*"         { return new Symbol(Sym.MUL); }
"/"         { return new Symbol(Sym.DIV); }
"="         { return new Symbol(Sym.AFFECTATION); }

/* --- DÉLIMITEURS --- */
"("         { return new Symbol(Sym.PO); }
")"         { return new Symbol(Sym.PF); }
"{"         { return new Symbol(Sym.AO); }
"}"         { return new Symbol(Sym.AF); }
";"         { return new Symbol(Sym.PV); }
","         { return new Symbol(Sym.VIRG); }

/* --- VALEURS --- */
{ID}        { return new Symbol(Sym.ID, yytext()); }
{ENTIER}    { return new Symbol(Sym.ENTIER, Integer.valueOf(yytext())); }

/* --- IGNORÉS --- */
{SEP}+            { /* Ignorer */ }
{COMMENT_LIGNE}   { /* Ignorer */ }
{COMMENT_MULTI}   { /* Ignorer */ }

/* --- ERREURS & FIN --- */
<<EOF>>     { return new Symbol(Sym.EOF); }
.           { System.err.println("Erreur lexicale : '" + yytext() + "' (ligne " + (yyline+1) + ")"); }


