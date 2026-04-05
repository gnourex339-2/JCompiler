# Package arbre

## Description

Ce projet a consisté en la réalisation d'un compilateur dans le cadre du cours de Compilation
en Licence MIASHS parcours MIAGE et TAL à l'Université de Lorraine




## Prérequis

Utiliser un IDE qui intègre Maven (par exemple VS Code, IntelliJ ou Eclipse)



## Lancer le compilateur

Pour tester le compilateur sur votre machine, suivez simplement ces deux étapes dans le terminal (VS Code, IntelliJ ou Eclipse), à la racine du projet.

---

### Étape 1 : Générer et compiler

Comme **JFlex** et **CUP** génèrent les fichiers `Scanner.java` et `ParserCup.java`, il suffit de lancer la commande Maven suivante :

```bash
mvn clean package
```

Cette commande :
- nettoie le projet
- lit la grammaire
- génère le code Java manquant
- compile l’ensemble
- crée l’exécutable dans le dossier `bindist/`

---

### Étape 2 : Exécuter le compilateur

Une fois le projet compilé, vous pouvez tester les fichiers d’exemple présents dans le dossier `samples/`.

Ces fichiers contiennent du code écrit dans le langage **"Jason"** que nous avons créé.

---

#### Sous Windows

Exemple avec le fichier `ex6.txt` (fonctions) :

```bash
bindist\bin\jasonc.bat samples\ex6.txt
```

---

#### Sous Mac / Linux

```bash
./bindist/bin/jasonc samples/e6.exp
```

Si vous obtenez une erreur **"permission denied"**, exécutez d'abord :

```bash
chmod +x bindist/bin/jasonc
```

---

### Résultat attendu

Après exécution, vous devriez voir :
- l’**Arbre Syntaxique Abstrait (AST)** s’afficher dans la console
- suivi du **code Assembleur Beta**

---

### Tester d'autres fichiers

Vous pouvez remplacer `ex6.txt` par n'importe quel autre fichier de test :

- `e1.exp`
- `e9.exp`
- etc.

## Projet Universitaire

(IDMC/Université de Lorraine)

##Licence

Licence MIT