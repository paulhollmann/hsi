package FEJDMath;

import java.util.StringTokenizer;

/**
 * Used to evaluate a function given as a string in the data file, the parser understands tan, sin, cos, exp, ln, log, sqrt, pi, X, Y
 */
class FctParser {
    /**
     * The maximum numbr of token that the string may have (all symbols including spaces are considered tokens)
     */
    private final int maxToken = 100; // nombre de token max
    /**
     * The token list on which we work
     */
    private Token[] liste;
    /**
     * The original token list
     */
    private Token[] originalListe;
    /**
     * The string representing the function
     */
    private String function;
    /**
     * The size of the array containing the tokens
     */
    private int arraySize;
    /**
     * The coordinates at which the function is calculated
     */
    private double x, y; // coordonn?es ou l'on calcule la fonction

    /**
     * Creates a new parser and loads the string s
     */
    public FctParser(String s) {
        function = s;
        s = s.toUpperCase();
        originalListe = new Token[maxToken];

        int i = 0;
        StringTokenizer st = new StringTokenizer(s, "+*-/() ", true);
        while (st.hasMoreTokens()) {
            originalListe[i] = new Token(st.nextToken().trim());
            i++;
        }
        arraySize = i;
    }

    /**
     * Computes the loaded function at the point (x,y)
     */
    public double calcFctAt(double x, double y) {
        this.x = x;
        this.y = y;
        liste = new Token[maxToken];

        for (int i = 0; i < arraySize; i++) {
            liste[i] = (Token) originalListe[i].clone();
        }

        //liste = (Token[])(originalListe.clone());
        splitPar(0, arraySize - 1);

        int ret = findNext(0, arraySize - 1);
        return liste[ret].getValue();
    }

    /**
     * Splits the string according to parentheses and calls calculToken to compute each token
     */
    private void splitPar(int deb, int fin) { // decoupe suivant les parenth?ses et appelle calculToken
        int[] open = new int[10]; // pile des parenth?ses ouvrantes
        int tot = 0;

        for (int i = deb; i <= fin; i++) {
            if (liste[i].getType() == Token.pgauche) {
                open[tot] = i;
                tot++;
            } else if (liste[i].getType() == Token.pdroit) // parenth?se fermante
            {
                // on remplace les fonctions mathematiques entre ( et )
                replaceMFct(open[tot - 1] + 1, i - 1);

                // on evalue la chaine entre i-1 et la derni?re parenth?se ouvrante
                caculToken(open[tot - 1] + 1, i - 1);
                // on met ( et ) ? empty
                liste[i].setType(Token.empty);
                liste[open[tot - 1]].setType(Token.empty);

                tot--;
            }
        }

        // on remplace les fonctions mathematiques
        replaceMFct(deb, fin);

        // on termine le calcul
        caculToken(deb, fin);
    }

    /**
     * Computes each token which represents an operation (+ * - /)
     */
    private void caculToken(int deb, int fin) {

        for (int z = deb; z <= fin; z++) {
            if (liste[z].getType() == Token.multiplication || liste[z].getType() == Token.division) {
                int pre = findPrec(z, deb);
                int suiv = findNext(z, fin);

                liste[pre].setType(Token.numeric);

                if (liste[z].getType() == Token.multiplication) {
                    liste[pre].setValue(liste[pre].getValue() * liste[suiv].getValue());
                } else {
                    liste[pre].setValue(liste[pre].getValue() / liste[suiv].getValue());
                }

                liste[z].setType(Token.empty);
                liste[suiv].setType(Token.empty);

                z = suiv;
            }
        }

        for (int z = deb; z <= fin; z++) {
            if (liste[z].getType() == Token.addition || liste[z].getType() == Token.soustraction) {
                int pre = findPrec(z, deb);
                int suiv = findNext(z, fin);

                liste[pre].setType(Token.numeric);

                if (liste[z].getType() == Token.addition) {
                    liste[pre].setValue(liste[pre].getValue() + liste[suiv].getValue());
                } else {
                    liste[pre].setValue(liste[pre].getValue() - liste[suiv].getValue());
                }

                liste[z].setType(Token.empty);
                liste[suiv].setType(Token.empty);

                z = suiv;
            }
        }
    }

    /**
     * Computes the math functions
     */
    private void replaceMFct(int deb, int fin) {
        for (int i = fin; i >= deb; i--) {
            if (liste[i].getType() == Token.func) {
                if (liste[i].getName().equals("TAN")) {
                    int ind = findNext(i, fin);
                    liste[i].setValue(Math.tan(liste[ind].getValue()));
                    liste[ind].setType(Token.empty);
                }
                if (liste[i].getName().equals("SIN")) {
                    int ind = findNext(i, fin);
                    liste[i].setValue(Math.sin(liste[ind].getValue()));
                    liste[ind].setType(Token.empty);
                }
                if (liste[i].getName().equals("COS")) {
                    int ind = findNext(i, fin);
                    liste[i].setValue(Math.cos(liste[ind].getValue()));
                    liste[ind].setType(Token.empty);
                }
                if (liste[i].getName().equals("EXP")) {
                    int ind = findNext(i, fin);
                    liste[i].setValue(Math.exp(liste[ind].getValue()));
                    liste[ind].setType(Token.empty);
                }
                if (liste[i].getName().equals("LOG") || liste[i].getName().equals("LN")) {
                    int ind = findNext(i, fin);
                    liste[i].setValue(Math.log(liste[ind].getValue()));
                    liste[ind].setType(Token.empty);
                }
                if (liste[i].getName().equals("SQRT")) {
                    int ind = findNext(i, fin);
                    liste[i].setValue(Math.sqrt(liste[ind].getValue()));
                    liste[ind].setType(Token.empty);
                }
                if (liste[i].getName().equals("PI"))
                    liste[i].setValue(Math.PI);
                if (liste[i].getName().equals("X"))
                    liste[i].setValue(x);
                if (liste[i].getName().equals("Y"))
                    liste[i].setValue(y);
                liste[i].setType(Token.numeric);
            }
        }
    }

    /**
     * Finds the preceding non empty token in liste
     */
    private int findPrec(int z, int deb) {
        while (liste[z].getType() != Token.numeric && z > deb) {
            z--;
        }
        return z;
    }

    /**
     * Finds the following non empty token in liste
     */
    private int findNext(int z, int fin) {
        while (liste[z].getType() != Token.numeric && z < fin) {
            z++;
        }
        return z;
    }

    /**
     * Displays the list of tokens
     */
    public void dispToken() {
        // displays the tokens
        for (int z = 0; z < arraySize; z++) {
            if (originalListe[z].getType() == Token.numeric) {
                System.out.println("value :  " + originalListe[z].getValue());
            } else if (originalListe[z].getType() == Token.empty) {
                System.out.println("empty");
            } else {
                System.out.println("type :  " + originalListe[z].getType());
            }
        }
    }

    /**
     * Returns the string representing the function
     */
    public String getFunction() {
        return function;
    }
}
