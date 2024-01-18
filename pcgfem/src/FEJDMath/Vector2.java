package FEJDMath;

/**
 * Used to manipulate vertical vectors with 2 elements
 */
public class Vector2 {

    /**
     * The coefficients of the vector
     */
    private double[] vect;

    /**
     * Makes a new empty vector
     */
    public Vector2() {
        vect = new double[2];
    }

    /**
     * Makes a new vector with the coefficients x,y
     */
    public Vector2(double x, double y) {
        vect = new double[]{x, y};
    }

    /**
     * Returns the value of the ath coefficient of the vector (1 based as usual in math)
     */
    public double getNum(int a) {
        return vect[a - 1];
    }

    /**
     * Sets the ath coefficient of the vector to val (1 based as usual in math)
     */
    public void setNum(int a, double val) {
        vect[a - 1] = val;
    }

    /**
     * Returns the vector multiplied by v
     */
    public double mult(Vector2 v) {
        return (vect[0] * v.vect[0] + vect[1] * v.vect[1]);
    }

    /**
     * Returns  the vector added to v
     */
    public Vector2 add(Vector2 v) {
        return new Vector2(vect[0] + v.vect[0], vect[1] + v.vect[1]);
    }

    /**
     * Returns the vector in a mathematical format
     */
    public String toString() {
        return "\n" + "[ " + vect[0] + " ]" + "\n" + "[ " + vect[1] + " ]" + "\n";
    }
}
