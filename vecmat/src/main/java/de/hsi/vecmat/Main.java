package de.hsi.vecmat;

import org.jocl.samples.JOCLDeviceQuery;

import java.util.Random;

public class Main {

    public static void main(String[] args)
    {
        double[] mat = {1, 2, 3, 2, 3, 4, 1, 1, 1};
        double[] vec = {2, 3, 4};
        var result = getMatVecProd_(10000);

        System.out.println(result[0]);
        System.out.println(result[1]);
        System.out.println(result[2]);

        JOCLDeviceQuery.main(args);
    }

    /* Size of double mat needs to be square double size(vec)
     */
    public static double[] getMatVecProd(double [] mat, double[] vec){
        assert Math.sqrt(mat.length) == vec.length;
        var result = new double[vec.length];
        for (int i = 0; i < vec.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                result[i]+= mat[i * vec.length + j] * vec[j];
            }
        }
        return result;
    }

    public static double[] getMatVecProd_(int m){
        Random r = new Random();
        var mat = new double[m*m];
        var vec = new double[m];
        for(int i = 0; i < m; i++)
        {
            vec[i] = 100* r.nextDouble();
            for(int j = 0; j < m; j++)
            {
                mat[i*m + j] = 100 * r.nextDouble();
            }
        }
        var vec_res = new double[m];
        long start = System.currentTimeMillis();
        for (int i = 0; i < vec.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                vec_res[i]+= mat[i * vec.length + j] * vec[j];
            }
        }
        long end = System.currentTimeMillis();
        System.out.printf("Matrix-Vektor-Produkt completed in %dms%n", end - start);
        return vec_res;
    }

}
