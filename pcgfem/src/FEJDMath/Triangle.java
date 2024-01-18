package FEJDMath;

import java.util.Vector;

/**
 * Represents a triangular element of the mesh
 */
public class Triangle extends Elem {
    /**
     * Array containing the neighbors of the element
     */
    private Vector Neigh = new Vector(3, 1);
    /**
     * The number of split edges of the triangle, -1 if the triangle has already been split (used for the refinement)
     */
    private int marks = 0;

    /**
     * @param nb the global nb of the Triangle
     * @param n0 the first node of the triangle
     * @param n1 the second node of the triangle
     * @param n2 the third node of the triangle
     */
    public Triangle(int nb, Node n0, Node n1, Node n2, MeshPoint[] arr) {
        super(nb);
        setNbOfMeshPoints(3 + arr.length);
        setMeshPoint(0, n0);
        setMeshPoint(1, n1);
        setMeshPoint(2, n2);

        for (int i = 0; i < arr.length; i++)
            setMeshPoint(i + 3, arr[i]);
    }

    /**
     * @param nb the global number of the Triangle
     * @param n0 the first node of the triangle
     * @param n1 the second node of the triangle
     * @param n2 the third node of the triangle
     */
    public Triangle(int nb, Node n0, Node n1, Node n2) {
        super(nb);
        setNbOfMeshPoints(3);
        setMeshPoint(0, n0);
        setMeshPoint(1, n1);
        setMeshPoint(2, n2);
    }

    public int getNumberOfNodes() {
        return 3;
    }

    public Matrix22 calcJacobiAt(int i) {
        double n0x = getNode(0).getx();
        double n0y = getNode(0).gety();
        double n1x = getNode(1).getx();
        double n1y = getNode(1).gety();
        double n2x = getNode(2).getx();
        double n2y = getNode(2).gety();
        return new Matrix22(n1x - n0x, n2x - n0x, n1y - n0y, n2y - n0y);

    }


    // For the refinement :

    public Vector2 xi2x(int i) {
        return calcJacobiAt(i).multVectRight(new Vector2(Data.getQuadratPts(i, 0), Data.getQuadratPts(i, 1))).add(new Vector2(getNode(0).getx(), getNode(0).gety()));
        // in this case we know that the Jacobi is a constant, works only for linear transformation of the element
    }

    public String toString() {
        return "Triangle " + super.toString();
    }

    /**
     * Returns the number of Marks of the triangle
     */
    public int getNbOfMarks() {
        return marks;
    }

    /**
     * Sets the Marks of the triangle to m
     */
    public void setMarks(int m) {
        marks = m;
    }

    /**
     * Add a mark the triangle
     */
    public void addMark() {
        marks++;
    }

    /**
     * Returns the ith neighbour of the Triangle
     */
    public Triangle getNeigh(int i) {
        return (Triangle) Neigh.get(i);
    }

    /**
     * Returns the number of neighbours of the Triangle
     */
    public int getNbOfNeigh() {
        return Neigh.size();
    }

    /**
     * Deletes neighbours of the Triangle
     */
    public void deleteNeigh() {
        Neigh = new Vector(3, 1);
    }

    /**
     * Add the Triangle n to the list of neighbours of the Triangle
     */
    public void addNeigh(Triangle t) {
        boolean already = false;
        for (int i = 0; i < Neigh.size() && !already; i++)
            if (t.getGlobalNb() == ((Triangle) Neigh.get(i)).getGlobalNb())
                already = true;
        if (!already)
            Neigh.add(t);
    }

    /**
     * Add the array of Triangle tabTriangle to the list of neighbours of the Triangle
     */
    public void addNeigh(Triangle[] triangleArray) {
        for (int i = 0; i < triangleArray.length; i++)
            Neigh.add(triangleArray[i]);
    }

    /**
     * Returns a description of the elements and the global number of its neighbors
     */
    public String toString2() {
        String s = "Element nb " + getGlobalNb() + " - Neighbors :";
        for (int i = 0; i < getNbOfNeigh(); i++)
            s = s + " " + getNeigh(i).getGlobalNb() + " ";
        return s;
    }
}
