package FEJDMath;

/**
 * Used to compute the data about the shape functions in order to compute the integrals
 */
public class Data {
    /**
     * Constant used to denote the triangular elements
     */
    public final static int TRIANGLE = 1;
    /**
     * Constant used to denote the quadrilateral elements
     */
    public final static int QUADRILATERAL = 2;
    /**
     * Constant used to denote the linear functions
     */
    public final static int LINEAR = 1;
    /**
     * Constant used to denote the quadratic functions
     */
    public final static int QUADRATIC = 2;
    /**
     * Constants used to denote the bc type
     */
    public final static int DIRICHLET = 1;
    public final static int NEUMANN = 2;
    public final static int ROBIN = 3;

    /**
     * Constant used to know the number of coefficients used for f
     */
    public final static int NBRCOEFF = 12;

    /**
     * Array containing the ansatz functions for the 2D problem
     */
    private static ShapeFunction[] fctArray;

	/*	Valeurs des fonctions de forme et de leur gradient aux pts de quadrature
		dataArray[i][j][0] = valeur de phi[i] en xi[j]
		dataArray[i][j][1] = valeur de dphi[i]/dxi[1] en xi[j]
		dataArray[i][j][2] = valeur de dphi[i]/dxi[2] en xi[j]
		dataArray[no de la fct][pt de quadrature][deriv�e (0 = pas de deriv�e)] */

    /**
     * Values of the shape functions and of their gradient at the gauss points
     * dataArray[i][j][0] = value of phi[i] at xi[j]
     * dataArray[i][j][1] = value of dphi[i]/dxi[1] at xi[j]
     * dataArray[i][j][2] = value of dphi[i]/dxi[2] at xi[j]
     */
    private static double[][][] dataArray;
    /* Coordonn�es des pts de quadrature suivi de leur poids, tjs du type double[n][3] avec n nombre de pts de quadrature */
    /**
     * Coordinated of the gauss points followed by their weight, always of the type double[n][3] with n the number of gauss points
     */
    private static double[][] quadratPtsArray;

    /**
     * Parser which contains the f function (right hand side)
     */
    private static FctParser[] function;

    // 1D for boundary conditions
    /**
     * In tabFct1d, the first 2 functions correspond to the 2 end points (then the functions corresponding to the others points, in the middle, are entered)
     */
    private static ShapeFunction1D[] fct1DArray;

	/*	Valeurs des fonctions de forme aux pts de quadrature
		tabData[i][j] = valeur de phi[i] en xi[j]
		tabData[no de la fct][pt de quadrature] */
    /**
     * Values of the shape functions at the gauss points
     * tabData[i][j] = value of phi[i] at xi[j]
     */
    private static double[][] data1DArray;
    /*	Coordonn�es des pts de quadrature suivi de leur poids, tjs du type double[n][2] avec n nombre de pts de quadrature */
    /**
     * Coordinated of the gauss points followed by their weight, always of the type double[n][2] with n the number of gauss points
     */
    private static double[][] quadratPts1DArray;


