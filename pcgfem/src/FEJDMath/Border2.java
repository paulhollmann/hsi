package FEJDMath;

/**
 * Represents a border of the mesh whose nodes have neumann boundary conditions
 */
public class Border2 extends MeshBorder {

    /**
     * The vector used to store the value of the boundary conditions
     */
    private double[] BCVector;
    /**
     * The value of the boundary condition
     */
    private double a;

    /**
     * @param e  the considered MeshBorder
     * @param aa the value of the boundary condition
     */
    public Border2(MeshBorder e, double aa) {
        super(e);
        a = aa;
    }

    /**
     * Returns a decription of the border
     */
    public String toString() {
        return "Border2 : " + getGlobalNb() + ": " + getNode1().getGlobalNb() + " " + getNode2().getGlobalNb() + "; a= " + a;
    }

    /**
     * Returns a decription of the border containing only its global number and the value of a
     */
    public String toString2() {
        return "" + getGlobalNb() + " a= " + a + "\n";
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
     * Computes the boundary conditions and adds them to the last vector
     */
    public void calcBCondition(Domain domain) {

// Assembling :
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

            BCVector[k] = value * a * sqrtValue;
        }

        int gNbj;
        for (int j = 0; j < Data.getNbOfFct1D(); j++) {
            MeshPoint mp = getMeshPoint(j);
            if (domain.getNodeList().contains(mp)) {
                gNbj = getMeshPoint(j).getGlobalNb();
                domain.addLVector(gNbj - 1, BCVector[j]);
            }
        }

        //System.out.println("BC T2");
        //System.out.println(BCVector[0]);
        //System.out.println(BCVector[1] + "\n");
    }

}
