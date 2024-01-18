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

        double[] v0 = new double[f.length];

        double[] r0 = new double[f.length];
        for(int i = 0; i < f.length; i++)
        {
            double[] temp = getMatVecProd(K, f);
            r0[i] = f[i] - temp[0];
        }

        var C_inv  = getInvJacobiPreconditioner(K);
        var d0 = getVectorVectorProduct(C_inv,r0);

        var gamma0 = getDotProd(d0, r0);

        // Vektor aus gammas erstellen?
        var gamma_k = 0;
        for(int k = 1; gamma_k < 0.0000001; k++)
        {


        }


        return v0;
    }

}