    /**
     * Stores the values of the shape functions 1D and 2D at the gauss points in order to compute the integrals
     */
    public static void computeDataArray(int elemType, int fctType) {
        int nbrfct = 0;
        int nbrpts = 0; // Number of Gauss points used for the computation of the integrals
        // in order to have an exact computation for functions of degree >= 2*(degree of the shape functions - 1) (1 point for linear, 3 for quadratic, etc.)

        int nbrfct1D = 0;
        int nbrpts1D = 0;
        // in order to have an exact computation for functions of degree >= 2* degree of the shape functions (2 points for linear, 3 for quadratic, etc.)

        if (fctType == LINEAR) {

            switch (elemType) {
                case TRIANGLE:
                    nbrpts = 1;
                    nbrfct = 3;

                    fctArray = new LinearFct[nbrfct];
                    fctArray[0] = new LinearFct(1, -1, -1);
                    fctArray[1] = new LinearFct(0, 1, 0);
                    fctArray[2] = new LinearFct(0, 0, 1);

                    dataArray = new double[nbrfct][nbrpts][3];
                    quadratPtsArray = new double[nbrpts][3];
                    quadratPtsArray[0][0] = 1. / 3.;
                    quadratPtsArray[0][1] = 1. / 3.;
                    quadratPtsArray[0][2] = .5;

                    break;

                case QUADRILATERAL:
                    nbrpts = 4;
                    nbrfct = 4;

                    fctArray = new BilinearFct[nbrfct];
                    fctArray[0] = new BilinearFct(1, -1, -1, 1);
                    fctArray[1] = new BilinearFct(0, 0, 1, -1);
                    fctArray[2] = new BilinearFct(0, 0, 0, 1);
                    fctArray[3] = new BilinearFct(0, 1, 0, -1);


                    dataArray = new double[nbrfct][nbrpts][3];
                    quadratPtsArray = new double[nbrpts][3];
                    quadratPtsArray[0][0] = (3. - Math.sqrt(3)) / 6.;
                    quadratPtsArray[0][1] = (3. - Math.sqrt(3)) / 6.;
                    quadratPtsArray[0][2] = .25;
                    quadratPtsArray[1][0] = (3. - Math.sqrt(3)) / 6.;
                    quadratPtsArray[1][1] = (3. + Math.sqrt(3)) / 6.;
                    quadratPtsArray[1][2] = .25;
                    quadratPtsArray[2][0] = (3. + Math.sqrt(3)) / 6.;
                    quadratPtsArray[2][1] = (3. - Math.sqrt(3)) / 6.;
                    quadratPtsArray[2][2] = .25;
                    quadratPtsArray[3][0] = (3. + Math.sqrt(3)) / 6.;
                    quadratPtsArray[3][1] = (3. + Math.sqrt(3)) / 6.;
                    quadratPtsArray[3][2] = .25;

                    break;
            }
            // 1D
            nbrfct1D = 2;
            nbrpts1D = 2;
            fct1DArray = new LinearFct1D[nbrfct1D];
            fct1DArray[0] = new LinearFct1D(1, -1);
            fct1DArray[1] = new LinearFct1D(0, 1);

            data1DArray = new double[nbrfct1D][nbrpts1D];
            quadratPts1DArray = new double[nbrpts1D][2];
            quadratPts1DArray[0][0] = (3. - Math.sqrt(3)) / 6.;
            quadratPts1DArray[0][1] = .5;
            quadratPts1DArray[1][0] = (3. + Math.sqrt(3)) / 6.;
            quadratPts1DArray[1][1] = .5;

        } else if (fctType == QUADRATIC) {

            switch (elemType) {


                case TRIANGLE:
                    nbrpts = 3;
                    nbrfct = 6;

                    fctArray = new QuadraticFct[nbrfct];
                    fctArray[0] = new QuadraticFct(1, -3, -3, 2, 2, 4);
                    fctArray[1] = new QuadraticFct(0, -1, 0, 2, 0, 0);
                    fctArray[2] = new QuadraticFct(0, 0, -1, 0, 2, 0);
                    fctArray[3] = new QuadraticFct(0, 4, 0, -4, 0, -4);
                    fctArray[4] = new QuadraticFct(0, 0, 0, 0, 0, 4);
                    fctArray[5] = new QuadraticFct(0, 0, 4, 0, -4, -4);


                    dataArray = new double[nbrfct][nbrpts][3];
                    quadratPtsArray = new double[nbrpts][3];

                    quadratPtsArray[0][0] = 0.5;
                    quadratPtsArray[0][1] = 0.;
                    quadratPtsArray[0][2] = 1. / 6.;

                    quadratPtsArray[1][0] = 0.5;
                    quadratPtsArray[1][1] = 0.5;
                    quadratPtsArray[1][2] = 1. / 6.;

                    quadratPtsArray[2][0] = 0.;
                    quadratPtsArray[2][1] = 0.5;
                    quadratPtsArray[2][2] = 1. / 6.;
                    break;

                case QUADRILATERAL:
                    nbrpts = 9;
                    nbrfct = 9;

                    fctArray = new BiquadraticFct[nbrfct];
                    fctArray[0] = new BiquadraticFct(1, -3, -3, 2, 2, 9, -6, -6, 4);
                    fctArray[1] = new BiquadraticFct(0, 0, -1, 0, 2, 3, -2, -6, 4);
                    fctArray[2] = new BiquadraticFct(0, 0, 0, 0, 0, 1, -2, -2, 4);
                    fctArray[3] = new BiquadraticFct(0, -1, 0, 2, 0, 3, -6, -2, 4);
                    fctArray[4] = new BiquadraticFct(0, 0, 4, 0, -4, -12, 8, 12, -8);
                    fctArray[5] = new BiquadraticFct(0, 0, 0, 0, 0, -4, 4, 8, -8);
                    fctArray[6] = new BiquadraticFct(0, 0, 0, 0, 0, -4, 8, 4, -8);
                    fctArray[7] = new BiquadraticFct(0, 4, 0, -4, 0, -12, 12, 8, -8);
                    fctArray[8] = new BiquadraticFct(0, 0, 0, 0, 0, 16, -16, -16, 16);


                    dataArray = new double[nbrfct][nbrpts][3];
                    quadratPtsArray = new double[nbrpts][3];

                    quadratPtsArray[0][0] = (5. - Math.sqrt(15)) / 10.;
                    quadratPtsArray[0][1] = (5. - Math.sqrt(15)) / 10.;
                    quadratPtsArray[0][2] = 25. / 324.;

                    quadratPtsArray[1][0] = (5. - Math.sqrt(15)) / 10.;
                    quadratPtsArray[1][1] = 0.5;
                    quadratPtsArray[1][2] = 10. / 81.;

                    quadratPtsArray[2][0] = (5. - Math.sqrt(15)) / 10.;
                    quadratPtsArray[2][1] = (5. + Math.sqrt(15)) / 10.;
                    quadratPtsArray[2][2] = 25. / 324.;

                    quadratPtsArray[3][0] = .5;
                    quadratPtsArray[3][1] = (5. - Math.sqrt(15)) / 10.;
                    quadratPtsArray[3][2] = 10. / 81.;

                    quadratPtsArray[4][0] = 0.5;
                    quadratPtsArray[4][1] = 0.5;
                    quadratPtsArray[4][2] = 16. / 81.;

                    quadratPtsArray[5][0] = 0.5;
                    quadratPtsArray[5][1] = (5. + Math.sqrt(15)) / 10.;
                    quadratPtsArray[5][2] = 10. / 81.;

                    quadratPtsArray[6][0] = (5. + Math.sqrt(15)) / 10.;
                    quadratPtsArray[6][1] = (5. - Math.sqrt(15)) / 10.;
                    quadratPtsArray[6][2] = 25. / 324.;

                    quadratPtsArray[7][0] = (5. + Math.sqrt(15)) / 10.;
                    quadratPtsArray[7][1] = 0.5;
                    quadratPtsArray[7][2] = 10. / 81.;

                    quadratPtsArray[8][0] = (5. + Math.sqrt(15)) / 10.;
                    quadratPtsArray[8][1] = (5. + Math.sqrt(15)) / 10.;
                    quadratPtsArray[8][2] = 25. / 324.;


                    break;
            }
            // 1D
            nbrfct1D = 3;
            nbrpts1D = 3;
            fct1DArray = new QuadraticFct1D[nbrfct1D];
            fct1DArray[0] = new QuadraticFct1D(1, -3, 2);
            fct1DArray[1] = new QuadraticFct1D(0, -1, 2);
            fct1DArray[2] = new QuadraticFct1D(0, 4, -4);

            data1DArray = new double[nbrfct1D][nbrpts1D];
            quadratPts1DArray = new double[nbrpts1D][2];
            quadratPts1DArray[0][0] = (5. - Math.sqrt(15)) / 10.;
            quadratPts1DArray[0][1] = 5. / 18.;
            quadratPts1DArray[1][0] = 0.5;
            quadratPts1DArray[1][1] = 8. / 18.;
            quadratPts1DArray[2][0] = (5. + Math.sqrt(15)) / 10.;
            quadratPts1DArray[2][1] = 5. / 18.;

        }

        for (int i = 0; i < nbrfct; i++) {
            for (int j = 0; j < nbrpts; j++) {
                dataArray[i][j][0] = fctArray[i].computeFctAt(quadratPtsArray[j][0], quadratPtsArray[j][1]);
                dataArray[i][j][1] = fctArray[i].computeGradAt(quadratPtsArray[j][0], quadratPtsArray[j][1]).getNum(1);
                dataArray[i][j][2] = fctArray[i].computeGradAt(quadratPtsArray[j][0], quadratPtsArray[j][1]).getNum(2);
            }

        }

        for (int i = 0; i < nbrfct1D; i++)
            for (int j = 0; j < nbrpts1D; j++)
                data1DArray[i][j] = fct1DArray[i].computeFctAt(quadratPts1DArray[j][0]);

    }

