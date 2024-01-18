package FEJDMath;

/**
 * The thread used to simulate the boundary conditions of the first type
 */
class SimulBCT1Task {

    /**
     * The mesh on which the computation is performed
     */
    private Mesh m;
    private Domain domain;
    /**
     * The last vector
     */
    private double[] lastVector;
    /**
     * The stiffness matrix
     */
    private SkyLine stiffnessMatrix;

    /**
     * Constructs a new thread for simulating the boundary conditions of first type
     */
    public SimulBCT1Task(Mesh m, int i) {
        this.m = m;
        domain = m.getDomain(i);
        this.lastVector = domain.getLastVector();
        this.stiffnessMatrix = domain.getStiffnessMatrix();
    }

    /**
     * Performs the simulation, sets the diagonal entries of the matrix corresponding to dirichlet nodes to 1 and sets the values of the dirichlet nodes in the last vector
     */
    public void simulate() {
        calculate2();
        calculate3();
        for (int i = 0; i < m.getNbOfMeshPoints(); i++) {

            if (m.getMeshPoint(i).isDirichletNode()) {
                int gNb = m.getMeshPoint(i).getGlobalNb();
                stiffnessMatrix.set(gNb, gNb, 1); // set the diagonal entries to 1
                lastVector[gNb - 1] = m.getMeshPoint(i).getDirichletValue(); // set the value of the dirichlet node in the last vector
            }
        }
    }

    /**
     * Performs the calculation ie ask each border to compute its boundary conditions type 2
     */
    private void calculate2() {
        for (int i = 0; i < m.getNbOfBorderDomains(); i++) {
            if (m.getBorderDomain(i).getType() == 2)
                m.getBorderDomain(i).calcAllBC(domain);
        }
    }

    /**
     * Performs the calculation ie ask each border to compute its boundary conditions type 3
     */
    private void calculate3() {
        for (int i = 0; i < m.getNbOfBorderDomains(); i++) {
            if (m.getBorderDomain(i).getType() == 3)
                m.getBorderDomain(i).calcAllBC(domain);
        }
    }

    /**
     * Returns the mesh
     */
    public Domain getDomain() {
        return domain;
    }
}
