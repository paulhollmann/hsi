package FEJDMath;

import java.util.ArrayList;

/**
 * Represents a domain of the mesh, ie a group of elements having the same coefficients of thermal conductivity
 */
class Domain {

    /**
     * Array containing the elements of the domain
     */
    private ArrayList elemArray;
    /**
     * List containing the nodes of the domain
     */
    private MeshPointList nodeList;
    /**
     * List containing the coupling nodes of the domain
     */
    private MeshPointList couplingNodeList;
    /**
     * The coefficients of the domain
     */
    private double Kxx, Kyy;
    /**
     * The global number of the domain
     */
    private int globalNb;
    /**
     * The last vector
     */
    private double[] lastVector;
    /**
     * The stiffness matrix stored in skyline format
     */
    private SkyLine stiffnessMatrix;


    /**
     * @param nb the global number of the domain
     * @param Kx the coefficient of thermal conductivity lambdaxx of the domain
     * @param Ky the coefficient of thermal conductivity lambdayy of the domain
     */
    public Domain(int nb, double Kx, double Ky) {
        globalNb = nb;
        Kxx = Kx;
        Kyy = Ky;
    }

    /**
     * Returns a description of the domain ie the coefficients of thermal conductivity and a description of each element
     */
    public String toString() {
        String s = "Domain " + globalNb + " : " + Kxx + " " + Kyy + " - ";
        for (int i = 0; i < elemArray.size(); i++)
            s += ((Elem) elemArray.get(i)).getGlobalNb() + " ";
        return s;
    }

    /**
     * Returns the coefficient lambdaxx of the domain
     */
    public double getKxx() {
        return Kxx;
    }

    /**
     * Returns the coefficient lambdayy of the domain
     */
    public double getKyy() {
        return Kyy;
    }

    public MeshPointList getNodeList() {
        return nodeList;
    }

    public MeshPointList getCouplingNodeList() {
        return couplingNodeList;
    }

    /**
     * returns true if e1 and e2 are elements of this domains elemArray
     */
    public boolean sameDomain(Elem e1, Elem e2) {
        return elemArray.contains(e1) & elemArray.contains(e2);
    }

    /**
     * Sets the ith element of the domain to el
     */
    public void setElem(int i, Elem el) {
        elemArray.set(i, el);
    }

    /**
     * Adds the element el to the domain
     */
    public void addElem(Elem el) {
        elemArray.add(el);
    }

    /**
     * Returns the ith element of the domain
     */
    public Elem getElem(int i) {
        return (Elem) elemArray.get(i);
    }

    /**
     * Returns the number of elements of the domain
     */
    public int getNbOfElem() {
        return elemArray.size();
    }

    /**
     * Sets the domain to i elements
     */
    public void setNbOfElem(int i) {
        elemArray = new ArrayList(i);
        for (int k = 0; k < i; k++)
            elemArray.add(k, null);
    }

    /**
     * initialize double[size] vector
     */
    private double[] initRHS(int size) {
        double[] RHS = new double[size];
        // the stiffnessMatrix is already set to 0
        for (int i = 0; i < size; i++)
            RHS[i] = 0;
        return RHS;
    }

    /**
     * Adds x to the ith element of the last vector
     */
    public void addLVector(int i, double x) {
        //System.out.println("addLVector element " + i + " value=" + x);
        lastVector[i] += x;
    }

    /**
     * Computes the elementary stiffness matrix for every element of the domain
     */
    public void calcAllElementMatrix(int size) {
        lastVector = initRHS(size);
        stiffnessMatrix = new SkyLine(size);
        nodeList = new MeshPointList(size);
        couplingNodeList = new MeshPointList(size);

        for (int i = 0; i < elemArray.size(); i++) {

            Elem el = (Elem) elemArray.get(i);

            // Computing of the element matrices and element vectors :
            double[][] curMatrix = el.calcElementMatrix(Kxx, Kyy);
            double[] curVector = el.calcElementVector(globalNb);

            // Assembling :
            for (int j = 0; j < el.getNbOfMeshPoints(); j++) {
                MeshPoint mp = el.getMeshPoint(j);
                nodeList.add(mp);
                int gNbj = mp.getGlobalNb();

                addLVector(gNbj - 1, curVector[j]);

                for (int k = 0; k < el.getNbOfMeshPoints(); k++) {
                    int gNbk = el.getMeshPoint(k).getGlobalNb();
                    if (gNbk >= gNbj) {
                        stiffnessMatrix.addSMatrix(gNbj, gNbk, curMatrix[j][k]);
                    }
                }
            }
        }

        // Calculate coupling nodes of this domain

        for (int i = 0; i < elemArray.size(); i++) {

            Elem el = (Elem) elemArray.get(i);
            if (el instanceof Triangle) {
                Triangle t1 = (Triangle) el;
                MeshPointList mpl = new MeshPointList(3);
                for (int k = 0; k < t1.getNbOfMeshPoints(); k++)
                    mpl.add(t1.getMeshPoint(k));

                for (int j = 0; j < t1.getNbOfNeigh(); j++) {
                    Triangle t2 = t1.getNeigh(j);
                    if (!sameDomain(t1, t2)) {
                        for (int k = 0; k < t2.getNbOfMeshPoints(); k++)
                            if (mpl.contains(t2.getMeshPoint(k)))
                                couplingNodeList.add(t2.getMeshPoint(k));
                    }
                }
            }
        }
    }

    public double[] getLastVector() {
        return lastVector;
    }

    public void setLastVector(double[] lastVector) {
        this.lastVector = lastVector;
    }

    public SkyLine getStiffnessMatrix() {
        return stiffnessMatrix;
    }

    public void setStiffnessMatrix(SkyLine stiffnessMatrix) {
        this.stiffnessMatrix = stiffnessMatrix;
    }

}
