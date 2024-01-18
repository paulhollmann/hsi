package FEJDMath.hsi;

import mpi.MPI;

public class MPICommunication {


    public static void sendMatrixKAndVectorF(double[] K, double[] f, int SECONDARY_RANK) {
        MPI.COMM_WORLD.Send(K, 0, K.length, MPI.DOUBLE, SECONDARY_RANK, 0);
        MPI.COMM_WORLD.Send(f, 0, f.length, MPI.DOUBLE, SECONDARY_RANK, 0);
    }

    public static void receiveMatrixKAndVectorF(double[] K, double[] f, int PRIMARY_RANK) {
        MPI.COMM_WORLD.Recv(K, 0, K.length, MPI.DOUBLE, PRIMARY_RANK, 0);
        MPI.COMM_WORLD.Recv(f, 0, f.length, MPI.DOUBLE, PRIMARY_RANK, 0);
    }


    public static void initializeAndSendBeginningVector(double[] v0, int PRIMARY_RANK, int SECONDARY_RANK) {
        // As it is initialized as a zero vector, do nothing, just send
        MPI.COMM_WORLD.Send(v0, 0, v0.length, MPI.DOUBLE, SECONDARY_RANK, 0);
    }

    public static void receiveBeginningVector(double[] v0, int PRIMARY_RANK) {
        MPI.COMM_WORLD.Recv(v0, 0, v0.length, MPI.DOUBLE, PRIMARY_RANK, 0);
    }

    public static void sendConjugator(double[] C, int PRIMARY_RANK, int SECONDARY_RANK) {
        MPI.COMM_WORLD.Send(C, 0, C.length, MPI.DOUBLE, SECONDARY_RANK, 0);
    }

    public static void receiveConjugator(double[] C, int PRIMARY_RANK) {
        MPI.COMM_WORLD.Recv(C, 0, C.length, MPI.DOUBLE, PRIMARY_RANK, 0);
    }

    public static void allReduce(double[] buf) {
        MPI.COMM_WORLD.Allreduce(buf, 0, buf, 0, buf.length, MPI.DOUBLE, MPI.SUM);
    }

}
