import java.util.Random;
import mpi.*;

public class MPI {

    public static void main(String[] args)
    {
        String appArgs[] = MPI.Init(args);
        var d = Integer.parseInt(appArgs[0]);
        int rank = MPI.COMM_WORLD.Rank();
        int p = MPI.COMM_WORLD.Size();

        if (rank == 0)
        {
            System.out.println("Starting MPI Function *I am the root process.*");

            var n = d * p;

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
            //printMatrix(A, n, p);
            System.out.println("B :");
            //printMatrix(B, n, p);
            System.out.println("C :");
            //printMatrix(C, n, p);

            //var D = getCannonIteration(A, B, C);
            System.out.println("C = AB * C:");
            //printMatrix(D, n, p);
        }
        else
        {
            System.out.println("Hello World! I am number <"+ (rank + 1) + "/" + size + ">\n");
        }

        MPI.Finalize();
    }

}