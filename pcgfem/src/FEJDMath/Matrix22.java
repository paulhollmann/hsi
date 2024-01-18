package FEJDMath;

/**
 * Used to manipulate 2*2 matrix
 * Note : to compute : vectleft * m * vectright use the following syntax : m.multVectLeft(vectleft).mult(vectright);
 */
class Matrix22 {

    /**
     * The coefficients of the matrix
     */
    private double[] mat;

    /**
     * Makes a new empty matrix
     */
    public Matrix22() {
        mat = new double[4];
    }

    /**
     * Makes a new matrix with the coefficients x,y,z,t
     */
    public Matrix22(double x, double y, double z, double t) {
        mat = new double[]{x, y, z, t};
    }

    /**
     * Returns the value of the coefficient (a,b) of the matrix (1 based as usual in math)
     */
    public double getNum(int a, int b) {
        return mat[2 * (a - 1) + b - 1];
    }

    /**
     * Sets the coefficient (a,b) of the matrix to val (1 based as usual in math)
     */
    public void setNum(int a, int b, double val) {
        mat[2 * (a - 1) + b - 1] = val;
    }

    /**
     * Returns the determinant of the matrix
     */
    public double det() {
        return mat[0] * mat[3] - mat[1] * mat[2];
    }

    /**
     * Returns the invert of the matrix
     */
    public Matrix22 inv() {
        double d = det();
        return new Matrix22(mat[3] / d, -mat[1] / d, -mat[2] / d, mat[0] / d);
    }

    /**
     * Returns the matrix multiplied by m
     */
    public Matrix22 mult(Matrix22 m) {
        double m0 = m.mat[0];
        double m1 = m.mat[1];
        double m2 = m.mat[2];
        double m3 = m.mat[3];
        return new Matrix22(mat[0] * m0 + mat[1] * m2, mat[0] * m1 + mat[1] * m3, mat[2] * m0 + mat[3] * m2, mat[2] * m1 + mat[3] * m3);
    }

    /**
     * Returns the matrix multiplied by the scalar value l
     */
    public Matrix22 scalMult(double l) {
        return new Matrix22(l * mat[0], l * mat[1], l * mat[2], l * mat[3]);
    }

    /**
     * Returns the matrix transposed
     */
    public Matrix22 transpose() {
        return new Matrix22(mat[0], mat[2], mat[1], mat[3]);
    }

    /**
     * Returns the result of the multiplication of v and the matrix
     */
    public Vector2 multVectLeft(Vector2 v) {
        return new Vector2(mat[0] * v.getNum(1) + mat[2] * v.getNum(2), mat[1] * v.getNum(1) + mat[3] * v.getNum(2));
    }

    /**
     * Returns the result of the multiplication of the matrix and v
     */
    public Vector2 multVectRight(Vector2 v) {
        return new Vector2(mat[0] * v.getNum(1) + mat[1] * v.getNum(2), mat[2] * v.getNum(1) + mat[3] * v.getNum(2));
    }

    /**
     * Returns the matrix in a mathematical format
     */
    public String toString() {
        return "\n" + "[ " + mat[0] + " " + mat[1] + " ]" + "\n" + "[ " + mat[2] + " " + mat[3] + " ]" + "\n";
    }
}
