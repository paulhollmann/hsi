package FEJDMath.hsi;

import mpi.MPI;
import mpi.Request;

import static FEJDMath.hsi.MathOperations.*;

public class PCGSolverParallel {

    public static double[][] get_K_local(double[][] K_global, int number) {
        assert K_global.length == 9;
        assert K_global[0].length == 9;
        var K_local = new double[6][6];
        if (number == 0) {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    double couplingfactor = i > 3 && j > 3 ? 0.5 : 1;
                    K_local[i][j] = couplingfactor * K_global[i][j];
                }
            }
        } else {
            for (int i = 0; i < 6; i++) {
                for (int j = 0; j < 6; j++) {
                    double couplingfactor = i + 3 < 6 && j + 3 < 6 ? 0.5 : 1;
                    K_local[i][j] = couplingfactor * K_global[i + 3][j + 3];
                }
            }
        }
        return K_local;
    }

    public static double[] get_f_local(double[] f_global, int number) {
        assert f_global.length == 9;
        var f_local = new double[6];
        if (number == 0) {
            for (int i = 0; i < 6; i++) {
                double couplingfactor = i > 3 ? 0.5 : 1;
                f_local[i] = couplingfactor * f_global[i];
            }
        } else {
            for (int i = 0; i < 6; i++) {
                double couplingfactor = i + 3 < 6 ? 0.5 : 1;
                f_local[i] = couplingfactor * f_global[i + 3];
            }
        }
        return f_local;
    }

    public static double[] get_absolute_vec(double[] local_additive_vec, int number) {
        double[] result;
        double[] result_from_other = new double[6];
        result = copy(local_additive_vec);
        Request req = MPI.COMM_WORLD.Isend(result, 0, 6, MPI.DOUBLE, number + 1 % 2, 43454);
        MPI.COMM_WORLD.Recv(result_from_other, 0, 6, MPI.DOUBLE, number + 1 % 2, 43454);
        if (number == 0) {
            for (int i = 0; i < 3; i++) {
                result[i + 3] += result_from_other[i];
            }
        } else {
            for (int i = 0; i < 3; i++) {

                result[i] += result_from_other[i + 3];
            }
        }
        req.Wait();
        return result;
    }

    public static double get_absolute_scalar(double local_additive_scalar, int number) {
        double[] result = new double[]{local_additive_scalar};
        double[] result_from_other = new double[1];
        Request req = MPI.COMM_WORLD.Isend(result, 0, 1, MPI.DOUBLE, number + 1 % 2, 43454);
        MPI.COMM_WORLD.Recv(result_from_other, 0, 1, MPI.DOUBLE, number + 1 % 2, 43454);
        result[0] += result_from_other[0];
        req.Wait();
        return result[0];
    }

    /**
     * Solve double [ ].
     *
     * @param K Matrix K
     * @param f Vektor f
     * @return Vektor v
     */
    public static double[] solve(double[][] K, double[] f) {

        final int rank = 0; //MPI.COMM_WORLD.Rank();

        var K_local = get_K_local(K, rank);

        var f_local = get_f_local(f, rank);


        var v_k_prev_local = copy(f_local);

        var r_k_prev_local = getVectorVectorSub(f_local, getMatVecProd(K_local, v_k_prev_local));

        //var C_inv  = getInvJacobiPreconditioner(K);
        var d_k_prev_local = get_absolute_vec(r_k_prev_local, rank); // NOT PRECONDITIONED

        var gamma_k_prev_local = get_absolute_scalar(getDotProd(d_k_prev_local, r_k_prev_local), rank);

        double eps = 1E-7 * gamma_k_prev_local;

        double gamma_k;
        double[] d_k;
        double[] v_k;
        double[] r_k;

        for (int k = 1; ; k++) {
            var u = getMatVecProd(K_local, d_k_prev_local);
            var du = 0.0;

            /*
            var alpha = gamma_k_prev/getDotProd(d_k_prev,u);


            v_k = getVectorVectorAdd(v_k_prev, getScalarVectorMul(alpha,  d_k_prev));
            r_k = getVectorVectorSub(r_k_prev, getScalarVectorMul(alpha,  u));

            var p = getVectorVectorProduct(C_inv, r_k);

            gamma_k = getDotProd(r_k, p);
            if (gamma_k < eps) {
                System.out.println("Took k=" + k +" iterations.");
                break;
            }

            var beta = gamma_k/gamma_k_prev;
            d_k = getVectorVectorAdd(p, getScalarVectorMul(beta, d_k_prev));

            v_k_prev = v_k;
            r_k_prev = r_k;
            d_k_prev = d_k;
            gamma_k_prev = gamma_k;

             */
            return u;
        }

        //return v_k;
    }
}
