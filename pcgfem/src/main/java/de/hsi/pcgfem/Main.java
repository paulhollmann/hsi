package de.hsi.pcgfem;

import mpi.MPI;
import mpi.Request;


public class Main {

    public static void main(String[] args) {

        boolean print_active = false;
        boolean append_to_file = true;
        String file_name = "logs/perf.log";


        String[] appArgs = MPI.Init(args);

        if (appArgs.length < 1) {
            System.exit(69);
        }
        if (appArgs.length > 1) {
            file_name = appArgs[1];
        }

        final int rank = MPI.COMM_WORLD.Rank();
        final int size = MPI.COMM_WORLD.Size();

        final int d = Integer.parseInt(appArgs[0]);
        final int p = (int) Math.sqrt(size);
        final int n = p * d;

        final float[][] local_a = new float[2][d * d];
        final float[][] local_b = new float[2][d * d];
        final float[] local_c = new float[d * d];

        final int rank_y = rank / p;
        final int rank_x = rank - rank_y * p;

        if (p * p != size) {
            System.exit(69);
        }

        final float[] global_a;
        final float[] global_b;
        final float[] global_c;
        float[] global_c_serial_result = new float[0];

        if (rank == MPI.HOST) {
            global_a = getGridMat(p, d);
            global_b = getGridMat(p, d);
            global_c = getGridMat(p, d);
        } else {
            global_a = new float[p * p * d * d];
            global_b = new float[p * p * d * d];
            global_c = new float[p * p * d * d];
        }

        // SERIAL TEST
        long time_before_serial = 0;
        long time_after_serial = 0;
        if (rank == MPI.HOST) {
            float[] global_a_s = convertMat(global_a, p, d, true);
            float[] global_b_s = convertMat(global_a, p, d, true);
            float[] global_c_s = convertMat(global_a, p, d, true);

            time_before_serial = System.nanoTime();
            global_c_serial_result = serialMatMul(global_a_s, global_b_s, global_c_s);
            time_after_serial = System.nanoTime();
        }
        // SERIAL TEST END

        MPI.COMM_WORLD.Barrier();
        if (rank == MPI.HOST && print_active) {
            System.out.println("global_a=");
            printMatrix(global_a, d, p);
            System.out.println(".");
        }
        MPI.COMM_WORLD.Barrier();

        final long time_before_init = System.nanoTime();

        final int initial_local_out = 0;
        MPI.COMM_WORLD.Scatter(global_a, 0, d * d, MPI.FLOAT, local_a[initial_local_out], 0, d * d, MPI.FLOAT, MPI.HOST);
        MPI.COMM_WORLD.Scatter(global_b, 0, d * d, MPI.FLOAT, local_b[initial_local_out], 0, d * d, MPI.FLOAT, MPI.HOST);
        MPI.COMM_WORLD.Scatter(global_c, 0, d * d, MPI.FLOAT, local_c, 0, d * d, MPI.FLOAT, MPI.HOST);

        int iteration = 0;
        // STEP INIT
        {
            final int local_out = 0;
            final int local_in = 1;

            final int tag_a = 1;
            final int tag_b = 2;

            int send_to_rank_a = rank + (rank_x - rank_y >= 0 ? 0 : p) - rank_y;
            int send_to_rank_b = rank + (rank_y - rank_x >= 0 ? 0 : p * p) - rank_x * p;
            int receive_from_rank_a = rank - (rank_x + rank_y < p ? 0 : p) + rank_y;
            int receive_from_rank_b = rank - (rank_y + rank_x < p ? 0 : p * p) + rank_x * p;

            //System.out.println("Rank " + rank + " sends A" + Arrays.toString(local_a[local_out]) + " to rank " + send_to_rank_a);
            //System.out.println("Rank " + rank + " sends B" + Arrays.toString(local_b[local_out]) + " to rank " + send_to_rank_b);

            Request req_a = MPI.COMM_WORLD.Isend(local_a[local_out], 0, d * d, MPI.FLOAT, send_to_rank_a, tag_a);
            Request req_b = MPI.COMM_WORLD.Isend(local_b[local_out], 0, d * d, MPI.FLOAT, send_to_rank_b, tag_b);
            MPI.COMM_WORLD.Recv(local_a[local_in], 0, d * d, MPI.FLOAT, receive_from_rank_a, tag_a);
            MPI.COMM_WORLD.Recv(local_b[local_in], 0, d * d, MPI.FLOAT, receive_from_rank_b, tag_b);

            //System.out.println("Rank " + rank + " received A" + Arrays.toString(local_a[local_in]) + " from rank " + receive_from_rank_a);
            //System.out.println("Rank " + rank + " received B" + Arrays.toString(local_b[local_in]) + " from rank " + receive_from_rank_b);

            req_a.Wait();
            req_b.Wait();
        }

        iteration++;
        final long time_after_init = System.nanoTime();

        Request req_a = null;
        Request req_b = null;
        for (; iteration <= p; iteration++) {
            final int local_out = iteration % 2 == 1 ? 1 : 0;
            final int local_in = iteration % 2 == 0 ? 1 : 0;

            final int tag_a = iteration * 2 + 1;
            final int tag_b = iteration * 2 + 2;


            final int send_to_rank_a = rank + (rank_x - 1 >= 0 ? 0 : p) - 1;
            final int send_to_rank_b = rank + (rank_y - 1 >= 0 ? 0 : p * p) - p;
            final int receive_from_rank_a = rank - (rank_x + 1 < p ? 0 : p) + 1;
            final int receive_from_rank_b = rank - (rank_y + 1 < p ? 0 : p * p) + p;

            for (int y = 0; y < d; y++) {
                for (int x = 0; x < d; x++) {
                    float res = 0.f;
                    for (int xy = 0; xy < d; xy++) {
                        res += local_a[local_out][y * d + xy] * local_b[local_out][xy * d + x];
                    }
                    local_c[y * d + x] += res;
                }
            }
            if (iteration == p) {
                break; // last iteration
            }
            //System.out.println("Rank " + rank + " sends A" + Arrays.toString(local_a[local_out]) + " to rank " + send_to_rank_a);
            //System.out.println("Rank " + rank + " sends B" + Arrays.toString(local_b[local_out]) + " to rank " + send_to_rank_b);
            if (req_a != null) req_a.Wait();
            if (req_a != null) req_b.Wait();

            req_a = MPI.COMM_WORLD.Isend(local_a[local_out], 0, d * d, MPI.FLOAT, send_to_rank_a, tag_a);
            req_b = MPI.COMM_WORLD.Isend(local_b[local_out], 0, d * d, MPI.FLOAT, send_to_rank_b, tag_b);
            MPI.COMM_WORLD.Recv(local_a[local_in], 0, d * d, MPI.FLOAT, receive_from_rank_a, tag_a);
            MPI.COMM_WORLD.Recv(local_b[local_in], 0, d * d, MPI.FLOAT, receive_from_rank_b, tag_b);

            //System.out.println("Rank " + rank + " received A" + Arrays.toString(local_a[local_in]) + " from rank " + receive_from_rank_a);
            //System.out.println("Rank " + rank + " received B" + Arrays.toString(local_b[local_in]) + " from rank " + receive_from_rank_b);
        }


        MPI.COMM_WORLD.Gather(local_c, 0, d * d, MPI.FLOAT, global_c, 0, d * d, MPI.FLOAT, MPI.HOST);

        final long time_final = System.nanoTime();

        if (rank == MPI.HOST) {
            if (print_active) {
                System.out.println("after iteration " + iteration + ": global_c=");
                printMatrix(global_c, d, p);
                System.out.println(" ");
            }
            double iteration_time = (double) (time_final - time_after_init) / 1e6;
            double total_time = (double) (time_final - time_before_init) / 1e6;
            double total_time_serial = (double) (time_after_serial - time_before_serial) / 1e6;
            System.out.printf("completed in %f ms (total parallel), %f ms (iteration only), %f ms (serial) ", total_time, iteration_time, total_time_serial);

            boolean good = verify(global_c_serial_result, convertMat(global_c, p, d, true), 1e-45f);
            System.out.print(good ? "ok " : "NICHT ok");

            if (append_to_file && good) {
                FileHelper.appendToFile(file_name, String.format("%d %d %d %f %f %f \n", n, p, d, total_time, iteration_time, total_time_serial));
            }
        }
        MPI.Finalize();
    }

