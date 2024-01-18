package FEJDMath;

import java.util.Vector;

/**
 * Represents a point of the mesh. A MeshPoint is also described by the list of his neighbours for the renumbering
 */
public class MeshPoint {

    /**
     * The global number of the mesh point
     */
    private int globalNb;
    /**
     * The coordinates of the mesh point
     */
    private double x, y;
    /**
     * The temperature of the mesh point
     */
    private double temperature;
    /**
     * Stores if the point has a dirichlet condition
     */
    private boolean isDirichlet;
    /**
     * The value of the dirichlet condition
     */
    private double dirichletVal;
    /**
     * The value of the new global number after renumbering
     */
    private int newNb;
    /**
     * Array containing the successors of the point
     */
    private Vector Succ = new Vector(6, 1);
    /**
     * Stores if the point has been renumbered
     */
    private boolean isRen = false;
    /**
     * Stores if the point has been marked in the renumbring process
     */
    private boolean isMar = false;

    /**
     * @param nb the global number of the meshpoint
     * @param xx the x coordinate of the meshpoint
     * @param yy the y coordinate of the meshpoint
     */
    public MeshPoint(int nb, double xx, double yy) {
        x = xx;
        y = yy;
        globalNb = nb;
        temperature = 0;
        isDirichlet = false;
    }

    /**
     * @param nb   the global number of the meshpoint
     * @param xx   the x coordinate of the meshpoint
     * @param yy   the y coordinate of the meshpoint
     * @param temp the temperature of the meshpoint
     */
    public MeshPoint(int nb, double xx, double yy, double temp) {
        x = xx;
        y = yy;
        globalNb = nb;
        temperature = temp;
        isDirichlet = false;
    }

    /**
     * Return a description of the meshpoint
     */
    public String toString() {
        return "MeshPoint " + globalNb + " : " + x + " " + y + " " + temperature;
    }

    /**
     * Returns the global number of the meshpoint
     */
    public int getGlobalNb() {
        return globalNb;
    }

    /**
     * Sets the global number of the meshpoint to i
     */
    public void setGlobalNb(int i) {
        globalNb = i;
    }

    /**
     * Returns the x coordinate of the meshpoint
     */
    public double getx() {
        return x;
    }

    /**
     * Returns the y coordinate of the meshpoint
     */
    public double gety() {
        return y;
    }

    /**
     * Returns the temperature of the meshpoint
     */
    public double getTemperature() {
        return temperature;
    }

    /**
     * Sets the temperature of the meshpoint to temp
     */
    public void setTemperature(double temp) {
        temperature = temp;
    }


// for the renumbering :

    // Variables

    /**
     * Returns true if the meshpoint is a dirichlet node
     */
    public boolean isDirichletNode() {
        return isDirichlet;
    }

    /**
     * Sets the dirichlet state of the meshpoint to state
     */
    public void setDirichlet(boolean state) {
        isDirichlet = state;
    }

    /**
     * Returns the dirichlet value of the meshpoint
     */
    public double getDirichletValue() {
        return dirichletVal;
    }

    /**
     * Sets the dirichlet value of the meshpoint to val
     */
    public void setDirichletValue(double val) {
        dirichletVal = val;
    }

    // Accessors

    /**
     * Returns the ith neighbour of the MeshPoint
     */
    public MeshPoint getSucc(int i) {
        return (MeshPoint) Succ.get(i);
    }

    /**
     * Returns the number of neighbours of the MeshPoint
     */
    public int getNbOfSucc() {
        return Succ.size();
    }

    /**
     * Returns true if the MeshPoint is marked (that means that we know his level (cf the reverse Cuthill McKee algorithm))
     */
    public boolean isMarked() {
        return isMar;
    }

    /**
     * Marks the MeshPoint (ie indicates that we know his level (cf the reverse Cuthill McKee algorithm))
     */
    public void mark() {
        isMar = true;
    }

    /**
     * Unmarks the MeshPoint
     */
    public void unmark() {
        isMar = false;
    }

    /**
     * Sets isMar to b (ie indicates if the MeshPoints is renumbered)
     */
    public void setRenumber(boolean b) {
        isRen = b;
    }

    /**
     * Returns true if the MeshPoint is already renumbered
     */
    public boolean isRenumbered() {
        return isRen;
    }

    /**
     * Add the MeshPoint n to the list of successors (= neighbors) of the MeshPoint
     */
    public void addSucc(MeshPoint n) {
        boolean Already = false;
        for (int i = 0; i < Succ.size() && !Already; i++)
            if (n.globalNb == ((MeshPoint) Succ.get(i)).globalNb)
                Already = true;
        if (!Already)
            Succ.add(n);
    }

