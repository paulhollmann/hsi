package de.hsi.matmat;

import mpi.MPI;
import mpi.Request;

import java.util.Random;


public class Main {

    public static void main(String[] args) {
        String[] appArgs = MPI.Init(args);

        final int rank = MPI.COMM_WORLD.Rank();
        final int size = MPI.COMM_WORLD.Size();

        final int d = Integer.parseInt(appArgs[0]);
        final int p = (int) Math.sqrt(size);
        final int n = p * d;

        if (p * p != size) {
            System.exit(69);
        }

        float[][] A;
        float[][] B;
        float[][] C;

        final float[] global_a = new float[p * p * d * d];
        final float[] global_b = new float[p * p * d * d];
        final float[] global_c = new float[p * p * d * d];

        if (rank == MPI.HOST) {
            A = new float[p * p][d * d]; // [[1,1,1,1], [2,2,2,2], [3,3,3,3], [4,4,4,4]] für p = 2 und d = 2
            B = new float[p * p][d * d];
            C = new float[p * p][d * d];

            Random r = new Random(187);

            // Generator für Matrixbelegung
            for (int i = 0; i < p * p; i++) {
                for (int j = 0; j < d * d; j++) {
                    A[i][j] = i + 1;// r.nextInt(100);
                    B[i][j] = i + 1;//r.nextInt(100);
                }
            }
            System.out.println("A :");
            printMatrix(A, n, p);
            System.out.println("B :");
            printMatrix(B, n, p);
            System.out.println("C :");
            printMatrix(C, n, p);

            var D = getCannonIteration(A, B, C);

            System.out.println("C = AB * C:");
            printMatrix(D, n, p);

            MPI.COMM_WORLD.Barrier();

            System.out.println("Usage of MPI, I am Host, Size is  " + MPI.COMM_WORLD.Size() + " ------------------");

            // Initialisierung mittels steigernder zyklischer Vertauschung
            for (int i = 0; i < p; i++) { //block_rows (for A)
                for (int j = 0; j < i; j++) { // amount of commutations
                    var firstAColumnElem = A[i * p];
                    var firstBRowElem = B[i];
                    for (int k = 0; k < p - 1; k++) { //block_column elements (for A)
                        A[i * p + k] = A[i * p + k + 1];
                        B[k * p + i] = B[(k + 1) * p + i];
                    }
                    A[i * p + p - 1] = firstAColumnElem;
                    B[(p - 1) * p + i] = firstBRowElem;
                }
            }


            for (int y = 0; y < n; y++) {
                for (int x = 0; x < n; x++) {
                    var x_block = x / d;
                    var y_block = y / d;
                    var x_local = x - x_block * d;
                    var y_local = y - y_block * d;
                    global_a[y * p * d + x] = A[y_block * p + x_block][y_local * d + x_local];
                    global_b[y * p * d + x] = B[y_block * p + x_block][y_local * d + x_local];
                    global_c[y * p * d + x] = C[y_block * p + x_block][y_local * d + x_local];
                }
                System.out.println();
            }

        } else {
            MPI.COMM_WORLD.Barrier();
            System.out.println("Hello World! I am number <" + (rank + 1) + "/" + size + ">\n");
        }
        MPI.COMM_WORLD.Barrier();

        printMatrix(global_a, d, p);

        MPI.COMM_WORLD.Barrier();


        float[][] local_a = new float[2][d * d];
        float[][] local_b = new float[2][d * d];
        float[] local_c = new float[d * d];

        final int rank_y = rank / p;
        final int rank_x = rank - rank_y * p;

        int iteration = 1;

        MPI.COMM_WORLD.Scatter(global_a, 0, d * d, MPI.FLOAT, local_a[1], 0, d * d, MPI.FLOAT, MPI.HOST);
        MPI.COMM_WORLD.Scatter(global_b, 0, d * d, MPI.FLOAT, local_b[1], 0, d * d, MPI.FLOAT, MPI.HOST);
        MPI.COMM_WORLD.Scatter(global_c, 0, d * d, MPI.FLOAT, local_c, 0, d * d, MPI.FLOAT, MPI.HOST);

        // STEP INIT
        //int send_to_rank_a = rank_x - rank_y >= 0 ? rank - rank_y : rank - rank_y + p;
        //int send_to_rank_b = rank_y - rank_x >= 0 ? rank - rank_x * p : rank - rank_x * p + p * p;
        //MPI.COMM_WORLD.Send(local_a, 0, d * d, MPI.FLOAT, send_to_rank_a, rank);
        //MPI.COMM_WORLD.Send(local_b, 0, d * d, MPI.FLOAT, send_to_rank_b, rank);
        //MPI.COMM_WORLD.Recv(local_a, )


        for (; iteration < p; iteration++) {
            final int local_out = iteration % 2 == 1 ? 1 : 0;
            final int local_in = iteration % 2 == 0 ? 1 : 0;

            final int tag_a = iteration * 2 + 1;
            final int tag_b = iteration * 2 + 2;


            final int send_to_rank_a = rank + (rank_x - 1 >= 0 ? 0 : p) - 1;
            final int send_to_rank_b = rank + (rank_y - 1 >= 0 ? 0 : p * p) - p;
            final int receive_from_rank_a = rank - (rank_x + 1 < p ? 0 : p) + 1;
            final int receive_from_rank_b = rank - (rank_y + 1 < p ? 0 : p * p) + p;

            Request req_a = MPI.COMM_WORLD.Isend(local_a[local_out], 0, d * d, MPI.FLOAT, send_to_rank_a, tag_a);
            //System.out.println("Rank " + rank + " sends A" + Arrays.toString(local_a[local_out]) + " to rank " + send_to_rank_a);

            Request req_b = MPI.COMM_WORLD.Isend(local_b[local_out], 0, d * d, MPI.FLOAT, send_to_rank_b, tag_b);
            //System.out.println("Rank " + rank + " sends B" + Arrays.toString(local_b[local_out]) + " to rank " + send_to_rank_b);


            MPI.COMM_WORLD.Recv(local_a[local_in], 0, d * d, MPI.FLOAT, receive_from_rank_a, tag_a);
            //System.out.println("Rank " + rank + " receives A" + Arrays.toString(local_a[local_in]) + " from rank " + receive_from_rank_a);

            MPI.COMM_WORLD.Recv(local_b[local_in], 0, d * d, MPI.FLOAT, receive_from_rank_b, tag_b);
            //System.out.println("Rank " + rank + " receives B" + Arrays.toString(local_b[local_in]) + " from rank " + receive_from_rank_b);


            for (int y = 0; y < d; y++) {
                for (int x = 0; x < d; x++) {
                    float res = 0.f;
                    for (int xy = 0; xy < d; xy++) {
                        res += local_a[local_in][y * d + xy] * local_b[local_in][xy * d + x];
                    }
                    local_c[y * d + x] += res;
                }
            }


            req_a.Wait();
            req_b.Wait();

            // debug
            MPI.COMM_WORLD.Barrier();
            MPI.COMM_WORLD.Gather(local_c, 0, d * d, MPI.FLOAT, global_c, 0, d * d, MPI.FLOAT, MPI.HOST);
            if (rank == MPI.HOST) {
                printMatrix(global_c, d, p);
                System.out.println("Iteration ----------------------------------------------");
            }
            MPI.COMM_WORLD.Barrier();
        }


        MPI.COMM_WORLD.Gather(local_c, 0, d * d, MPI.FLOAT, global_c, 0, d * d, MPI.FLOAT, MPI.HOST);


        if (rank == MPI.HOST) {
            printMatrix(global_c, d, p);
        }

        //System.out.println("C = AB + C:");
        //printMatrix(E, n, p);
        MPI.Finalize();

    }

