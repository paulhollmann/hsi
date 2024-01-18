package FEJDMath;

import java.util.Vector;

/**
 * This class permits to find a better numbering for the mesh
 */
public class Renumbering {


    /**
     * The global number of the next element which will be created
     */
    public static int NB = 1;

    /**
     * The global toggle for Renumbering On == true
     */
    public static boolean isRenumberAllowed = true;


    /**
     * Creates a link between the MeshPoint n1 and n2
     */
    public static void addLink(MeshPoint n1, MeshPoint n2) {
        n1.addSucc(n2);
        n2.addSucc(n1);
    }

    /**
     * Algorithm of reverse Cuthill Mc Kee applied to the Mesh m if the shape-functions are quadratic.
     * If newNetFile is true, a new net File will be created
     */
    public static Mesh McKee(Mesh m, boolean newNetFile) {

        NB = 1;

        for (int i = 0; i < m.getNbOfMeshPoints() && isRenumberAllowed; i++) {
            m.getMeshPoint(i).unmark();
            m.getMeshPoint(i).setRenumber(false);
        }

        // creates all the links btw the Nodes
        for (int i = 0; i < m.getNbOfElem() && isRenumberAllowed; i++) {
            Elem e = m.getElem(i);
            for (int j = 0; j < e.getNbOfMeshPoints() - 1; j++) {
                MeshPoint mp1 = e.getMeshPoint(j);
                int ne1 = mp1.getGlobalNb();
                for (int k = j + 1; k < e.getNbOfMeshPoints(); k++) {
                    MeshPoint mp2 = e.getMeshPoint(k);
                    int ne2 = mp2.getGlobalNb();
                    addLink(mp1, mp2);
                }
            }
        }


        // Find a good initial MeshPoint

        MeshPoint[] mpArray = m.getAllMeshPoints();

        MeshPoint n = mpArray[0];
        for (int i = 1; i < mpArray.length; i++)
            if (mpArray[i].getNbOfSucc() < n.getNbOfSucc())
                n = mpArray[i];

        int excMax = n.exc(mpArray);
        boolean action = true;
        while (action) {
            action = false;
            Vector lastLevel = n.lastLevel(mpArray);
            for (int i = 0; i < lastLevel.size(); i++) {
                if (((MeshPoint) lastLevel.get(i)).exc(mpArray) > excMax) {
                    action = true;
                    n = (MeshPoint) lastLevel.get(i);
                    excMax = n.exc(mpArray);
                    break;
                }
            }
        }

/*
      MeshPoint[] mpArray = m.getAllMeshPoints();
      MeshPoint n = mpArray[0];
      boolean finish = false;
      do {
         finish=true;
         int e = n.exc(mpArray);
         Vector v = n.lastLevel(mpArray);
         for (int i=0; i<v.size(); i++)
            if ( ((MeshPoint)v.get(i)).exc(mpArray) > e) {
               n = (MeshPoint) (v.get(i));
               finish=false;
               break;
            }
      } while (!finish && Display.frame.BP.isRenumberThreadAllowed());
*/

/*
      MeshPoint[] mpArray = m.getAllMeshPoints();
      MeshPoint n = mpArray[0];
      int excmax = n.exc(mpArray);
      int excprov;
      for (int i=1; i<mpArray.length; i++) {
         excprov = mpArray[i].exc(mpArray);
         if (excprov > excmax) {
            n = mpArray[i];
            excmax = excprov;
         }
      }

*/

        // Cuthill Mc Kee algorithm
        Vector S = new Vector(10, 10);
        Vector Snew = new Vector(10, 10);
        S.add(n);

        while (NB <= m.getNbOfMeshPoints() && isRenumberAllowed) {
            Snew = new Vector(10, 10);
            for (int i = 0; i < S.size(); i++) {
                MeshPoint x = (MeshPoint) S.get(i);
                Vector neighOfX = new Vector();
                for (int j = 0; j < x.getNbOfSucc(); j++)
                    if (!x.getSucc(j).isRenumbered())
                        neighOfX.add(x.getSucc(j));
                while (neighOfX.size() > 0) {
                    MeshPoint v = (MeshPoint) neighOfX.get(0);
                    int k = 0;
                    for (int j = 1; j < neighOfX.size(); j++)
                        if (((MeshPoint) neighOfX.get(j)).getNbOfSucc() < v.getNbOfSucc()) {
                            k = j;
                            v = (MeshPoint) neighOfX.get(j);
                        }
                    v.renum();
                    Snew.add(v);
                    neighOfX.remove(k);
                }
            }
            S = new Vector(10, 10);
            for (int i = 0; i < Snew.size(); i++)
                S.add(Snew.get(i));
        }


        // reverse :
        for (int i = 0; i < m.getNbOfMeshPoints(); i++) {
            int s = m.getMeshPoint(i).getNewNb();
            m.getMeshPoint(i).setNewNb(m.getNbOfMeshPoints() + 1 - s);
        }


        // Renumbers the Nodes :
        for (int i = 0; i < m.getNbOfMeshPoints() && isRenumberAllowed; i++) {
            int ngn = m.getMeshPoint(i).getNewNb();
            m.getMeshPoint(i).setGlobalNb(ngn);
        }

        if (newNetFile && isRenumberAllowed) {
            Writers writers = new Writers();
            writers.NetWriter(m, /* PATH + */ "_renum.net");
            m = new Mesh(m.getFctType());
            Readers reader = new Readers();
            reader.readFile(m, "*.net", "*.dat", false);
        }

        return m;
    }


}
