package FEJDMath.hsi;

import static FEJDMath.hsi.MathOperations.*;

public class PCGSolver {
    /**
     * Solve double [ ].
     *
     * @param K Matrix K
     * @param f Vektor f
     * @return Vektor v
     */
    public static double[] solve(double[][] K, double[] f) {

        var v_k_prev = copy(f);

        var r_k_prev = getVectorVectorSub(f, getMatVecProd(K, f));

        var C_inv = getInvJacobiPreconditioner(K);
        var d_k_prev = getVectorVectorProduct(C_inv, r_k_prev);

        var gamma_k_prev = getDotProd(d_k_prev, r_k_prev);

        double eps = 1E-7 * gamma_k_prev;

        double gamma_k;
        double[] d_k;
        double[] v_k;
        double[] r_k;

        for (int k = 1; ; k++) {
            var u = getMatVecProd(K, d_k_prev);
            var alpha = gamma_k_prev / getDotProd(d_k_prev, u);

            v_k = getVectorVectorAdd(v_k_prev, getScalarVectorMul(alpha, d_k_prev));
            r_k = getVectorVectorSub(r_k_prev, getScalarVectorMul(alpha, u));

            var p = getVectorVectorProduct(C_inv, r_k);

            gamma_k = getDotProd(r_k, p);
            if (gamma_k < eps) {
                System.out.println("Took k=" + k + " iterations. Gamma_k " + gamma_k + "  <  eps " + eps);
                break;
            }

            var beta = gamma_k / gamma_k_prev;
            d_k = getVectorVectorAdd(p, getScalarVectorMul(beta, d_k_prev));

            v_k_prev = v_k;
            r_k_prev = r_k;
            d_k_prev = d_k;
            gamma_k_prev = gamma_k;
        }


        return v_k;
    }

}