    public static float[] getGridMat(int p, int d) {
        float[] mat = new float[p * p * d * d];
        for (int j = 0; j < p; j++) {
            for (int y = 0; y < d; y++) {
                for (int i = 0; i < p; i++) {
                    int offset = j * p * d * d + i * d * d;
                    for (int x = 0; x < d; x++) {
                        mat[offset + y * d + x] = j * p + i;
                    }
                }
            }
        }
        return mat;
    }

    public static float[] serialMatMul(float[] matA, float[] matB, float[] matC) {
        int n = (int) Math.sqrt(matA.length);
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                float res = 0;
                for (int xy = 0; xy < n; xy++) {
                    res += matA[y * n + xy] * matB[xy * n + x];
                }
                matC[y * n + x] += res;
            }
        }
        return matC;
    }

    public static float[] convertMat(final float[] mat, final int p, final int d, boolean has_grid) {
        final int n = p * d;
        float[] res = new float[n * n];
        if (has_grid) {
            int new_x = 0;
            int new_y = 0;
            for (int j = 0; j < p; j++) {
                for (int y = 0; y < d; y++) {
                    for (int i = 0; i < p; i++) {
                        int offset = j * p * d * d + i * d * d;
                        for (int x = 0; x < d; x++) {
                            res[new_y * n + new_x] = mat[offset + y * d + x];
                            new_x++;
                        }
                    }
                    new_x = 0;
                    new_y++;
                }
            }
        } else {
            System.err.println("not impl");
        }
        return res;
    }


    public static boolean verify(float[] vec_a, float[] vec_b, float diff) {
        boolean good = vec_a.length == vec_b.length;
        int count = 0;
        for (int i = 0; i < vec_a.length; i++) {
            if (Math.abs(vec_a[i] - vec_b[i]) > diff) {
                good = false;
                System.out.println("diff at i=" + i + " of " + (vec_a[i] - vec_b[i]));
                count++;
                if (count > 5) break;
            }
        }
        return good;
    }

    public static void printMatrix(final float[] mat, final int d, final int p) {
        for (int j = 0; j < p; j++) {
            for (int y = 0; y < d; y++) {
                for (int i = 0; i < p; i++) {
                    System.out.print("| ");
                    int offset = j * p * d * d + i * d * d;
                    for (int x = 0; x < d; x++) {
                        System.out.format("%2.1f ", mat[offset + y * d + x]);
                    }
                }
                System.out.print(" \n");
            }
            for (int t = 0; t < d * p; t++) {
                System.out.print(" --- ");
            }
            System.out.print(" \n");
        }
    }

}