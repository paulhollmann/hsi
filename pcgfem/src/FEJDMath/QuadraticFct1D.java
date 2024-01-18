package FEJDMath;

/**
 * Represents a 1D quadratic function
 */
class QuadraticFct1D extends ShapeFunction1D {
    // not an AnsatzFunction but an AnsatzFunction1D

    /**
     * Makes a new QuadraticFct1D of the form: a + b*X + c*X�
     */
    public QuadraticFct1D(int a, int b, int c) {
        setNbOfCoeff(3);
        setCoeffElem(0, a);
        setCoeffElem(1, b);
        setCoeffElem(2, c);
    }

    public double computeFctAt(double x) {
        return getCoeffElem(0) + getCoeffElem(1) * x + getCoeffElem(2) * x * x;
    }

    /**
     * Returns a description of the function
     */
    public String toString() {
        return "Fct - Quadratic1D : " + getCoeffElem(0) + " + " + getCoeffElem(1) + " * X +" + getCoeffElem(2) + " * X�";
    }
}
