package FEJDMath;

/**
 * Represents a 2D linear function
 */
class LinearFct extends ShapeFunction {

    /**
     * Makes a new LinearFct of the form: a + b*X + c*Y
     */
    public LinearFct(int a, int b, int c) {
        setNbOfCoeff(3);
        setCoeffElem(0, a);
        setCoeffElem(1, b);
        setCoeffElem(2, c);
    }

    public Vector2 computeGradAt(double x, double y) {
        return new Vector2(getCoeffElem(1), getCoeffElem(2));
    }

    public double computeFctAt(double x, double y) {
        return getCoeffElem(0) + getCoeffElem(1) * x + getCoeffElem(2) * y;
    }

    /**
     * Returns a description of the function
     */
    public String toString() {
        return "AF - Linear : " + getCoeffElem(1) + "* X +" + getCoeffElem(2) + "* Y +" + getCoeffElem(0);
    }
}
