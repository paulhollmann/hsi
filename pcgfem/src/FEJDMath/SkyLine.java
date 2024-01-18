package FEJDMath;

import FEJDMath.hsi.PCGSolver;
import FEJDMath.hsi.PCGSolverParallel;

import java.util.ArrayList;

/**
 * Uses to implement the skyline storage method for matrices, ie in a column, the zero elements above the first non zero element are not stored in the matrix. A pointer vector is used to point to the diagonal elements
 */
public class SkyLine {

    /**
     * The size of the matrix
     */
    private int size;    // size*size matrix
    /**
     * The coefficients of the matrix
     */
    private ArrayList<Double> K;
    /**
     * The pointer vector
     */
    private int[] Lk;

    /**
     * @param n size of the new skyline matrix
     */
    public SkyLine(int n) {
        Lk = new int[n];
        for (int i = 0; i < n; i++)
            Lk[i] = -1; // sets the matrix to 0
        K = new ArrayList();
        size = n;
    }

    /**
     * Adds x to the element (i,j) of the stiffness matrix
     */
    public void addSMatrix(int i, int j, double x) {
        //System.out.println("addSMatrix element [" + i + ", " + j + "] value=" + x);
        if (x != 0) {
            double t = get(i, j);
            set(i, j, t + x);
        }
    }

    /**
     * Retuns the (i,j) element of the stiffness matrix
     */
    public double getMatrixValue(int i, int j) {
        return get(i, j);
    }


    /**
     * Returns x the solution of the following problem : this * x = v
     */
    public double[] linSolve(double[] v) {
        // solve this * return = v

        SkyLine L = new SkyLine(size);
        computeLLT(L);

        double[] u = L.solveTinf(v);
        double[] x = L.solveTsup(u);

        return x;
    }

    /**
     * computes dot product
     */
    public double dotProd(double[] a, double[] b) {
        double prod = 0;
        for (int i = 0; i < a.length; i++) {
            prod += a[i] * b[i];
        }
        return prod;
    }

    /**
     * computes scaled update v +=alpha*d
     */
    public void update(double[] v, double alpha, double[] d) {
        for (int i = 0; i < v.length; i++) {
            v[i] += alpha * d[i];
        }
    }

    /**
     * computes scaled update d = p + beta*d
     */
    public void update11(double[] d, double beta, double[] p) {
        for (int i = 0; i < d.length; i++) {
            d[i] = p[i] + beta * d[i];
        }
    }

    /**
     * generates copy f
     */
    public double[] vCopy(double[] f) {
        double[] r = new double[size];
        for (int i = 0; i < size; i++) {
            r[i] = f[i];
        }
        return r;
    }

