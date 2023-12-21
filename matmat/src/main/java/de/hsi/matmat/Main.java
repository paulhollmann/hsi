package de.hsi.matmat;

import mpi.MPI;

import java.util.Arrays;
import java.util.Random;


public class Main {

    public static void main(String[] args) {
        String[] appArgs = MPI.Init(args);

        int rank = MPI.COMM_WORLD.Rank();
        int size = MPI.COMM_WORLD.Size();

        int d = Integer.parseInt(appArgs[0]);
        int p = (int) Math.sqrt(size);
        int n = p * d;

        float[][] A;
        float[][] B;
        float[][] C;

        float[] global_a = new float[p * p * d * d];
        float[] global_b = new float[p * p * d * d];
        float[] global_c = new float[p * p * d * d];

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

        float[] local_a = new float[d * d];
        float[] local_b = new float[d * d];
        float[] local_c = new float[d * d];

        int offset = rank * d * d;
        int rank_y = rank / p;
        int rank_x = rank - rank_y * p;


        MPI.COMM_WORLD.Scatter(global_a, 0, d * d, MPI.FLOAT, local_a, 0, d * d, MPI.FLOAT, MPI.HOST);
        //MPI.COMM_WORLD.Scatter(global_b, 0, d * d, MPI.FLOAT, local_b, offset, d * d, MPI.FLOAT, MPI.HOST);
        //MPI.COMM_WORLD.Scatter(global_c, 0, d * d, MPI.FLOAT, local_c, offset, d * d, MPI.FLOAT, MPI.HOST);

        // STEP INIT
        //int send_to_rank_a = rank_x - rank_y >= 0 ? rank - rank_y : rank - rank_y + p;
        //int send_to_rank_b = rank_y - rank_x >= 0 ? rank - rank_x * p : rank - rank_x * p + p * p;
        //MPI.COMM_WORLD.Send(local_a, 0, d * d, MPI.FLOAT, send_to_rank_a, rank);
        //MPI.COMM_WORLD.Send(local_b, 0, d * d, MPI.FLOAT, send_to_rank_b, rank);
        //MPI.COMM_WORLD.Recv(local_a, )


        /*for (int i = 1; i < p; i++) {

        }*/
        //int px = (p * p + rank_x - rank_y) % (p * p);
        //int qx = (p * p + rank_y - rank_x) % (p * p);

        MPI.COMM_WORLD.Gather(local_a, 0, d * d, MPI.FLOAT, global_c, 0, d * d, MPI.FLOAT, MPI.HOST);


        if (rank == MPI.HOST) {
            printMatrix(global_a);
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

    public static void printMatrix(float[] mat) {
        System.out.println(Arrays.toString(mat));
    }

}