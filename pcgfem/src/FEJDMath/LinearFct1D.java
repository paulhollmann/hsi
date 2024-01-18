package FEJDMath;

/**
 * Represents a 1D linear function
 */
class LinearFct1D extends ShapeFunction1D {
    // not an AnsatzFunction but an AnsatzFunction1D

    /**
     * Makes a new LinearFct1D of the form: a + b*X
     */
    public LinearFct1D(int a, int b) {
        setNbOfCoeff(2);
        setCoeffElem(0, a);
        setCoeffElem(1, b);
    }

    public double computeFctAt(double x) {
        return getCoeffElem(0) + getCoeffElem(1) * x;
    }

    /**
     * Returns a description of the function
     */
    public String toString() {
        return "Fct - Linear1D : " + getCoeffElem(1) + "* X +" + getCoeffElem(0);
    }
}
