package FEJDMath;

import java.io.File;

/**
 * This class is used to refine a mesh so that all the temperature differences do not exceed a given value
 */
public class Refinement {

    public static final boolean isRefinementAllowed = false;

    private static final boolean isNewFileRefineSelected = false;

    /**
     * The global number of the next node which will be created
     */
    private int node_NB = 1;
    /**
     * The global number of the next border which will be created
     */
    private int border_NB = 1;
    /**
     * The global number of the next element which will be created
     */
    private int elem_NB = 1;
    /**
     * The mesh which we are refining
     */
    private Mesh m;
    /**
     * The maximum allowed temperature difference
     */
    private double maxDiff;

    /**
     * Constructs a new refinement object, which will refine the mesh m so that no temperature diffrence excedes maxDiff
     */
    public Refinement(Mesh m, double maxDiff) {
        this.m = m;
        this.maxDiff = maxDiff;
    }

    /**
     * Returns the index of the Domain Border which contains the MeshBorder e
     */
    private static int getBorderDomain(Mesh m, MeshBorder e) {
        for (int i = 0; i < m.getNbOfBorderDomains(); i++)
            for (int j = 0; j < m.getBorderDomain(i).getNbOfBorders(); j++)
                if (m.getBorderDomain(i).getBorder(j).getGlobalNb() == e.getGlobalNb())
                    return i;
        return -1; // ne devrait pas arriver...
    }

    /**
     * Creates the lists of the neighbours of each elements
     */
    public void createNeighbours(Mesh m) {
        Class elemClass = m.getElem(0).getClass(); // Müssen die Elemente nicht alle vom gleichen Typ sein?
        if (elemClass == Triangle.class) {
            for (int i = 0; i < m.getNbOfElem(); i++)
                ((Triangle) m.getElem(i)).deleteNeigh();
            for (int i = 0; i < m.getNbOfElem() - 1; i++) {
                Triangle t1 = (Triangle) m.getElem(i);
                for (int j = i + 1; j < m.getNbOfElem(); j++) {
                    Triangle t2 = (Triangle) m.getElem(j);
                    int nb_same = 0;
                    for (int s = 0; s < 3; s++)
                        for (int t = 0; t < 3; t++)
                            if (t1.getNode(s).getGlobalNb() == t2.getNode(t).getGlobalNb())
                                nb_same++;
                    if (nb_same == 2) {
                        t1.addNeigh(t2);
                        t2.addNeigh(t1);
                    }
                }
            }
        } else if (elemClass == Quadrilateral.class) {
            for (int i = 0; i < m.getNbOfElem(); i++)
                ((Quadrilateral) m.getElem(i)).deleteNeigh();
            for (int i = 0; i < m.getNbOfElem() - 1; i++) {
                Quadrilateral t1 = (Quadrilateral) m.getElem(i);
                for (int j = i + 1; j < m.getNbOfElem(); j++) {
                    Quadrilateral t2 = (Quadrilateral) m.getElem(j);
                    int nb_same = 0;
                    for (int s = 0; s < 4; s++)
                        for (int t = 0; t < 4; t++)
                            if (t1.getNode(s).getGlobalNb() == t2.getNode(t).getGlobalNb())
                                nb_same++;
                    if (nb_same == 2) {
                        t1.addNeigh(t2);
                        t2.addNeigh(t1);
                    }
                }
            }
        }
    }

    /**
     * Returns the cosinus of the angle (ABC)
     */
    private double cosABC(Node A, Node B, Node C) {
        double xA = A.getx();
        double yA = A.gety();
        double xB = B.getx();
        double yB = B.gety();
        double xC = C.getx();
        double yC = C.gety();
        double numerator = (xA - xB) * (xC - xB) + (yA - yB) * (yC - yB);
        double denominator = Math.sqrt(((xA - xB) * (xA - xB) + (yA - yB) * (yA - yB)) * ((xC - xB) * (xC - xB) + (yC - yB) * (yC - yB)));
        return numerator / denominator;
    }