    public static float[] getMatMul(float[] matA, float[] matB, float[] matC) {
        int n = (int) Math.sqrt(matA.length);
        // matmul aka matC[y * p + x][0] += matA[y * p + x][0] * matB[y * p + x][0];
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

    public static float[][] getCannonIteration(float[][] matA, float[][] matB, float[][] matC) {
        assert matA.length == matB.length && matA.length == matC.length;
        int p = (int) Math.sqrt(matA.length);
        int d = (int) Math.sqrt(matA[0].length);
        int n = p * d;

        // Initialisierung mittels steigernder zyklischer Vertauschung
        for (int i = 0; i < p; i++) { //block_rows (for A)
            for (int j = 0; j < i; j++) { // amount of commutations
                var firstAColumnElem = matA[i * p];
                var firstBRowElem = matB[i];
                for (int k = 0; k < p - 1; k++) { //block_column elements (for A)
                    matA[i * p + k] = matA[i * p + k + 1];
                    matB[k * p + i] = matB[(k + 1) * p + i];
                }
                matA[i * p + p - 1] = firstAColumnElem;
                matB[(p - 1) * p + i] = firstBRowElem;
            }
        }

        System.out.println("A :");
        printMatrix(matA, n, p);
        System.out.println("B :");
        printMatrix(matB, n, p);

        // Cannon Iteration
        for (int i = 0; i < p; i++) {
            // Multiply Submatrix Axy*Bxy
            for (int y = 0; y < p; y++) {
                for (int x = 0; x < p; x++) {
                    // matmul aka matC[y * p + x][0] += matA[y * p + x][0] * matB[y * p + x][0];
                    for (int yy = 0; yy < d; yy++) {
                        for (int xx = 0; xx < d; xx++) {
                            float res = 0;
                            for (int xy = 0; xy < d; xy++) {
                                res += matA[y * p + x][yy * d + xy] * matB[y * p + x][xy * d + xx];
                            }
                            matC[y * p + x][yy * d + xx] += res;
                        }
                    }
                }
            }

            // einfache zyklische Vertauschung
            for (int j = 0; j < p; j++) {
                var firstAColumnElem = matA[j * p];
                var firstBRowElem = matB[j];
                for (int k = 0; k < p - 1; k++) {
                    matA[j * p + k] = matA[j * p + k + 1];
                    matB[k * p + j] = matB[(k + 1) * p + j];
                }
                matA[j * p + p - 1] = firstAColumnElem;
                matB[(p - 1) * p + j] = firstBRowElem;
            }
        }

        return matC;
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
        //System.out.println("Vectors of length " + vec_b.length + " match" + (good ? "":" not") + " to " + diff);
        return good;
    }

    public static void printMatrix(float[][] mat, int n, int p) {
        var d = n / p;
        for (int y = 0; y < n; y++) {
            for (int x = 0; x < n; x++) {
                var x_block = x / d;
                var y_block = y / d;
                var x_local = x - x_block * d;
                var y_local = y - y_block * d;
                System.out.format("%02f  ", mat[y_block * p + x_block][y_local * d + x_local]);
            }
            System.out.println();
        }
    }

    public static void printMatrix(final float[] mat, final int d, final int p) {
        for (int j = 0; j < p; j++) {
            for (int y = 0; y < d; y++) {
                for (int i = 0; i < p; i++) {
                    System.out.print("| ");
                    int offset = j * p * d * d + i * d;
                    for (int x = 0; x < d; x++) {
                        System.out.print(mat[offset + y * d + x] + " ");
                    }
                }
                System.out.print(" \n");
            }
            for (int t = 0; t < d * p; t++) {
                System.out.print(" ---- ");
            }
            System.out.print(" \n");
        }
    }

}