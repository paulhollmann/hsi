package FEJDMath;

import FEJDMath.hsi.MathOperations;
import mpi.MPIException;

import java.util.Arrays;

/**
 * The thread used to solve the problem
 */
class SolutionTask {

    /**
     * The mesh on which the calculation is performed
     */
    private Mesh m;
    /**
     * The last vector
     */
    private double[] lastVector;
    /**
     * The stiffness matrix
     */
    private SkyLine stiffnessMatrix;
    /** The solution vector */


    /**
     * Constructs a new thread for solving the problem
     */
    public SolutionTask(Mesh m, int domain) {
        this.m = m;
        this.lastVector = m.getDomain(domain).getLastVector();
        //for(int i = 0; i < this.lastVector.length; i++)
        //    System.out.println(this.lastVector[i]);
        this.stiffnessMatrix = m.getDomain(domain).getStiffnessMatrix();
    }

    /**
     * Solves the problem, ie calls the methods of skyline to make the decomposition and perform the back anf forward substitution, mesh points are then set to their right temperature
     */
    public void solve() {
        //m.printDebug();
        double[] solLin = stiffnessMatrix.linSolve(lastVector); // solution of the problem
        System.out.println("solLin: " + Arrays.toString(solLin));

        double[] solCG = stiffnessMatrix.cgSolve(lastVector); // solution of the problem
        System.out.println("solCG: " + Arrays.toString(solCG));

        if (MathOperations.vectorComparison(solLin, solCG))
            System.out.println("solLin == solCG");
        else
            System.out.println("solLin != solCG");

        double[] solPCG = stiffnessMatrix.pcgSolve(lastVector); // solution of the problem
        System.out.println("solPCG: " + Arrays.toString(solPCG));

        if (MathOperations.vectorComparison(solLin, solPCG))
            System.out.println("solLin == solPCG");
        else
            System.out.println("solLin != solPCG");

        try {
            double[] solPCGP = stiffnessMatrix.pcgSolveParallel(lastVector); // solution of the problem
            System.out.println("solPCGP: " + Arrays.toString(solPCGP));
            if (MathOperations.vectorComparison(solPCG, solPCGP))
                System.out.println("solPCG == solPCGP");
            else
                System.out.println("solPCG != solPCGP");
        } catch (MPIException mpiEx) {
            System.out.println(mpiEx.getMessage());
        }

        for (int i = 0; i < m.getNbOfMeshPoints(); i++) {
            MeshPoint mp = m.getMeshPoint(i);
            mp.setTemperature(solLin[mp.getGlobalNb() - 1]);
            System.out.println(mp.getGlobalNb() + " : " + mp.getTemperature());
        }

    }
}
