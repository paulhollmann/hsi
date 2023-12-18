import java.util.Random;

public class Main {

    public static void main(String[] args)
    {
        var n = 6;
        // Verprobung?: lokale Blockgröße 2
        var p = 3;

        assert(n % p == 0);
        //Noch bessere Implementation der Verblockung

        var d = n/p;

        var A = new int[p*p][d*d];
        var B = new int[p*p][d*d];
        var C = new int[p*p][d*d];

        Random r = new Random(187);

        // Generator für Matrixbelegung
        for(int i = 0; i < p*p; i++)
        {
            for(int j = 0; j < d*d; j++)
            {
                A[i][j] = i+1;// r.nextInt(100);
                B[i][j] = i+1;//r.nextInt(100);
                //C[i][j] = 0;
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
    }

    public static int[][] getCannonIteration(int [][] matA, int[][] matB, int[][] matC){
        assert matA.length == matB.length && matA.length == matC.length;
        int p = (int) Math.sqrt(matA.length);
        int d = (int) Math.sqrt(matA[0].length);
        var n = p*d;

        // Initialisierung mittels steigernder zyklischer Vertauschung
        for (int i = 0; i < p; i++) { //block_rows (for A)
            for (int j = 0; j < i; j++) { // amount of commutations
                var firstAColumnElem = matA[i * p];
                var firstBRowElem = matB[i];
                for(int k = 0; k < p - 1; k++) { //block_column elements (for A)
                    matA[i * p + k] = matA[i * p + k + 1];
                    matB[k * p + i] = matB[(k+1) * p + i];
                }
                matA[i * p + p-1] = firstAColumnElem;
                matB[(p-1) * p + i] = firstBRowElem;
            }
        }

        System.out.println("A :");
        printMatrix(matA, n, p);
        System.out.println("B :");
        printMatrix(matB, n, p);

        // Cannon Iteration
        for(int i = 0; i < p; i++)
        {
            // Multiply Submatrix Axy*Bxy
            for(int y = 0; y < p; y++)
            {
                for(int x = 0; x < p; x++)
                {
                    // matmul aka matC[y * p + x][0] += matA[y * p + x][0] * matB[y * p + x][0];
                    for(int yy = 0; yy < d; yy++)
                    {
                        for(int xx = 0; xx < d; xx++)
                        {
                            var res = 0;
                            for(int xy = 0; xy < d; xy++)
                            {
                                res += matA[y * p + x][yy * d + xy] * matB[y * p + x][xy * d + xx];
                            }
                            matC[y * p + x][yy * d + xx] += res;
                        }
                    }
                }
            }

            // einfache zyklische Vertauschung
            for(int j = 0; j < p; j++)
            {
                var firstAColumnElem = matA[j * p];
                var firstBRowElem = matB[j];
                for(int k = 0; k < p - 1; k++)
                {
                    matA[j * p + k] = matA[j * p + k + 1];
                    matB[k * p + j] = matB[(k+1) * p + j];
                }
                matA[j * p + p-1] = firstAColumnElem;
                matB[(p-1) * p + j] = firstBRowElem;
            }
        }

        return matC;
    }

    public static void printMatrix(int[][] mat, int n, int p){
        var d = n/p;
        for(int y = 0; y < n; y++)
        {
            for(int x = 0; x < n; x++)
            {
                var x_block = x / d;
                var y_block = y / d;
                var x_local = x - x_block * d;
                var y_local = y - y_block * d;
                System.out.format("%02d  ", mat[y_block * p + x_block][y_local * d + x_local]);
            }
            System.out.println();
        }
    }

}
