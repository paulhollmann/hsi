package FEJDMath;

import java.util.ArrayList;

public class MeshPointList {
    /**
     * Array containing the nodes of the domain
     */
    public ArrayList<MeshPoint> nodeList;

    public MeshPointList() {
        nodeList = new ArrayList<MeshPoint>();

    }

    public MeshPointList(int size) {
        nodeList = new ArrayList<MeshPoint>(size);
    }

    public void add(MeshPoint p) {
        if (!nodeList.contains(p))
            nodeList.add(p);
    }

    public boolean contains(MeshPoint p) {
        return nodeList.contains(p);
    }
}