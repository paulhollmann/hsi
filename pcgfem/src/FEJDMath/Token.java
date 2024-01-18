package FEJDMath;

/**
 * Represents a token of the string
 */
class Token implements Cloneable {
    // constants
    /**
     * Empty constant
     */
    public static final int empty = 0;
    /**
     * Function constant
     */
    public static final int func = 1;
    /**
     * Numeric constant
     */
    public static final int numeric = 2;
    /**
     * Addition constant
     */
    public static final int addition = 3;
    /**
     * Multiplication constant
     */
    public static final int multiplication = 4;
    /**
     * Substration constant
     */
    public static final int soustraction = 5;
    /**
     * Division constant
     */
    public static final int division = 6;
    /**
     * Left parenthesis constant
     */
    public static final int pgauche = 7;
    /**
     * Right parenthesis constant
     */
    public static final int pdroit = 8;
    /**
     * List of the supported math functions in upper case
     */
    private String[] supportedMFct = {"TAN", "SIN", "COS", "EXP", "LN", "LOG", "SQRT", "PI", "X", "Y"};
    /**
     * The type of the token (one of the constant)
     */
    private int type; // one of the constans constantes
    /**
     * The numerical value of the token
     */
    private double value;
    /**
     * The name of the function represented by the token
     */
    private String name; // the name of the function

    public Token(String s) {
        setToken(s);
    }

    /**
     * Returns the type of the token
     */
    public int getType() {
        return type;
    }

    /**
     * Sets the type of the token
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Returns the value of the token
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value of the token
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Returns the name of the math function
     */
    public String getName() {
        return name;
    }

    /**
     * Checks of the math function s is supported
     */
    private boolean isMFctSupported(String s) {
        boolean result = false;

        for (int i = 0; i < supportedMFct.length; i++) {
            if (supportedMFct[i].equals(s)) {
                result = true;
            }

        }
        return result;
    }

    /**
     * Sets the token to its value
     */
    private void setToken(String s) {
        if (s.equals("+"))
            type = addition;
        else if (s.equals("*"))
            type = multiplication;
        else if (s.equals("-"))
            type = soustraction;
        else if (s.equals("/"))
            type = division;
        else if (s.equals("("))
            type = pgauche;
        else if (s.equals(")"))
            type = pdroit;
        else if (s.equals(""))
            type = empty;
            // les fonctions mathematiques
        else if (isMFctSupported(s)) {
            type = func;
            name = s;
        }
        // les chiffres
        else {
            try {
                value = Double.parseDouble(s);
                type = numeric;
            } catch (NumberFormatException e) {
                type = empty;
            }
        }
    }

    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return new Object();
        }
    }
}
