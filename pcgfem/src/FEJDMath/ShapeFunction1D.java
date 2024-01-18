package FEJDMath;

/**
 * Represents a 1D shape function, abstract class used to create the 1D functions we use
 */
abstract class ShapeFunction1D {

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
    public void setCoeffElem(int i, double value) {
        coeffElem[i] = value;
    }

    /**
     * Sets the number of coefficients of the function to i
     */
    public void setNbOfCoeff(int i) {
        coeffElem = new double[i];
    }

    /**
     * Returns the value of the function at x
     */
    public abstract double computeFctAt(double x);

    /**
     * Returns a description of the function
     */
    public abstract String toString();
}
