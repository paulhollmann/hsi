package FEJDMath.hsi;

import java.util.Arrays;

public class MathOperations {


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