    /**
     * Returns the index of the Node, whose coordinates are (x,y)
     */
    private int getIndex(Mesh m, double x, double y) {
        for (int i = 0; i < m.getNbOfNodes(); i++)
            if (m.getNode(i).getx() == x && m.getNode(i).gety() == y)
                return i;
        return -1;
    }

    /**
     * Returns the index of the Domain which contains the element e
     */
    private int getDomain(Mesh m, Triangle e) {
        for (int i = 0; i < m.getNbOfDomains(); i++)
            for (int j = 0; j < m.getDomain(i).getNbOfElem(); j++)
                if (m.getDomain(i).getElem(j).getGlobalNb() == e.getGlobalNb())
                    return i;
        return -1; // ne devrait pas arriver...
    }

    /**
     * Returns the index of the element e in the Domain d
     */
    private int indexInDomain(Domain d, Triangle e) {
        for (int i = 0; i < d.getNbOfElem(); i++)
            if (d.getElem(i).getGlobalNb() == e.getGlobalNb())
                return i;
        return -1; // ne devrait pas arriver...
    }

    /**
     * Returns the index of the Border (n1, n2). If it isn't a Border, returns -1
     */
    private int getBorder(Mesh m, Node n1, Node n2) {
        int gn1 = n1.getGlobalNb();
        int gn2 = n2.getGlobalNb();
        for (int i = 0; i < m.getNbOfBorders(); i++) {
            int Bgn1 = m.getBorder(i).getNode1().getGlobalNb();
            int Bgn2 = m.getBorder(i).getNode2().getGlobalNb();
            if ((gn1 == Bgn1 && gn2 == Bgn2) || (gn1 == Bgn2 && gn2 == Bgn1))
                return i;
        }
        return -1;
    }

    /**
     * Returns the index of the MeshBorder e in the Domain Border db
     */
    private int indexInBorderDomain(BorderDomain db, MeshBorder e) {
        for (int i = 0; i < db.getNbOfBorders(); i++)
            if (db.getBorder(i).getGlobalNb() == e.getGlobalNb())
                return i;
        return -1; // ne devrait pas arriver...
    }


    /**
     * Saves the .net and .dat files with a name which contains maxDiff and loads them
     */
    private Mesh saveAndReload(Mesh m, String netName, String datName, double maxDiff) {

        if (isRefinementAllowed) {
            String endName;

            if (maxDiff != 0)
                endName = "_" + "ref" + "_" + maxDiff;
            else
                endName = "_" + "refined";

            Writers writers = new Writers();
            writers.NetWriter(m, netName + endName + ".net");
            writers.DatWriter(m, datName + endName + ".dat");

            m = new Mesh(m.getFctType());

            Readers reader = new Readers();

            reader.readFile(m, netName + endName + ".net", datName + endName + ".dat", false);

            if (!isNewFileRefineSelected) {
                new File(netName + endName + ".net").delete();
                new File(datName + endName + ".dat").delete();
            }

            Renumbering.McKee(m, false);

            return m;
        } else
            return null;

    }

