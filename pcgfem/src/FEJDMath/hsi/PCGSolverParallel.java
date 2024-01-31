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

    public static double[] get_C_inv_local(double[][] K_global, int number) {
        assert K_global.length == 9;
        var C_inv_local = new double[6];
        if (number == 0) {
            for (int i = 0; i < 6; i++) {
                double couplingfactor = i > 3 ? 0.5 : 1;
                C_inv_local[i] = couplingfactor * K_global[i][i];
            }
        } else {
            for (int i = 0; i < 6; i++) {
                double couplingfactor = i + 3 < 6 ? 0.5 : 1;
                C_inv_local[i] = couplingfactor * K_global[i + 3][i + 3];
            }
        }
        return C_inv_local;
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


    /**
     * reduce the vector according to  and synchronize the value on all threads
     *
     * @param local_additive_vec double input vector
     * @param number             process rank
     */
    public static double[] get_absolute_vec(double[] local_additive_vec, int number) {
        double[] result;
        double[] result_from_other = new double[6];
        result = copy(local_additive_vec);
        Request req = MPI.COMM_WORLD.Isend(result, 0, 6, MPI.DOUBLE, (number + 1) % 2, 33453);
        MPI.COMM_WORLD.Recv(result_from_other, 0, 6, MPI.DOUBLE, (number + 1) % 2, 33453);
        req.Wait();

        if (number == 0) {
            for (int i = 0; i < 3; i++) {
                result[i + 3] += result_from_other[i];
            }
        } else {
            for (int i = 0; i < 3; i++) {

                result[i] += result_from_other[i + 3];
            }
        }
        return result;
    }

    /**
     * reduce the value and synchronize the value on all threads
     *
     * @param local_additive_scalar double input scalar
     * @param number                process rank
     */
    public static double get_absolute_scalar(double local_additive_scalar, int number) {
        double[] result = new double[]{local_additive_scalar};
        double[] result_from_other = new double[1];
        Request req = MPI.COMM_WORLD.Isend(result, 0, 1, MPI.DOUBLE, (number + 1) % 2, 43454);
        MPI.COMM_WORLD.Recv(result_from_other, 0, 1, MPI.DOUBLE, (number + 1) % 2, 43454);
        req.Wait();
        result[0] += result_from_other[0];
        return result[0];
    }

    public static double[] get_combined_vec(double[] local_additive_vec, int number) {
        double[] result = new double[9];
        double[] result_from_me = copy(local_additive_vec);
        double[] result_from_other = new double[6];

        Request req = MPI.COMM_WORLD.Isend(result_from_me, 0, 6, MPI.DOUBLE, (number + 1) % 2, 11413);
        MPI.COMM_WORLD.Recv(result_from_other, 0, 6, MPI.DOUBLE, (number + 1) % 2, 11413);
        req.Wait();
        if (number == 0) {
            for (int i = 0; i < 3; i++) {
                result[i] = local_additive_vec[i];
            }
            for (int i = 0; i < 3; i++) {
                result[i + 3] = local_additive_vec[i + 3];
            }
            for (int i = 0; i < 3; i++) {
                result[i + 6] = result_from_other[i + 3];
            }
        } else {
            for (int i = 0; i < 3; i++) {
                result[i] = result_from_other[i];
            }
            for (int i = 0; i < 3; i++) {
                result[i + 3] = local_additive_vec[i];
            }
            for (int i = 0; i < 3; i++) {
                result[i + 6] = local_additive_vec[i + 3];
            }
        }

        return result;
    }

    /**
     * Solve double [ ].
     *
     * @param K Matrix K
     * @param f Vektor f
     * @return Vektor v
     */
    public static double[] solve(double[][] K, double[] f) {

        final int rank = MPI.COMM_WORLD.Rank();

        var K_local = get_K_local(K, rank);

        var f_local = get_f_local(f, rank);


        var v_k_prev_local = copy(f_local);

        var r_k_prev_local = getVectorVectorSub(f_local, getMatVecProd(K_local, v_k_prev_local));

        var C_inv_local = get_C_inv_local(K, rank);

        var d_k_prev_local = get_absolute_vec(getVectorVectorProduct(C_inv_local, r_k_prev_local), rank); // PRECONDITIONED

        var gamma_k_prev_local = get_absolute_scalar(getDotProd(d_k_prev_local, r_k_prev_local), rank);

        double eps = 1E-7 * gamma_k_prev_local;

        double gamma_k;
        double[] d_k;
        double[] v_k;
        double[] r_k;

        for (int k = 1; ; k++) {
            var u = getMatVecProd(K_local, d_k_prev_local);
            var du = getDotProd(d_k_prev_local, u);
            var du_abs = get_absolute_scalar(du, rank);
            var alpha = gamma_k_prev_local / du_abs;

            v_k = getVectorVectorAdd(v_k_prev_local, getScalarVectorMul(alpha, d_k_prev_local));

            r_k = getVectorVectorSub(r_k_prev_local, getScalarVectorMul(alpha, u));

            var p = getVectorVectorProduct(C_inv_local, r_k); // Precond

            p = get_absolute_vec(p, rank);

            gamma_k = getDotProd(r_k, p);

            gamma_k = get_absolute_scalar(gamma_k, rank);

            if (gamma_k < eps) {
                System.out.println("Took k=" + k + " iterations. Gamma_k " + gamma_k + "  <  eps " + eps);
                break;
            }
            var beta = gamma_k / gamma_k_prev_local;
            d_k = getVectorVectorAdd(p, getScalarVectorMul(beta, d_k_prev_local));


            v_k_prev_local = v_k;
            r_k_prev_local = r_k;
            d_k_prev_local = d_k;
            gamma_k_prev_local = gamma_k;


        }

        return get_combined_vec(v_k, rank);
    }
}
