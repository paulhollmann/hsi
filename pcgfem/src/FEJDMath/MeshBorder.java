package FEJDMath;

/**
 * Represents an border of the mesh
 */
public class MeshBorder {

    /**
     * Array containing the meshpoints of the border
     */
    private MeshPoint[] tabMeshPoint;
    /**
     * The blobal number of the border
     */
    private int globalNb;

    /**
     * @param nb  the global number of the border
     * @param nn1 the first node of the border
     * @param nn2 the second node of the border
     */
    public MeshBorder(int nb, Node nn1, Node nn2) {
        tabMeshPoint = new MeshPoint[2];
        tabMeshPoint[0] = nn1;
        tabMeshPoint[1] = nn2;
        globalNb = nb;
    }

    /**
     * @param nb    the global number of the border
     * @param nn1   the first node of the border
     * @param nn2   the second node of the border
     * @param mpTbl the array of the border's MeshPoints
     */
    public MeshBorder(int nb, Node nn1, Node nn2, MeshPoint[] mpTbl) {
        tabMeshPoint = new MeshPoint[mpTbl.length + 2];
        tabMeshPoint[0] = nn1;
        tabMeshPoint[1] = nn2;
        for (int i = 0; i < mpTbl.length; i++)
            tabMeshPoint[i + 2] = mpTbl[i];
        globalNb = nb;
    }

    /**
     * @param e an MeshBorder whose data will be copied into the new border
     */
    public MeshBorder(MeshBorder e) {
        tabMeshPoint = new MeshPoint[e.tabMeshPoint.length];
        for (int i = 0; i < e.tabMeshPoint.length; i++)
            tabMeshPoint[i] = e.tabMeshPoint[i];
        globalNb = e.globalNb;
    }

    /**
     * Returns a description of the border, ie its global number and those of its nodes
     */
    public String toString() {
        return "Edge : " + globalNb + " - " + tabMeshPoint[0].getGlobalNb() + " " + tabMeshPoint[1].getGlobalNb();
    }

    /**
     * Returns only the global number
     */
    public String toString2() {
        return "" + globalNb + "\n";
    }

    /**
     * Returns the global number of the edge
     */
    public int getGlobalNb() {
        return globalNb;
    }

    /**
     * Returns the ith MeshPoint
     */
    public MeshPoint getMeshPoint(int i) {
        return tabMeshPoint[i];
    }

    /**
     * Returns the ith node (the same as getNode1() and getNode2() but may be used in a loop)
     */
    public Node getNode(int i) {
        return (Node) tabMeshPoint[i];
    }

    /**
     * Returns the first node
     */
    public Node getNode1() {
        return (Node) tabMeshPoint[0];
    }

    /**
     * Returns the second node
     */
    public Node getNode2() {
        return (Node) tabMeshPoint[1];
    }

    /**
     * Returns the number of MeshPoints of the border
     */
    public int getNbOfMeshPoints() {
        return tabMeshPoint.length;
    }

    /**
     * Computes the boundary conditions (not for meshborders, only for borders...)
     */
    public void calcBCondition(Domain domain) {
    }

}
