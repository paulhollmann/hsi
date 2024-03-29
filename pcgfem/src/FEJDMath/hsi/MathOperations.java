package FEJDMath.hsi;

import java.util.Arrays;

public class MathOperations {


    public static double getDotProd(double[] vec1, double[] vec2){
        assert vec1.length == vec2.length;
        double result = 0.;
        for (int i = 0; i < vec1.length; i++) {
            result += vec1[i] * vec2[i];
        }
        return result;
    }


    public static double[] getInvJacobiPreconditioner(double[][] mat){
        assert mat.length == mat[0].length;
        var result = new double[mat.length];
        for (int i = 0; i < mat.length; i++) {
            result[i] = 1.0/mat[i][i];
        }
        return result;
    }

    public static double[] getVectorVectorProduct(double[] vec1, double[] vec2){ // special
        assert vec1.length == vec2.length ;
        var res = copy(vec1);
        for (int i = 0; i < vec1.length; i++) {
            res[i] *= vec2[i];
        }
        return res;
    }

    public static double[] getVectorVectorSub(double[] vec1, double[] vec2){
        assert vec1.length == vec2.length;
        var res = copy(vec1);
        for (int i = 0; i < vec1.length; i++) {
            res[i] -= vec2[i];
        }
        return res;
    }

    public static double[] getVectorVectorAdd(final double[] vec1, final double[] vec2){
        assert vec1.length == vec2.length ;
        var res = copy(vec1);
        for (int i = 0; i < vec1.length; i++) {
            res[i] += vec2[i];
        }
        return res;
    }

    public static double[] getScalarVectorMul(double scalar, double[] vec){
        var res = copy(vec);
        for (int i = 0; i < vec.length; i++) {
            res[i] *= scalar;
        }
        return res;
    }

    public static double[] getMatVecProd(double[][] mat, double[] vec){
        assert mat[0].length == vec.length; // mat[0] or mat .length ??
        var result = new double[mat.length];
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                result[i] += mat[i][j] * vec[j];
            }
        }
        return result;
    }


    public static double[] copy(double[] vec){
        var res = new double[vec.length];
        System.arraycopy(vec, 0, res, 0, vec.length);
        return res;
    }


    public static void print(double[] v) {
        System.out.println(Arrays.toString(v));
    }

    public static void print(double[][] m, int j) {
        for (int i = 0; i < m.length; i++)
            System.out.println(j + Arrays.toString(m[i]));
    }

    private static boolean thresholdBasedFloatsComparison(double a, double b) {
        final double THRESHOLD = .001;

        return Math.abs(a - b) < THRESHOLD;
    }

    public static boolean vectorComparison(double[] a, double[] b) {
        if (a.length != b.length) return false;
        for (int i = 0; i < a.length; i++) {
            if (!thresholdBasedFloatsComparison(a[i], b[i]))
                return false;
        }
        return true;
    }
}
