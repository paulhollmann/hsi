package FEJDMath;

/**
 * Represents a 2D quadratic function
 */
class BiquadraticFct extends ShapeFunction {

    /**
     * Makes a new QuadraticFct of the form: a + b*X + c*Y + d*X? + e*Y? + f*X*Y + g*X?*Y + h*X*Y? + i*X?*Y?
     */
    public BiquadraticFct(int a, int b, int c, int d, int e, int f, int g, int h, int i) {
        setNbOfCoeff(9);
        setCoeffElem(0, a);
        setCoeffElem(1, b);
        setCoeffElem(2, c);
        setCoeffElem(3, d);
        setCoeffElem(4, e);
        setCoeffElem(5, f);
        setCoeffElem(6, g);
        setCoeffElem(7, h);
        setCoeffElem(8, i);
    }

    public Vector2 computeGradAt(double x, double y) {

        double gx = getCoeffElem(1) + 2 * getCoeffElem(3) * x + getCoeffElem(5) * y + 2 * getCoeffElem(6) * x * y + getCoeffElem(7) * y * y + 2 * getCoeffElem(8) * x * y * y;
        double gy = getCoeffElem(2) + 2 * getCoeffElem(4) * y + getCoeffElem(5) * x + getCoeffElem(6) * x * x + 2 * getCoeffElem(7) * y * x + 2 * getCoeffElem(8) * x * x * y;

        return new Vector2(gx, gy);
    }

    public double computeFctAt(double x, double y) {
        return getCoeffElem(0) + getCoeffElem(1) * x + getCoeffElem(2) * y + getCoeffElem(3) * x * x + getCoeffElem(4) * y * y + getCoeffElem(5) * x * y + getCoeffElem(6) * x * x * y + getCoeffElem(7) * x * y * y + getCoeffElem(8) * x * x * y * y;
    }

    /**
     * Returns a description of the function
     */
    public String toString() {
        return "AF - Biquadratic :" + getCoeffElem(0) + getCoeffElem(1) + "* X +" + getCoeffElem(2) + "* Y +" + getCoeffElem(3) + "* X*2 +" + getCoeffElem(4) + "* Y*2 +" + getCoeffElem(5) + "* X*Y" + getCoeffElem(6) + "* X?*Y" + getCoeffElem(7) + "* X*Y?" + getCoeffElem(8) + "* X?*Y?";
    }
}
