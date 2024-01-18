package FEJDMath;

/**
 * Represents a 2D linear function
 */
class BilinearFct extends ShapeFunction {

    /**
     * Makes a new LinearFct of the form: a + b*X + c*Y + d*X*Y
     */
    public BilinearFct(int a, int b, int c, int d) {
        setNbOfCoeff(4);
        setCoeffElem(0, a);
        setCoeffElem(1, b);
        setCoeffElem(2, c);
        setCoeffElem(3, d);
    }

    public Vector2 computeGradAt(double x, double y) {
        return new Vector2(getCoeffElem(1) + getCoeffElem(3) * y, getCoeffElem(2) + getCoeffElem(3) * x);
    }

    public double computeFctAt(double x, double y) {
        return getCoeffElem(0) + getCoeffElem(1) * x + getCoeffElem(2) * y + getCoeffElem(3) * x * y;
    }

    /**
     * Returns a description of the function
     */
    public String toString() {
        return "AF - Bilinear : " + getCoeffElem(0) + getCoeffElem(1) + "* X +" + getCoeffElem(2) + "* Y +" + getCoeffElem(3) + "* X*Y";
    }
}
