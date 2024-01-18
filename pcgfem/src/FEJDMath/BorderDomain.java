package FEJDMath;

import java.util.ArrayList;

/**
 * Represents a border domain of the mesh, ie a group of borders having the same boundary conditions
 */
public class BorderDomain {

    /**
     * Array containing the borders of the border domain
     */
    private ArrayList borderArray;
    /**
     * The type of boundary conditions : 1 for dirichlet, 2 for neumann, 3 for robin
     */
    private int type;

    /**
     * @param nb    the number of borders the domain contains
     * @param style the type of the border : 1 for Dirichlet, 2 for Neumann, 3 for Robin
     */
    public BorderDomain(int nb, int style) {
        type = style;
        borderArray = new ArrayList(nb);
        for (int k = 0; k < nb; k++)
            borderArray.add(k, null);
    }

    /**
     * Returns a description of the borderDomain ie a description of each of its borders
     */
    public String toString() {
        String s = "Border Domain : " + type + "\n";
        for (int i = 0; i < borderArray.size(); i++)
            s += ((MeshBorder) borderArray.get(i)).toString2();
        return s;
    }

    /**
     * Returns the type of the domain
     */
    public int getType() {
        return type;
    }

    /**
     * Returns the ith border of the domain
     */
    public MeshBorder getBorder(int i) {
        return (MeshBorder) borderArray.get(i);
    }

    /**
     * Sets the ith border of the domain to ed
     */
    public void setBorder(int i, MeshBorder ed) {
        borderArray.set(i, ed);
    }

    /**
     * Adds the Border b to the domain border
     */
    public void addBorder(MeshBorder b) {
        borderArray.add(b);
    }

    /**
     * Computes the boundary conditions of every borders of the domain
     */
    public void calcAllBC(Domain domain) {
        for (int i = 0; i < borderArray.size(); i++) {
            ((MeshBorder) borderArray.get(i)).calcBCondition(domain);
        }
    }

    /**
     * Returns the number of borders of the domain
     */
    public int getNbOfBorders() {
        return borderArray.size();
    }

    /**
     * Sets the domain to i borders
     */
    public void setNbOfBorders(int i) {
        borderArray = new ArrayList(i);
        for (int k = 0; k < i; k++)
            borderArray.add(k, null);
    }
}