    /**
     * computes vector element-by-element product c = a*b
     */
    public void diagVec(double[] a, double[] b, double[] c) {
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] * b[i];
        }
    }

    /**
     * calculates x = this * c
     */
    public double[] SkyVec(double[] c) {
        double[] x = new double[size];
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double d = get(j + 1, i + 1);
                if (d != 0)
                    x[i] += d * c[j];
            }
        }
        return x;
    }

    /**
     * Generates inv diagonal vector of this
     */
    public double[] diagInv() {
        double[] d = new double[size];
        for (int i = 0; i < d.length; i++) {
            int i1 = i + 1;
            d[i] = 1.0 / get(i1, i1);
        }
        return d;
    }

    /**
     * Returns the size of shape the matrix
     */
    public int getSize() {
        return K.size();
    }

    /**
     * Returns the size of the matrix
     */
    public int getVectorSize() {
        return Lk.length;
    }

    /**
     * Returns the coefficient (i,j) of the matrix (1 based as usual in math)
     */
    public double get(int i, int j) {
        if (i > j) { // we only look in the upper part
            int tmp = i;
            i = j;
            j = tmp;
        }

        int ind = getIndice(i, j);
        if (ind == -1)
            return 0;
        else
            return K.get(ind).doubleValue();
    }

    /**
     * Sets the value of the coefficient (i,j) of the matrix to v (1 based as usual in math)
     */
    public void set(int i, int j, double v) {
        if (i > j) {    // we only look in the upper part
            int tmp = i;
            i = j;
            j = tmp;
        }

        int ind = getIndice(i, j);

        if (ind > -1) // if i,j is under the skyline
            K.set(ind, new Double(v));
        else {
            int rbound = Lk[j - 1];
            int lbound;
            int k = j - 2;
            while ((k >= 0) && (Lk[k] == -1))
                k--;
            if (k >= 0)
                lbound = Lk[k];
            else
                lbound = -1;

            K.add(lbound + 1, new Double(v));
            if (rbound == -1) { // empty column
                for (int l = 0; l < (j - i); l++)
                    K.add(lbound + 2, new Double(0));
                Lk[j - 1] = lbound + j - i + 1;
                for (int p = j; p < size; p++)
                    if (Lk[p] > -1)
                        Lk[p] += j - i + 1;
            } else {
                for (int l = 0; l < (j - i) - (rbound - lbound); l++)
                    K.add(lbound + 2, new Double(0));
                for (int p = j - 1; p < size; p++)
                    if (Lk[p] > -1)
                        Lk[p] += (j - i) - (rbound - lbound) + 1;
            }
        }
    }

    /**
     * Returns the index in the vector of the preceding diagonal element
     */
    private int getIndice(int i, int j) {
        if (Lk[j - 1] == -1) // empty column
            return -1;
        else {
            int lbound, rbound;
            rbound = Lk[j - 1];
            int k = j - 2;
            while ((k >= 0) && (Lk[k] == -1))
                k--;
            if (k >= 0)
                lbound = Lk[k];
            else
                lbound = -1;
            if ((rbound - lbound) > (j - i))
                return rbound - (j - i);
            else
                return -1;
        }
    }

    /**
     * Returns the matrix in its usual mathematical format (can be very long for large matrices)
     */
    public String toString() {
        String s = "";
        for (int i = 1; i <= size; i++) {
            s += "[ ";
            for (int j = 1; j <= size; j++)
                s += "" + get(i, j) + " ";
            s += "]\n";
        }
        return s;
    }

    /**
     * Returns the items contained in the pointer and those contained in the ArrayList, for debugging
     */
    public String toString2() {
        String s = "Pointer : ";

        s += "[ ";
        for (int j = 0; j < size; j++)
            s += "" + Lk[j] + " ";
        s += "]\n";

        s += K.toString();

        return s;
    }

    /**
     * Modifies the argument S so it contains the upper part of the decomposition of the current matrix
     */
    public void computeLLT(SkyLine S) {
        // at the end we have : this = ST * S
        int N = size;
        for (int i = 0; i < N; i++)
            S.Lk[i] = Lk[i];
        for (int i = 0; i < K.size(); i++)
            S.K.add(i, K.get(i));

        S.set(1, 1, Math.sqrt(get(1, 1)));

        for (int j = 2; j <= N; j++) {

            int loj;

            int lbound, rbound;
            rbound = Lk[j - 1];
            if (j - 2 >= 0)
                lbound = Lk[j - 2];
            else
                lbound = -1;

            loj = j - (rbound - lbound);


            if (loj + 1 <= j - 1)
                for (int i = loj + 1; i <= j - 1; i++) {
                    int loi;
                    rbound = Lk[i - 1];
                    if (i - 2 >= 0)
                        lbound = Lk[i - 2];
                    else
                        lbound = -1;

                    loi = i - (rbound - lbound);
                    double sum = 0;

                    for (int l = Math.max(loi, loj) + 1; l <= i - 1; l++)
                        sum += S.get(l, i) * S.get(l, j);

                    S.set(i, j, 1 / S.get(i, i) * (get(i, j) - sum));

                }

            double sum = 0;

            for (int l = loj + 1; l <= j - 1; l++)
                sum += S.get(l, j) * S.get(l, j);

            S.set(j, j, Math.sqrt(get(j, j) - sum));

        }
    }

    /**
     * Returns the solution of the following problem : this * sol = f with this considered as upper-triangular
     */
    private double[] solveTsup(double[] f) {
        double[] sol = new double[size];

        sol[size - 1] = f[size - 1] / get(size, size);

        for (int i = size - 1; i >= 1; i--) {

            double sum = 0;
            for (int j = i + 1; j <= size; j++)
                sum += get(i, j) * sol[j - 1];

            sol[i - 1] = 1 / get(i, i) * (f[i - 1] - sum);

        }

        return sol;
    }

    /**
     * Returns the solution of the following problem : this * sol = f with this considered as lower-triangular (but still stored the same way)
     */
    private double[] solveTinf(double[] f) {
        double[] sol = new double[size];

        sol[0] = f[0] / get(1, 1);

        for (int i = 2; i <= size; i++) {

            double sum = 0;
            for (int j = 1; j <= i - 1; j++)
                sum += get(j, i) * sol[j - 1];

            sol[i - 1] = 1 / get(i, i) * (f[i - 1] - sum);

        }


        return sol;
    }

    /**
     * Returns x the cg solution of the following problem : this * v = f
     */
    public double[] cgSolve(double[] f) {
        double[] v = new double[size];
        double[] r = vCopy(f);
        double[] s = vCopy(r);
        double gamma = dotProd(r, r);
        double eps = 1E-7 * gamma;
        for (int i = 0; ; i++) {
            double[] u = SkyVec(s);
            double alpha = gamma / dotProd(u, s);
            update(v, alpha, s);
            update(r, -alpha, u);
            double gamma_neu = dotProd(r, r);
            //System.out.println(i + ". Residuum " + gamma_neu);
            if (gamma_neu <= eps) break;
            double beta = gamma_neu / gamma;
            update11(s, beta, r);
            gamma = gamma_neu;
        }
        return v;
    }

    /**
     * Returns x the pcg solution of the following problem : this * v = f
     */
    public double[] pcgSolveParallel(double[] f) {
        double[][] K1 = new double[size][size];
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++)
                K1[i - 1][j - 1] = get(i, j);
        }
        return PCGSolverParallel.solve(K1, f);
    }

    /**
     * Returns x the pcg solution of the following problem : this * v = f
     */
    public double[] pcgSolve(double[] f) {
        double[][] K1 = new double[size][size];
        for (int i = 1; i <= size; i++) {
            for (int j = 1; j <= size; j++)
                K1[i - 1][j - 1] = get(i, j);
        }
        return PCGSolver.solve(K1, f);
    }
}
