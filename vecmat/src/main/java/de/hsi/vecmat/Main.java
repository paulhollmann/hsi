package de.hsi.vecmat;

public class Main {

    public static void main(String[] args)
    {
        double[] mat = {1, 2, 3, 2, 3, 4, 1, 1, 1};
        double[] vec = {2, 3, 4};
        var result = getMatVecProd(mat, vec);

        System.out.println(result[0] + result[1] + result[2]);
    }

    /* Size double mat needs to be square double size(vec)
     */

    public static double[] getMatVecProd(double [] mat, double[] vec){
        assert Math.sqrt(mat.length) == vec.length;
        var result = new double[vec.length];
        for (int i = 0; i< vec.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                result[i]+= mat[i*vec.length + j] * vec[j];
            }
        }
        return result;
    }

    public static double[] getMatVecProd_(int m){
        var mat = new double[m];
        var vec = new double[m];
        var vec_res = new double[m];
        for (int i = 0; i< vec.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                vec_res[i]+= mat[i*vec.length + j] * vec[j];
            }
        }
        return vec_res;
    }

}
