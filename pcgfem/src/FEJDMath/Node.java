package FEJDMath;

/**
 * Represents a Node of the mesh, ie a mesh point which is located on a summit of an element (for exemple with linear functions, the mesh only contains nodes)
 */
public class Node extends MeshPoint {

    /**
     * @param nb the global number of the node
     * @param xx the x coordinate of the node
     * @param yy the y coordinate of the node
     */
    public Node(int nb, double xx, double yy) {
        super(nb, xx, yy);
    }

    /**
     * @param nb   the global number of the node
     * @param xx   the x coordinate of the node
     * @param yy   the y coordinate of the node
     * @param temp the temperature of the node
     */
    public Node(int nb, double xx, double yy, double temp) {
        super(nb, xx, yy, temp);
    }
}
