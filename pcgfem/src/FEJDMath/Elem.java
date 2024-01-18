package FEJDMath;

/**
 * Represents an element of the mesh, abstract class used to create the elements we use
 */
public abstract class Elem implements Cloneable {

    /**
     * The global number of the element
     */
    private int globalNb;
    /**
     * Contains the MeshPoints. All nodes should be placed before the non-nodes meshpoints !
     */
    private MeshPoint[] meshPointArray;
    /**
     * The elementary stiffness matrix
     */
    private double[][] elementMatrix; // protected to be able to use it in subclasses
    /**
     * The elementary right hand side
     */
    private double[] elementVector; // protected to be able to use it in subclasses

    /**
     * @param nb the global number of the element
     */
    public Elem(int nb) {
        globalNb = nb;
    }

    /**
     * Returns the ith MeshPoint of the element
     */
    public MeshPoint getMeshPoint(int i) {
        return meshPointArray[i];
    }

    /**
     * Sets the ith MeshPoint of the element to mp
     */
    public void setMeshPoint(int i, MeshPoint mp) {
        meshPointArray[i] = mp;
    }

    /**
     * Returns the number of MeshPoints of the element
     */
    public int getNbOfMeshPoints() {
        return meshPointArray.length;
    }

    /**
     * Sets the element to i MeshPoints
     */
    public void setNbOfMeshPoints(int i) {
        meshPointArray = new MeshPoint[i];
    }

    /**
     * Returns the global number of the element
     */
    public int getGlobalNb() {
        return globalNb;
    }

    /**
     * Returns the ith Node of the element
     */
    public Node getNode(int i) {
        return (Node) meshPointArray[i];
    }

    /**
     * Returns a description of the element
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < meshPointArray.length; i++)
            s = s + meshPointArray[i].getGlobalNb() + " ";
        return "Elem : " + globalNb + " - " + s;
    }

    /**
     * Retuns a copy of the element
     */
    public Object clone() {
        try {
            return super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    /**
     * Computes the elementary stiffness matrix of the element in a domain with the coefficients Kxx Kyy
     */
    public double[][] calcElementMatrix(double Kxx, double Kyy) {
        elementMatrix = new double[getNbOfMeshPoints()][getNbOfMeshPoints()];
        Matrix22 KMat = new Matrix22(Kxx, 0, 0, Kyy);
        elementVector = new double[getNbOfMeshPoints()];

        for (int k = 0; k < getNbOfMeshPoints(); k++)
            elementVector[k] = 0;

        for (int k = 0; k < getNbOfMeshPoints(); k++) {
            for (int j = 0; j < getNbOfMeshPoints(); j++) {
                double value = 0;

                for (int i = 0; i < Data.getNbOfQuadratPts(); i++) {
                    Matrix22 Jacobi = calcJacobiAt(i);

                    Matrix22 lambdatild = Jacobi.inv().mult(KMat).mult(Jacobi.inv().transpose()).scalMult(Math.abs(Jacobi.det()));

                    Vector2 v1 = new Vector2(Data.getData(k, i, 1), Data.getData(k, i, 2));
                    Vector2 v2 = new Vector2(Data.getData(j, i, 1), Data.getData(j, i, 2));

                    value += Data.getWeight(i) * lambdatild.multVectLeft(v1).mult(v2);
                }

                elementMatrix[k][j] = value;
            }
        }

        // Boundary conditions 1st type, modifies elementVector
        for (int i = 0; i < getNbOfMeshPoints(); i++) {
            if (!getMeshPoint(i).isDirichletNode()) {
                double sum = 0;
                for (int k = 0; k < getNbOfMeshPoints(); k++)
                    if (getMeshPoint(k).isDirichletNode())
                        sum += getMeshPoint(k).getDirichletValue() * elementMatrix[i][k];
                elementVector[i] -= sum;
            }
        }

        // Boundary conditions 1st type
        for (int i = 0; i < getNbOfMeshPoints(); i++) {
            if (getMeshPoint(i).isDirichletNode()) {
                for (int j = 0; j < getNbOfMeshPoints(); j++) {
                    elementMatrix[j][i] = 0;
                    elementMatrix[i][j] = 0;
                }
                elementMatrix[i][i] = 1;
            }
        }

        //System.out.println(elementMatrix[0][0] + " " + elementMatrix[0][1] + " " + elementMatrix[0][2] + " " + elementMatrix[0][3] + " " + elementMatrix[0][4] + " " + elementMatrix[0][5]);
        //System.out.println(elementMatrix[1][0] + " " + elementMatrix[1][1] + " " + elementMatrix[1][2] + " " + elementMatrix[1][3] + " " + elementMatrix[1][4] + " " + elementMatrix[1][5]);
        //System.out.println(elementMatrix[2][0] + " " + elementMatrix[2][1] + " " + elementMatrix[2][2] + " " + elementMatrix[2][3] + " " + elementMatrix[2][4] + " " + elementMatrix[2][5]);
        //System.out.println(elementMatrix[3][0] + " " + elementMatrix[3][1] + " " + elementMatrix[3][2] + " " + elementMatrix[3][3] + " " + elementMatrix[3][4] + " " + elementMatrix[3][5]);
        //System.out.println(elementMatrix[4][0] + " " + elementMatrix[4][1] + " " + elementMatrix[4][2] + " " + elementMatrix[4][3] + " " + elementMatrix[4][4] + " " + elementMatrix[4][5]);
        //System.out.println(elementMatrix[5][0] + " " + elementMatrix[5][1] + " " + elementMatrix[5][2] + " " + elementMatrix[5][3] + " " + elementMatrix[5][4] + " " + elementMatrix[5][5] + "\n");

        return elementMatrix;
    }

    /**
     * Computes the elementary last vector of the element
     */
    public double[] calcElementVector(int domainNbr) {
        for (int k = 0; k < getNbOfMeshPoints(); k++) {
            double value = 0;

            for (int i = 0; i < Data.getNbOfQuadratPts(); i++) {
                double absJacobi = Math.abs(calcJacobiAt(i).det());
                value += Data.getWeight(i) * Data.getData(k, i, 0) * Data.computeFAt(domainNbr - 1, xi2x(i)) * absJacobi;
            }
            elementVector[k] += value;
        }

        //System.out.println("elementVector");
        //System.out.println(elementVector[0]);
        //System.out.println(elementVector[1]);
        //System.out.println(elementVector[2] + "\n");

        return elementVector;
    }


    /**
     * Computes the Jacobi of the element at the ith quadrature point
     */
    abstract public Matrix22 calcJacobiAt(int i);

    /**
     * Performs the transformation from Xi to X of the ith quadrature point, used for the computation of the values of f
     */
    abstract public Vector2 xi2x(int i);

    /**
     * Returns the number of Nodes (of summits) of the element
     */
    abstract public int getNumberOfNodes();


}
