package FEJDMath;

/**
 * Represents a 2D shape function, abstract class used to create the 2D functions we use
 */
abstract class ShapeFunction {

    /**
     * The coefficients of the function
     */
    private double[] coeffElem;

    /**
     * Returns the ith coefficient of the function
     */
    public double getCoeffElem(int i) {
        return coeffElem[i];
    }

    /**
     * Sets the ith coefficient of the function to value
     */
    public void setCoeffElem(int i, double d) {
        coeffElem[i] = d;
    }

    /**
     * Sets the number of coefficients of the function to i
     */
    public void setNbOfCoeff(int i) {
        coeffElem = new double[i];
    }

    /**
     * Returns the value of the gradient of the function at (x,y)
     */
    public abstract Vector2 computeGradAt(double x, double y);

    /**
     * Returns the value of the function at (x,y)
     */
    public abstract double computeFctAt(double x, double y);

    /**
     * Returns a description of the function
     */
    public abstract String toString();
}
