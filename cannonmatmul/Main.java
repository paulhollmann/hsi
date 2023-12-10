
public class Main {

    public static void main(String[] args)
    {
        var n = 4;
        // Verprobung?: lokale Blockgröße 2

        var A = new float[n*n];
        var B = new float[n*n];
        var C = new float[n*n];

        // Generator für Matrixbelegung
        for(int i = 0; i < A.length; i++)
        {
            A[i] = i+1;
            B[i] = i+1;
            C[i] = i+1;
        }

        var D = getCannonIteration(A, B, C);
        for (float d : D) System.out.println(d);

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


        return matB;
    }

}