    /**
     * Loads the function represented by the string s
     */
    public static void setFunction(int i, String s) {
        function[i] = new FctParser(s);
    }

    /**
     * Returns the string representing the ith function
     */
    public static String getFunction(int i) {
        return function[i].getFunction();
    }

    /**
     * Returns the value of the ith f function at (v.x,v.y)
     */
    public static double computeFAt(int i, Vector2 v) {
        double x = v.getNum(1);
        double y = v.getNum(2);
        return function[i].calcFctAt(x, y);
    }

    /**
     * Returns the value of the ith 2D shape function differentiated k times at the jth point
     */
    public static double getData(int i, int j, int k) {
        return dataArray[i][j][k];
    }

    /**
     * Returns the weight of the point in order to perform the 2D computation
     */
    public static double getWeight(int i) {
        return quadratPtsArray[i][2];
    }

    /**
     * Returns the jth component of the ith point for 2D computation, j=0 returns the x coordinate, j=1 returns the y coorfinate, j=2 returns the weight of the point
     */
    public static double getQuadratPts(int i, int j) {
        return quadratPtsArray[i][j];
    }

    /**
     * Returns the number of points used for 2D functions
     */
    public static int getNbOfQuadratPts() {
        if (quadratPtsArray == null)
            return 0;
        return quadratPtsArray.length;
    }

    /**
     * Returns the number of 2D functions
     */
    public static int getNbOfFct() {
        if (fctArray == null)
            return 0;
        return fctArray.length;
    }

    /**
     * Sets the number of functions that the array will contain
     */
    public static void setNbOfFct(int nbr) {
        function = new FctParser[nbr];
    }

    /**
     * Returns the value of the ith 1D shape function at the jth point
     */
    public static double getData1D(int i, int j) {
        return data1DArray[i][j];
    }

    /**
     * Returns the weight of the point in order to perform the 1D computation
     */
    public static double getWeight1D(int i) {
        return quadratPts1DArray[i][1];
    }

    /**
     * Returns the x coordinate of the ith point for 1D computation
     */
    public static double getQuadratPts1D(int i) {
        return quadratPts1DArray[i][0];
    }

    /**
     * Returns the number of points used for 1D functions
     */
    public static int getNbOfQuadratPts1D() {
        if (quadratPts1DArray == null)
            return 0;
        return quadratPts1DArray.length;
    }

    /**
     * Returns the number of 1D functions
     */
    public static int getNbOfFct1D() {
        if (fct1DArray == null)
            return 0;
        return fct1DArray.length;
    }
}