    /**
     * Split the triangle which is indexed with i in four triangles
     */
    private void splitInFour(Mesh m, int i) {
        Triangle t = (Triangle) m.getElem(i);
        Node n0 = t.getNode(0);
        Node n1 = t.getNode(1);
        Node n2 = t.getNode(2);
        Domain d = m.getDomain(getDomain(m, t));
        int iiD = indexInDomain(d, t);
        int B01 = getBorder(m, n0, n1);
        int B02 = getBorder(m, n0, n2);
        int B12 = getBorder(m, n1, n2);

        for (int l = 0; l < t.getNbOfNeigh(); l++)
            if (t.getNeigh(l).getNbOfMarks() != -1)
                t.getNeigh(l).addMark();

        double n0x = n0.getx();
        double n0y = n0.gety();
        double n1x = n1.getx();
        double n1y = n1.gety();
        double n2x = n2.getx();
        double n2y = n2.gety();

        int i01 = getIndex(m, (n0x + n1x) / 2, (n0y + n1y) / 2);
        int i02 = getIndex(m, (n0x + n2x) / 2, (n0y + n2y) / 2);
        int i12 = getIndex(m, (n1x + n2x) / 2, (n1y + n2y) / 2);

        Node n01;
        Node n02;
        Node n12;

        if (i01 == -1) {
            n01 = new Node(node_NB, (n0.getx() + n1.getx()) / 2, (n0.gety() + n1.gety()) / 2);
            node_NB++;
            m.addNode(n01);
            m.addMeshPoint(n01);
        } else
            n01 = m.getNode(i01);

        if (i02 == -1) {
            n02 = new Node(node_NB, (n0.getx() + n2.getx()) / 2, (n0.gety() + n2.gety()) / 2);
            node_NB++;
            m.addNode(n02);
            m.addMeshPoint(n02);
        } else
            n02 = m.getNode(i02);

        if (i12 == -1) {
            n12 = new Node(node_NB, (n1.getx() + n2.getx()) / 2, (n1.gety() + n2.gety()) / 2);
            node_NB++;
            m.addNode(n12);
            m.addMeshPoint(n12);
        } else
            n12 = m.getNode(i12);

        Triangle t0 = new Triangle(t.getGlobalNb(), n01, n02, n12);
        Triangle t1 = new Triangle(elem_NB, n0, n01, n02);
        Triangle t2 = new Triangle(elem_NB + 1, n1, n01, n12);
        Triangle t3 = new Triangle(elem_NB + 2, n2, n02, n12);
        t0.setMarks(0);
        t1.setMarks(0);
        t2.setMarks(0);
        t3.setMarks(0);
        elem_NB += 3;

        m.setElem(i, t0);
        m.addElem(t1);
        m.addElem(t2);
        m.addElem(t3);
        d.setElem(iiD, t0);
        d.addElem(t1);
        d.addElem(t2);
        d.addElem(t3);

        if (B01 != -1) {
            MeshBorder mb = m.getBorder(B01);
            BorderDomain bd = m.getBorderDomain(getBorderDomain(m, mb));
            int iiDB = indexInBorderDomain(bd, mb);
            MeshBorder mb1 = new MeshBorder(mb.getGlobalNb(), n0, n01);
            MeshBorder mb2 = new MeshBorder(border_NB, n01, n1);
            border_NB++;
            MeshBorder b1;
            MeshBorder b2;
            if (mb instanceof Border1) {
                double average = ((n0.getDirichletValue() + n1.getDirichletValue()) / 2);
                b1 = new Border1(mb1, n0.getDirichletValue(), average);
                b2 = new Border1(mb2, average, n1.getDirichletValue());
            } else if (mb instanceof Border2) {
                b1 = new Border2(mb1, ((Border2) mb).geta());
                b2 = new Border2(mb2, ((Border2) mb).geta());
            } else if (mb instanceof Border3) {
                b1 = new Border3(mb1, ((Border3) mb).geta(), ((Border3) mb).getb());
                b2 = new Border3(mb2, ((Border3) mb).geta(), ((Border3) mb).getb());
            } else {
                //System.out.println("erreur : " + mb.getGlobalNb());
                return;
            }
            m.setBorder(B01, b1);
            m.addBorder(b2);
            bd.setBorder(iiDB, b1);
            bd.addBorder(b2);
        }
        if (B02 != -1) {
            MeshBorder mb = m.getBorder(B02);
            BorderDomain bd = m.getBorderDomain(getBorderDomain(m, mb));
            int iiDB = indexInBorderDomain(bd, mb);
            MeshBorder mb1 = new MeshBorder(mb.getGlobalNb(), n0, n02);
            MeshBorder mb2 = new MeshBorder(border_NB, n02, n2);
            border_NB++;
            MeshBorder b1;
            MeshBorder b2;
            if (mb instanceof Border1) {
                double average = ((n0.getDirichletValue() + n2.getDirichletValue()) / 2);
                b1 = new Border1(mb1, n0.getDirichletValue(), average);
                b2 = new Border1(mb2, average, n2.getDirichletValue());
            } else if (mb instanceof Border2) {
                b1 = new Border2(mb1, ((Border2) mb).geta());
                b2 = new Border2(mb2, ((Border2) mb).geta());
            } else { //if (mb instanceof Border3) {
                b1 = new Border3(mb1, ((Border3) mb).geta(), ((Border3) mb).getb());
                b2 = new Border3(mb2, ((Border3) mb).geta(), ((Border3) mb).getb());
            }
            m.setBorder(B02, b1);
            m.addBorder(b2);
            bd.setBorder(iiDB, b1);
            bd.addBorder(b2);
        }
        if (B12 != -1) {
            MeshBorder mb = m.getBorder(B12);
            BorderDomain bd = m.getBorderDomain(getBorderDomain(m, mb));
            int iiDB = indexInBorderDomain(bd, mb);
            MeshBorder mb1 = new MeshBorder(mb.getGlobalNb(), n1, n12);
            MeshBorder mb2 = new MeshBorder(border_NB, n12, n2);
            border_NB++;
            MeshBorder b1;
            MeshBorder b2;
            if (mb instanceof Border1) {
                double average = ((n1.getDirichletValue() + n2.getDirichletValue()) / 2);
                b1 = new Border1(mb1, n1.getDirichletValue(), average);
                b2 = new Border1(mb2, average, n2.getDirichletValue());
            } else if (mb instanceof Border2) {
                b1 = new Border2(mb1, ((Border2) mb).geta());
                b2 = new Border2(mb2, ((Border2) mb).geta());
            } else { //if (mb instanceof Border3) {
                b1 = new Border3(mb1, ((Border3) mb).geta(), ((Border3) mb).getb());
                b2 = new Border3(mb2, ((Border3) mb).geta(), ((Border3) mb).getb());
            }
            m.setBorder(B12, b1);
            m.addBorder(b2);
            bd.setBorder(iiDB, b1);
            bd.addBorder(b2);
        }

    }

