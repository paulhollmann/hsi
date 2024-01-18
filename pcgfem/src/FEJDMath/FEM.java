package FEJDMath;

public class FEM {

    public static String[] args;
    private static int domain = 0;

    public static void main(String[] args) {
        FEM.args = args;

        String fname = "data/h03/9Meshpoints_1Domain";

        Mesh m = new Mesh(Data.LINEAR);

        Readers reader = new Readers();

        reader.readFile(m, fname + ".net", fname + ".dat", false);

        //Renumbering.McKee(MathStuff.getMesh(), false);
        Refinement r = new Refinement(m, 0.0);
        r.createNeighbours(m);
        /** Creates and assembles the stiffness matrix */
        m.getDomain(domain).calcAllElementMatrix(m.getSize());

        SimulBCT1Task bc1 = new SimulBCT1Task(m, domain);
        bc1.simulate();
        SolutionTask solThread = new SolutionTask(m, domain);
        solThread.solve();
    }

}
