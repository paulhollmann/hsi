package FEJDMath;

import java.util.ArrayList;

/**
 * Represents a mesh with all its elements (borders, elements, boundary conditions ...)
 */
public class Mesh {
    /**
     * Array containing the domains of the mesh
     */
    private Domain[] domainArray;
    /**
     * Array containing the borders of the mesh
     */
    private ArrayList borderArray;
    /**
     * Array containing the border domains of the mesh (each containing borders with the same boundary conditions)
     */
    private BorderDomain[] borderDomainArray;
    /**
     * Array containing the nodes of the mesh
     */
    private ArrayList nodeArray;
    /**
     * Array containing the meshpoints of the mesh
     */
    private ArrayList meshPointArray;
    /**
     * Array containing the elements of the mesh
     */
    private ArrayList elemArray;
    /**
     * The size of the mesh
     */
    private int size;
    /**
     * The type of functions used
     */
    private int fctType;
    /**
     * The type of elements used
     */
    private int elemType;

    /**
     * Creates a new mesh whose Shape-functions' type is FT
     */
    public Mesh(int FT) {
        fctType = FT;
    }

    /**
     * Returns a complete description of the mesh, useful for debugging
     */
    public String toString() {
        String s = "";
        for (int i = 0; i < meshPointArray.size(); i++)
            s += meshPointArray.get(i) + "\n";
        s += "\n-------------------------------------\n\n";
        for (int i = 0; i < elemArray.size(); i++)
            s += elemArray.get(i) + "\n";
        s += "\n-------------------------------------\n\n";
        for (int i = 0; i < borderArray.size(); i++)
            s += borderArray.get(i) + "\n";
        s += "\n-------------------------------------\n\n";
        for (int i = 0; i < borderDomainArray.length; i++)
            s += borderDomainArray[i] + "\n";
        s += "\n-------------------------------------\n\n";
        for (int i = 0; i < domainArray.length; i++)
            s += domainArray[i] + "\n";
        s += "\n-------------------------------------\n\n";

        return s;
    }

    /**
     * Returns the type of the shape-functions (Data.LINEAR, Data.QUADRATIC) of the mesh
     */
    public int getFctType() {
        return fctType;
    }

    /**
     * Sets the type of the shape-functions of the mesh
     */
    public void setFctType(int i) {
        fctType = i;
    }

    /**
     * Returns the type of the elements of the mesh
     */
    public int getElemType() {
        return elemType;
    }

    /**
     * Sets the type of the elements Data.TRIANGLE, Data.QUADRILATERAL) of the mesh
     */
    public void setElemType(int i) {
        elemType = i;
    }

    /**
     * Sets the ith domaine of the mesh to ndom
     */
    public void setDomain(int i, Domain ndom) {
        domainArray[i] = ndom;
    }

    /**
     * Sets the ith border of the mesh to nbord
     */
    public void setBorder(int i, MeshBorder nbord) {
        borderArray.set(i, nbord);
    }

    /**
     * Adds the Border b to the mesh
     */
    public void addBorder(MeshBorder b) {
        borderArray.add(b);
    }

    /**
     * Sets the ith border domain of the mesh to ndbord
     */
    public void setBorderDomain(int i, BorderDomain ndbord) {
        borderDomainArray[i] = ndbord;
    }

    /**
     * Sets the ith node of the mesh to nnode
     */
    public void setNode(int i, Node nnode) {
        nodeArray.set(i, nnode);
    }

    /**
     * Adds the node n to the mesh
     */
    public void addNode(Node n) {
        nodeArray.add(n);
    }

    /**
     * Sets the ith MeshPoint of the mesh to nmp
     */
    public void setMeshPoint(int i, MeshPoint nmp) {
        meshPointArray.set(i, nmp);
    }

    /**
     * Adds the MeshPoint mp to the mesh
     */
    public void addMeshPoint(MeshPoint mp) {
        meshPointArray.add(mp);
    }

    /**
     * Sets the ith element of the mesh to nelem
     */
    public void setElem(int i, Elem nelem) {
        elemArray.set(i, nelem);
    }

    /**
     * Adds the Element e to the mesh
     */
    public void addElem(Elem e) {
        elemArray.add(e);
    }

    /**
     * Returns the ith domain of the mesh
     */
    public Domain getDomain(int i) {
        return domainArray[i];
    }

    /**
     * Returns the ith border of the mesh
     */
    public MeshBorder getBorder(int i) {
        return (MeshBorder) borderArray.get(i);
    }

