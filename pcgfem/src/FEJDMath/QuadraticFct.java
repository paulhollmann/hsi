package FEJDMath;

/**
 * Represents a 2D quadratic function
 */
class QuadraticFct extends ShapeFunction {

    /**
     * Makes a new QuadraticFct of the form: a + b*X + c*Y + d*X� + e*Y� + f*X*Y
     */
    public QuadraticFct(int a, int b, int c, int d, int e, int f) {
        setNbOfCoeff(6);
        setCoeffElem(0, a);
        setCoeffElem(1, b);
        setCoeffElem(2, c);
        setCoeffElem(3, d);
        setCoeffElem(4, e);
        setCoeffElem(5, f);
    }

    public Vector2 computeGradAt(double x, double y) {
        return new Vector2(getCoeffElem(1) + 2 * getCoeffElem(3) * x + getCoeffElem(5) * y, getCoeffElem(2) + 2 * getCoeffElem(4) * y + getCoeffElem(5) * x);
    }

    public double computeFctAt(double x, double y) {
        return getCoeffElem(0) + getCoeffElem(1) * x + getCoeffElem(2) * y + getCoeffElem(3) * x * x + getCoeffElem(4) * y * y + getCoeffElem(5) * x * y;
    }

    /**
     * Returns a description of the function
     */
    public String toString() {
        return "AF - Quadratic :" + getCoeffElem(0) + getCoeffElem(1) + "* X +" + getCoeffElem(2) + "* Y +" + getCoeffElem(3) + "* X*2 +" + getCoeffElem(4) + "* Y*2 +" + getCoeffElem(5) + "* X*Y";
    }
}