    /**
     * Refines the mesh m in order for the difference of temperatures between every neighbour Nodes not to be higher than maxDiff
     */
    public void refine(String netName, String datName) {

        //String netName = Display.frame.LP.getSelectNetPath();
        //String datName = Display.frame.LP.getSelectDatPath();

        //Display.frame.RP.pb.setIndeterminate(true);

        if (isRefinementAllowed) {

            if (maxDiff == 0) {

                for (int i = 0; i < m.getNbOfElem() && isRefinementAllowed; i++)
                    ((Triangle) m.getElem(i)).setMarks(0);

                createNeighbours(m);

                node_NB = m.getNbOfNodes() + 1;
                border_NB = m.getNbOfBorders() + 1;
                elem_NB = m.getNbOfElem() + 1;

                int elemNb = m.getNbOfElem();

                for (int i = 0; i < elemNb && isRefinementAllowed; i++)
                    splitInFour(m, i);

                m = saveAndReload(m, netName, datName, maxDiff);

            } else {

                boolean act = true;

                while (act) {

                    act = false;

                    for (int i = 0; i < m.getNbOfElem() && isRefinementAllowed; i++)
                        ((Triangle) m.getElem(i)).setMarks(0);

                    createNeighbours(m);

                    node_NB = m.getNbOfNodes() + 1;
                    border_NB = m.getNbOfBorders() + 1;
                    elem_NB = m.getNbOfElem() + 1;

                    int elemNb = m.getNbOfElem();
                    for (int i = 0; i < elemNb; i++) {
                        Triangle t = (Triangle) m.getElem(i);
                        Node n0 = t.getNode(0);
                        Node n1 = t.getNode(1);
                        Node n2 = t.getNode(2);
                        double temp0 = n0.getTemperature();
                        double temp1 = n1.getTemperature();
                        double temp2 = n2.getTemperature();
                        double Tdiff01 = Math.abs(temp0 - temp1);
                        double Tdiff02 = Math.abs(temp0 - temp2);
                        double Tdiff12 = Math.abs(temp1 - temp2);

                        if (Tdiff01 > maxDiff || Tdiff02 > maxDiff || Tdiff12 > maxDiff) {
                            act = true;
                            splitInFour(m, i);
                        }
                    }


                    boolean action = true;
                    while (action && isRefinementAllowed) {


                        action = false;
                        elemNb = m.getNbOfElem();
                        for (int i = 0; i < elemNb; i++) {
                            Triangle t = (Triangle) m.getElem(i);
                            if (t.getNbOfMarks() >= 2) {
                                action = true;
                                splitInFour(m, i);
                            }
                        }


                        elemNb = m.getNbOfElem();
                        for (int i = 0; i < elemNb; i++) {
                            Triangle t = (Triangle) m.getElem(i);
                            if (t.getNbOfMarks() == 1) {
                                Node n0 = t.getNode(0);
                                Node n1 = t.getNode(1);
                                Node n2 = t.getNode(2);
                                if (getIndex(m, (n0.getx() + n1.getx()) / 2, (n0.gety() + n1.gety()) / 2) != -1) {
                                    if (cosABC(n0, n2, n1) > 0.7) {
                                        action = true;
                                        splitInFour(m, i);
                                    } else {
                                        int ind = getIndex(m, (n0.getx() + n1.getx()) / 2, (n0.gety() + n1.gety()) / 2);
                                        Triangle t1 = new Triangle(t.getGlobalNb(), n2, n0, m.getNode(ind));
                                        Triangle t2 = new Triangle(elem_NB, n2, n1, m.getNode(ind));
                                        elem_NB++;
                                        m.setElem(i, t1);
                                        m.addElem(t2);
                                        Domain d = m.getDomain(getDomain(m, t));
                                        int iiD = indexInDomain(d, t);
                                        d.setElem(iiD, t1);
                                        d.addElem(t2);
                                        createNeighbours(m);
                                    }
                                } else if (getIndex(m, (n0.getx() + n2.getx()) / 2, (n0.gety() + n2.gety()) / 2) != -1) {
                                    if (cosABC(n0, n1, n2) > 0.7) {
                                        action = true;
                                        splitInFour(m, i);
                                    } else {
                                        int ind = getIndex(m, (n0.getx() + n2.getx()) / 2, (n0.gety() + n2.gety()) / 2);
                                        Triangle t1 = new Triangle(t.getGlobalNb(), n1, n0, m.getNode(ind));
                                        Triangle t2 = new Triangle(elem_NB, n1, n2, m.getNode(ind));
                                        elem_NB++;
                                        m.setElem(i, t1);
                                        m.addElem(t2);
                                        Domain d = m.getDomain(getDomain(m, t));
                                        int iiD = indexInDomain(d, t);
                                        d.setElem(iiD, t1);
                                        d.addElem(t2);
                                        createNeighbours(m);
                                    }
                                } else if (getIndex(m, (n1.getx() + n2.getx()) / 2, (n1.gety() + n2.gety()) / 2) != -1) {
                                    if (cosABC(n1, n0, n2) > 0.7) {
                                        action = true;
                                        splitInFour(m, i);
                                    } else {
                                        int ind = getIndex(m, (n1.getx() + n2.getx()) / 2, (n1.gety() + n2.gety()) / 2);
                                        Triangle t1 = new Triangle(t.getGlobalNb(), n0, n1, m.getNode(ind));
                                        Triangle t2 = new Triangle(elem_NB, n0, n2, m.getNode(ind));
                                        elem_NB++;
                                        m.setElem(i, t1);
                                        m.addElem(t2);
                                        Domain d = m.getDomain(getDomain(m, t));
                                        int iiD = indexInDomain(d, t);
                                        d.setElem(iiD, t1);
                                        d.addElem(t2);
                                        createNeighbours(m);
                                    }
                                }
                            }
                        }

                    }
                    if (act)
                        m = saveAndReload(m, netName, datName, maxDiff);
                }
            }

        }
    }
}