    /**
     * Returns the ith border domain of the mesh
     */
    public BorderDomain getBorderDomain(int i) {
        return borderDomainArray[i];
    }

    /**
     * Returns the ith node of the mesh
     */
    public Node getNode(int i) {
        return (Node) nodeArray.get(i);
    }

    /**
     * Returns the ith MeshPoint of the mesh
     */
    public MeshPoint getMeshPoint(int i) {
        return (MeshPoint) meshPointArray.get(i);
    }

    /**
     * Returns the ith elem of the mesh
     */
    public Elem getElem(int i) {
        return (Elem) elemArray.get(i);
    }

    /**
     * Returns a clone of the array containing all the elements of the mesh
     */
    public Elem[] getAllElem() {
        Elem[] ret = new Elem[elemArray.size()];
        for (int i = 0; i < elemArray.size(); i++)
            ret[i] = (Elem) (((Elem) elemArray.get(i)).clone());
        return ret;
    }

    /**
     * Returns the array containing all the MeshPoints of the mesh
     */
    public MeshPoint[] getAllMeshPoints() {
        MeshPoint[] ret = new MeshPoint[meshPointArray.size()];
        for (int i = 0; i < meshPointArray.size(); i++)
            ret[i] = (MeshPoint) meshPointArray.get(i);
        return ret;
    }

    /**
     * Returns the number of elements of the mesh
     */
    public int getNbOfElem() {
        return elemArray.size();
    }

    /**
     * Sets the mesh to i elements
     */
    public void setNbOfElem(int i) {
        elemArray = new ArrayList(i);
        for (int k = 0; k < i; k++)
            elemArray.add(k, null);
    }

    /**
     * Returns the number of nodes of the mesh
     */
    public int getNbOfNodes() {
        return nodeArray.size();
    }

    /**
     * Sets the mesh to i nodes
     */
    public void setNbOfNodes(int i) {
        nodeArray = new ArrayList(i);
        for (int k = 0; k < i; k++)
            nodeArray.add(k, null);
    }

    /**
     * Returns the number of MehPoints of the mesh
     */
    public int getNbOfMeshPoints() {
        return meshPointArray.size();
    }

    /**
     * Sets the mesh to i MeshPoints
     */
    public void setNbOfMeshPoints(int i) {
        meshPointArray = new ArrayList(i);
        for (int k = 0; k < i; k++)
            meshPointArray.add(k, null);

    }

    /**
     * Returns the number of domains of the mesh
     */
    public int getNbOfDomains() {
        return domainArray.length;
    }

    /**
     * Sets the mesh to i domains
     */
    public void setNbOfDomains(int i) {
        domainArray = new Domain[i];
    }

    /**
     * Returns the number of border domains of the mesh
     */
    public int getNbOfBorderDomains() {
        return borderDomainArray.length;
    }

    /**
     * Sets the mesh to i border domains
     */
    public void setNbOfBorderDomains(int i) {
        borderDomainArray = new BorderDomain[i];
    }

    /**
     * Returns the number of borders of the mesh
     */
    public int getNbOfBorders() {
        return borderArray.size();
    }

    /**
     * Sets the mesh to i border elements
     */
    public void setNbOfBorders(int i) {
        borderArray = new ArrayList(i);
        for (int k = 0; k < i; k++)
            borderArray.add(k, null);
    }

    /**
     * Returns the size of the mesh ie the number of mesh points
     */
    public int getSize() {
        return size;
    }

    /**
     * sets the size of the mesh to i
     */
    public void setSize(int i) {
        size = i;
    }

    /**
     * Displays the associated matrix and the last vector, useful for debugging
     */
    public void printDebug() {
        // ------------ begin debugging info
        System.out.println("Debugging info");
        for (int i = 0; i < getNbOfDomains(); i++) {
            //System.out.println(stiffnessMatrix); // may take VERY long
            if (null != getDomain(i).getStiffnessMatrix()) {
                System.out.println(getDomain(i).getStiffnessMatrix().toString2());
                double[] lastVector = getDomain(i).getLastVector();
                System.out.println("LastVector");
                for (int j = 0; j < lastVector.length; j++) {
                    System.out.print(lastVector[j] + " ");
                    if (j % 10 == 0 && j != 0)
                        System.out.print("\n");
                }
                System.out.println();
            }
        }
        // ------------ end debugging info
    }

}
