package FEJDMath;

/**
 * Represents a border of the mesh whose nodes have dirichlet boundary conditions
 */
class Border1 extends MeshBorder {

    /**
     * @param e the considered MeshBorder
     * @param a the value of the boundary condition on the first node
     * @param b the value of the boundary condition on the second node
     */
    public Border1(MeshBorder e, double a, double b) {
        super(e);
        getNode1().setDirichlet(true);
        getNode2().setDirichlet(true);
        getNode1().setDirichletValue(a);
        getNode2().setDirichletValue(b);
    }

    /**
     * Returns a decription of the border
     */
    public String toString() {
        return "Border1 : " + getGlobalNb() + ": " + getNode1().getGlobalNb() + " " + getNode2().getGlobalNb() + "; t1= " + getNode1().getDirichletValue() + " t2= " + getNode2().getDirichletValue();
    }

    /**
     * Returns a decription of the border containing only its global number and the value of t1 and t2
     */
    public String toString2() {
        return "" + getGlobalNb() + " t1= " + getNode1().getDirichletValue() + " t2= " + getNode2().getDirichletValue() + "\n";
    }

}
