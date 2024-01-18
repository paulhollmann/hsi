package FEJDMath;

/**
 * Represents an edge of the mesh whose nodes have robin boundary conditions
 */
class Border3 extends MeshBorder { // Robin

    /**
     * The vector used to store the value of the boundary conditions
     */
    private double[] BCVector;
    /**
     * The matrix used to store the value of the boundary conditions
     */
    private double[][] BCMatrix;
    /**
     * The values of the boundary conditions
     */
    private double a, b;

    /**
     * @param e  the considered MeshBorder
     * @param aa the first value of the boundary condition (the value denoted alpha(x) in the book)
     * @param bb the second value of the boundary condition (the value denoted uA(x) in the book)
     */
    public Border3(MeshBorder e, double aa, double bb) {
        super(e);
        a = aa;
        b = bb;
    }

    /**
     * Returns the coefficient a of the boundary condition
     */
    public double geta() {
        return a;
    }

    /**
     * Sets the coefficient a to aa
     */
    public void seta(double aa) {
        a = aa;
    }

    /**
     * Returns the coefficient a of the boundary condition
     */
    public double getb() {
        return b;
    }

    /**
     * Sets the coefficient b to bb
     */
    public void setb(double bb) {
        b = bb;
    }

    public String toString() {
        return "Border3 : " + getGlobalNb() + ": " + getNode1().getGlobalNb() + " " + getNode2().getGlobalNb() + "; a= " + a + "; b= " + b;
    }

    public String toString2() {
        return "" + getGlobalNb() + "  a= " + a + "  b= " + b + "\n";
    }

    /**
     * Computes the boundary conditions and modifies the vector and the matrix to take boundary conditions of the first type into account, unlike the mehod described in the book, the modifications due to boundary confitions of the first type are computes at the element level and not at the end
     */
    public void calcBCondition(Domain domain) {
        // BCVector
        BCVector = new double[Data.getNbOfFct1D()];

        double n1x = getNode1().getx();
        double n1y = getNode1().gety();
        double n2x = getNode2().getx();
        double n2y = getNode2().gety();
        double sqrtValue = Math.sqrt((n2x - n1x) * (n2x - n1x) + (n2y - n1y) * (n2y - n1y));

        for (int k = 0; k < Data.getNbOfFct1D(); k++) {
            double value = 0;

            for (int i = 0; i < Data.getNbOfQuadratPts1D(); i++)
                value += Data.getWeight1D(i) * Data.getData1D(k, i);

            BCVector[k] = value * a * b * sqrtValue;
        }

        // BCMatrix
        BCMatrix = new double[Data.getNbOfFct1D()][Data.getNbOfFct1D()];

        for (int k = 0; k < Data.getNbOfFct1D(); k++) {

            for (int j = 0; j < Data.getNbOfFct1D(); j++) {
                double value = 0;

                for (int i = 0; i < Data.getNbOfQuadratPts1D(); i++)
                    value += Data.getWeight1D(i) * Data.getData1D(k, i) * Data.getData1D(j, i);

                BCMatrix[k][j] = value * a * sqrtValue;
            }
        }

        // Boundary conditions 1st type, modifies BCVector
        for (int i = 0; i < Data.getNbOfFct1D(); i++) {
            if (!getMeshPoint(i).isDirichletNode()) {
                double somme = 0;
                for (int k = 0; k < Data.getNbOfFct1D(); k++)
                    if (getMeshPoint(k).isDirichletNode())
                        somme += getMeshPoint(k).getDirichletValue() * BCMatrix[i][k];
                //System.out.println("modif T3 " + somme);
                BCVector[i] -= somme;
            }
        }

        // Boundary conditions 1st type
        for (int i = 0; i < Data.getNbOfFct1D(); i++) {
            if (getMeshPoint(i).isDirichletNode()) {
                for (int j = 0; j < Data.getNbOfFct1D(); j++) {
                    BCMatrix[j][i] = 0;
                    BCMatrix[i][j] = 0;
                }
                BCMatrix[i][i] = 1;
            }
        }

        // Assembling
        int gNbj, gNbk;
        for (int j = 0; j < Data.getNbOfFct1D(); j++) {
            MeshPoint mp = getMeshPoint(j);

            gNbj = mp.getGlobalNb();

            if (domain.getNodeList().contains(mp))
                domain.addLVector(gNbj - 1, BCVector[j]);

            for (int k = 0; k < Data.getNbOfFct1D(); k++) {
                MeshPoint kp = getMeshPoint(k);
                if (domain.getNodeList().contains(kp)) {
                    gNbk = kp.getGlobalNb();
                    if (gNbk >= gNbj) {
                        domain.getStiffnessMatrix().addSMatrix(gNbj, gNbk, BCMatrix[j][k]);
                    }
                }
            }
        }


        //System.out.println("BC T3");
        //System.out.println(BCVector[0]);
        //System.out.println(BCVector[1] + "\n");

        //System.out.println(BCMatrix[0][0] + " " + BCMatrix[0][1]);
        //System.out.println(BCMatrix[1][0] + " " + BCMatrix[1][1] + "\n");
    }
}
