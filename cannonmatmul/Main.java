
public class Main {

    public static void main(String[] args)
    {
        var n = 3;
        // Verprobung?: lokale Blockgröße 2
        //Noch bessere implementation der Verblockung

        var A = new float[n*n];
        var B = new float[n*n];
        var C = new float[n*n];

        // Generator für Matrixbelegung
        for(int i = 0; i < n*n; i++)
        {
            A[i] = i+1;
            B[i] = i+1;
            C[i] = 0;
        }
        System.out.println("A :");
        printMatrix(A, n);
        System.out.println("B :");
        printMatrix(B, n);
        System.out.println("C :");
        printMatrix(C, n);

        var D = getCannonIteration(A, B, C);
        System.out.println("C = AB * C :");
        printMatrix(D, n);
    }

    public static float[] getCannonIteration(float [] matA, float[] matB, float[] matC){
        assert matA.length == matB.length && matA.length == matC.length;
        int n = (int) Math.sqrt(matA.length);

        // Initialisierung mittels steigernder zyklischer Vertauschung
        for (int i = 0; i < n; i++) { //rows (for A)
            for (int j = 0; j < i; j++) { // amount of commutations
                var firstAColumnElem = matA[i * n];
                var firstBRowElem = matB[i];
                for(int k = 0; k < n - 1; k++) { //column elements (for A)
                    matA[i * n + k] = matA[i * n + k + 1];
                    matB[k * n + i] = matB[(k+1) * n + i];
                }
                matA[i * n + n-1] = firstAColumnElem;
                matB[(n-1) * n + i] = firstBRowElem;
            }
        }

        // Cannon Iteration
        for(int i = 0; i < n; i++)
        {
            // Multiply Submatrix Axy*Bxy
            for(int x = 0; x < n; x++)
            {
                for(int y = 0; y < n; y++)
                {
                    matC[y * n + x] += matA[y * n + x] * matB[y * n + x];
                }
            }

            // einfache zyklische Vertauschung
            for(int j = 0; j < n; j++)
            {
                var firstAColumnElem = matA[j * n];
                var firstBRowElem = matB[j];
                for(int k = 0; k < n - 1; k++)
                {
                    matA[j * n + k] = matA[j * n + k + 1];
                    matB[k * n + j] = matB[(k+1) * n + j];
                }
                matA[j * n + n-1] = firstAColumnElem;
                matB[(n-1) * n + j] = firstBRowElem;
            }
        }

        return matC;
    }

    public static void printMatrix(float[] mat, int n){
        for(int y = 0; y < n; y++)
        {
            for(int x = 0; x < n; x++)
            {
                System.out.format("%02d  ", (int) mat[y * n + x]);
            }
            System.out.println();
        }
    }

}
