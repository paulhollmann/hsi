package FEJDMath;

import java.util.Vector;

/**
 * Represents a quadrilateral element of the mesh
 */
public class Quadrilateral extends Elem {
    // PS
    private Vector Neigh = new Vector(4, 1);

    /**
     * @param nb the global nb of the Quadrilateral
     * @param n0 the first node of the Quadrilateral
     * @param n1 the second node of the Quadrilateral
     * @param n2 the third node of the Quadrilateral
     * @param n3 the fourth node of the Quadrilateral
     */
    public Quadrilateral(int nb, Node n0, Node n1, Node n2, Node n3, MeshPoint[] arr) {
        super(nb);
        setNbOfMeshPoints(4 + arr.length);
        setMeshPoint(0, n0);
        setMeshPoint(1, n1);
        setMeshPoint(2, n2);
        setMeshPoint(3, n3);

        for (int i = 0; i < arr.length; i++)
            setMeshPoint(i + 4, arr[i]);
    }

    /**
     * @param nb the global nb of the Quadrilateral
     * @param n0 the first node of the Quadrilateral
     * @param n1 the second node of the Quadrilateral
     * @param n2 the third node of the Quadrilateral
     * @param n3 the fourth node of the Quadrilateral
     */
    public Quadrilateral(int nb, Node n0, Node n1, Node n2, Node n3) {
        super(nb);
        setNbOfMeshPoints(4);
        setMeshPoint(0, n0);
        setMeshPoint(1, n1);
        setMeshPoint(2, n2);
        setMeshPoint(3, n3);
    }

    public int getNumberOfNodes() {
        return 4;
    }

    public Matrix22 calcJacobiAt(int i) {

        Matrix22 jac = new Matrix22(0, 0, 0, 0);

        //first coordinate
        for (int j = 1; j <= 2; j++) {
            double value = 0;
            for (int k = 0; k < getNbOfMeshPoints(); k++)
                value += getMeshPoint(k).getx() * Data.getData(k, i, j);

            jac.setNum(1, j, value);
        }
        //second coordinate
        for (int j = 1; j <= 2; j++) {
            double value = 0;
            for (int k = 0; k < getNbOfMeshPoints(); k++)
                value += getMeshPoint(k).gety() * Data.getData(k, i, j);

            jac.setNum(2, j, value);
        }

        return jac;
    }

    public Vector2 xi2x(int i) {

        Vector2 x = new Vector2(0, 0);
        for (int j = 0; j < getNbOfMeshPoints(); j++) {
            double value = Data.getData(j, i, 0);
            x = x.add(new Vector2(value * getMeshPoint(j).getx(), value * getMeshPoint(j).gety()));
        }

        return x;
    }

    public String toString() {
        return "Quadrilateral " + super.toString();
    }

    /**
     * Deletes neighbours of the Triangle
     */
    public void deleteNeigh() {
        Neigh = new Vector(4, 1);
    }

    /**
     * Add the Triangle n to the list of neighbours of the Triangle
     */
    public void addNeigh(Quadrilateral t) {
        boolean already = false;
        for (int i = 0; i < Neigh.size() && !already; i++)
            if (t.getGlobalNb() == ((Quadrilateral) Neigh.get(i)).getGlobalNb())
                already = true;
        if (!already)
            Neigh.add(t);
    }
}