    /**
     * Add the array of MeshPoints tabNod to the list of successors (= neighbors) of the MeshPoint
     */
    public void addSucc(MeshPoint[] tabMeshPoint) {
        for (int i = 0; i < tabMeshPoint.length; i++)
            Succ.add(tabMeshPoint[i]);
    }

    /**
     * Returns the number of the MeshPoint in the new numbering
     */
    public int getNewNb() {
        return newNb;
    }

    /**
     * Sets the number of the MeshPoint in the new numbering
     */
    public void setNewNb(int i) {
        newNb = i;
    }

    // Methods

    /**
     * Renumbers the MeshPoint (with the next available number)
     */
    public void renum() {
        newNb = Renumbering.NB;
        Renumbering.NB++;
        isRen = true;
    }

    /**
     * Returns the eccentricity of the MeshPoint in the mesh reprensented by TabMeshPoint (cf Cuthill Mc Kee algortihm)
     */
    public int exc(MeshPoint[] meshPointArray) {
        int excent = 0;
        boolean[] Mark = new boolean[meshPointArray.length];
        for (int i = 0; i < Mark.length; i++)
            Mark[i] = false;
        Mark[globalNb - 1] = true;
        Vector level1 = new Vector(10, 10);
        Vector level2 = new Vector(10, 10);
        level1.add(this);
        MeshPoint neigh;
        boolean sameLevel = false;
        do {
            excent++;
            level2 = new Vector(10, 10);
            for (int k = 0; k < level1.size(); k++)
                for (int j = 0; j < ((MeshPoint) level1.get(k)).getNbOfSucc(); j++) {
                    neigh = ((MeshPoint) level1.get(k)).getSucc(j);
                    sameLevel = false;
                    for (int l = 0; l < level1.size(); l++)
                        if (((MeshPoint) level1.get(l)).globalNb == neigh.globalNb)
                            sameLevel = true;
                    if (!Mark[neigh.globalNb - 1] && !sameLevel) {
                        level2.add(neigh);
                        Mark[neigh.globalNb - 1] = true;
                    }
                }
            level1 = new Vector(10, 10);
            for (int k = 0; k < level2.size(); k++)
                level1.add(level2.get(k));
        } while (level2.size() != 0);
        return --excent;
    }

    /**
     * Returns the MeshPoints that are on the last "neighbours-level" of the MeshPoint. They are sorted by degree
     */
    public Vector lastLevel(MeshPoint[] meshPointArray) {

        boolean[] Mark = new boolean[meshPointArray.length];
        for (int i = 0; i < Mark.length; i++)
            Mark[i] = false;
        Mark[globalNb - 1] = true;

        Vector level1 = new Vector(10, 10);
        Vector level2 = new Vector(10, 10);
        level1.add(this);
        MeshPoint neigh;
        boolean sameLevel = false;
        do {
            level2 = new Vector(10, 10);
            for (int k = 0; k < level1.size(); k++)
                for (int j = 0; j < ((MeshPoint) level1.get(k)).getNbOfSucc(); j++) {
                    neigh = ((MeshPoint) level1.get(k)).getSucc(j);
                    sameLevel = false;
                    for (int l = 0; l < level1.size(); l++)
                        if (((MeshPoint) level1.get(l)).globalNb == neigh.globalNb)
                            sameLevel = true;
                    if (!Mark[neigh.globalNb - 1] && !sameLevel) {
                        level2.add(neigh);
                        Mark[neigh.globalNb - 1] = true;
                    }
                }
            if (level2.size() != 0) {
                level1 = new Vector(10, 10);
                for (int k = 0; k < level2.size(); k++)
                    level1.add(level2.get(k));
            }
        } while (level2.size() != 0);

        Vector sortedLevel = new Vector();
        while (level1.size() > 0) {
            MeshPoint m = (MeshPoint) level1.get(0);
            int k = 0;
            for (int i = 1; i < level1.size(); i++)
                if (((MeshPoint) level1.get(i)).getNbOfSucc() < m.getNbOfSucc()) {
                    m = (MeshPoint) level1.get(i);
                    k = i;
                }
            sortedLevel.add(m);
            level1.remove(k);
        }

        return sortedLevel;
    }

    /**
     * Returns a string describing the change in global number of the point in the renumbering
     */
    public String toString2() {
        return "old N� = " + globalNb + "   - new N� =" + newNb;
    }

    /**
     * Returns a string with the global number of the neighbour of the MeshPoint
     */
    public String toString3() {
        String s = "N� = " + newNb + "\n";
        for (int i = 0; i < Succ.size(); i++)
            s += ((MeshPoint) Succ.get(i)).newNb + " ";
        return s;
    }
}
