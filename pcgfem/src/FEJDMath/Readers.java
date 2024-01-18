package FEJDMath;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;
import java.util.Vector;


/**
 * Read the net and dat files and save the data in the other objects (domain, elements, nodes, borders, etc.)
 */
public class Readers {

    /**
     * Returns an array of <tt>nbr</tt> double that are read from the String s
     */
    private double[] readDouble(String s, int nbr) throws IOException {
        double[] array = new double[nbr];
        StringTokenizer t = new StringTokenizer(s);
        for (int i = 0; i < nbr; i++) {
            array[i] = Double.parseDouble(t.nextToken());
        }
        return array;
    }

    /**
     * If the point of coordinates (x,y) belongs to the vector mpArray, this function returns its index in the vector. Else, it returns -1
     */
    public int isAtIndex(double x, double y, Vector mpArray) {
        for (int i = 0; i < mpArray.size(); i++)
            if (x == ((MeshPoint) mpArray.get(i)).getx() && y == ((MeshPoint) mpArray.get(i)).gety())
                return i;
        return -1;
    }

    /**
     * Reads the net and dat files and save the data in the other objects (domain, elements, nodes, borders, etc.)
     */
    public void readFile(Mesh m, String fichier1, String fichier2, boolean isApplet) {
        int currentLine = 0;
        String s = "";
        int currentLine2 = 0;
        String s2 = "";

        try {


            BufferedReader in, in2;

            if (isApplet) {
                in = new BufferedReader(new InputStreamReader(new URL(fichier2).openStream()));
                in2 = new BufferedReader(new InputStreamReader(new URL(fichier1).openStream()));
            } else {
                in = new BufferedReader(new FileReader(fichier2));
                in2 = new BufferedReader(new FileReader(fichier1));
            }

            // get rid of blank lines and comments
            do {
                s = in.readLine();
                currentLine++;
            } while (s.startsWith("#") || s.trim().equals(""));

            // Number of domains
            double[] array = readDouble(s, 1);
            int nbDom = (int) array[0];
            m.setNbOfDomains(nbDom);

            // Kxx und Kyy in each domain
            int i = 0;
            while (i < nbDom) {
                s = in.readLine();
                currentLine++;
                if (!s.startsWith("#") && !s.trim().equals("")) {
                    double[] array2 = readDouble(s, 2);
                    m.setDomain(i, new Domain(i + 1, array2[0], array2[1]));
                    i++;
                }
            }

            // get rid of blank lines and comments
            do {
                s = in.readLine();
                currentLine++;
            } while (s.startsWith("#") || s.trim().equals(""));

            // Number of types of different boundary domain (with different types of boundary conditions)
            array = readDouble(s, 1);
            int nbBor = (int) array[0]; // Nb de bords (3 dans Waerme.dat)
            m.setNbOfBorderDomains(nbBor);

            // Number and type of the border in each boundary domain
            i = 0;
            while (i < nbBor) {
                s = in.readLine();
                currentLine++;
                if (!s.startsWith("#") && !s.trim().equals("")) {
                    double[] array2 = readDouble(s, 2);
                    m.setBorderDomain(i, new BorderDomain((int) array2[0], (int) array2[1]));
                    i++;
                }
            }

// On doit maintenant lire l'autre fichier (.net) afin de pouvoir indexer toutes les bordures

            int nbrEl, nbrNo, nbrBord, elem;

            // get rid of blank lines and comments
            do {
                s2 = in2.readLine();
                currentLine2++;
            } while (s2.startsWith("#") || s2.trim().equals(""));

            // Type of elements
            double[] tbl = readDouble(s2, 1);
            m.setElemType((int) tbl[0]);

            Data.computeDataArray(m.getElemType(), m.getFctType());

            // get rid of blank lines and comments
            do {
                s2 = in2.readLine();
                currentLine2++;
            } while (s2.startsWith("#") || s2.trim().equals(""));

            // Number of nodes and number of elements
            tbl = readDouble(s2, 2);
            nbrNo = (int) tbl[0];
            nbrEl = (int) tbl[1];

            m.setNbOfNodes(nbrNo);
            Vector meshPointArray = new Vector();

            // Node's coordinates (and global number)
            i = 0;
            while (i < nbrNo) {
                s2 = in2.readLine();
                currentLine2++;
                if (!s2.startsWith("#") && !s2.trim().equals("")) {
                    double[] array2 = readDouble(s2, 3);
                    m.setNode((int) array2[0] - 1, new Node((int) array2[0], array2[1], array2[2]));
                    i++;
                }
            }

            Elem[][] tempTbl = new Elem[nbDom][];
            for (int k = 0; k < nbDom; k++)
                tempTbl[k] = new Elem[nbrEl];

            int[] nbElem = new int[nbDom];

            // Global Number of the Nodes of each elements (and number of the domain)
            m.setNbOfElem(nbrEl);

            if (m.getFctType() == Data.LINEAR) {

                m.setSize(nbrNo);
                m.setNbOfMeshPoints(nbrNo);
                for (int k = 0; k < nbrNo; k++)
                    m.setMeshPoint(k, m.getNode(k));

                switch (m.getElemType()) {
                    case Data.TRIANGLE:

                        i = 0;
                        while (i < nbrEl) {
                            s2 = in2.readLine();
                            currentLine2++;
                            if (!s2.startsWith("#") && !s2.trim().equals("")) {
                                double[] array2 = readDouble(s2, 5);
                                m.setElem(i, new Triangle(i + 1, m.getNode((int) array2[1] - 1), m.getNode((int) array2[2] - 1), m.getNode((int) array2[3] - 1)));
                                tempTbl[(int) array2[4] - 1][nbElem[(int) array2[4] - 1]] = m.getElem(i);
                                nbElem[(int) array2[4] - 1]++;
                                i++;

                            }
                        }

                        break;


                    case Data.QUADRILATERAL:

                        i = 0;
                        while (i < nbrEl) {
                            s2 = in2.readLine();
                            currentLine2++;
                            if (!s2.startsWith("#") && !s2.trim().equals("")) {
                                double[] array2 = readDouble(s2, 6);
                                m.setElem(i, new Quadrilateral(i + 1, m.getNode((int) array2[1] - 1), m.getNode((int) array2[2] - 1), m.getNode((int) array2[3] - 1), m.getNode((int) array2[4] - 1)));
                                tempTbl[(int) array2[5] - 1][nbElem[(int) array2[5] - 1]] = m.getElem(i);
                                nbElem[(int) array2[5] - 1]++;
                                i++;

                            }
                        }

                        break;

                }
            } else if (m.getFctType() == Data.QUADRATIC) {

                switch (m.getElemType()) {
                    case Data.TRIANGLE:
                        meshPointArray = new Vector(nbrEl * 3);
                        int CurrentGlobalNb = nbrNo + 1;
                        i = 0;
                        while (i < nbrEl) {
                            s2 = in2.readLine();
                            currentLine2++;
                            if (!s2.startsWith("#") && !s2.trim().equals("")) {
                                double[] array2 = readDouble(s2, 5);

                                Node n1 = m.getNode((int) array2[1] - 1);
                                Node n2 = m.getNode((int) array2[2] - 1);
                                Node n3 = m.getNode((int) array2[3] - 1);
                                MeshPoint mp1;
                                MeshPoint mp2;
                                MeshPoint mp3;

                                double index1 = (n1.getx() + n2.getx()) / 2.;
                                double index2 = (n1.gety() + n2.gety()) / 2.;
                                int i12 = isAtIndex(index1, index2, meshPointArray);


                                index1 = (n1.getx() + n3.getx()) / 2.;
                                index2 = (n1.gety() + n3.gety()) / 2.;
                                int i13 = isAtIndex(index1, index2, meshPointArray);

                                index1 = (n2.getx() + n3.getx()) / 2.;
                                index2 = (n2.gety() + n3.gety()) / 2.;
                                int i23 = isAtIndex(index1, index2, meshPointArray);

                                if (i12 == -1) {

                                    index1 = (n1.getx() + n2.getx()) / 2.;
                                    index2 = (n1.gety() + n2.gety()) / 2.;
                                    mp1 = new MeshPoint(CurrentGlobalNb, index1, index2);
                                    meshPointArray.add(mp1);
                                    CurrentGlobalNb++;
                                } else
                                    mp1 = (MeshPoint) meshPointArray.get(i12);

                                if (i13 == -1) {

                                    index1 = (n1.getx() + n3.getx()) / 2.;
                                    index2 = (n1.gety() + n3.gety()) / 2.;
                                    mp2 = new MeshPoint(CurrentGlobalNb, index1, index2);
                                    meshPointArray.add(mp2);
                                    CurrentGlobalNb++;
                                } else
                                    mp2 = (MeshPoint) meshPointArray.get(i13);

                                if (i23 == -1) {
                                    index1 = (n2.getx() + n3.getx()) / 2.;
                                    index2 = (n2.gety() + n3.gety()) / 2.;
                                    mp3 = new MeshPoint(CurrentGlobalNb, index1, index2);
                                    meshPointArray.add(mp3);
                                    CurrentGlobalNb++;
                                } else
                                    mp3 = (MeshPoint) meshPointArray.get(i23);

                                m.setElem(i, new Triangle(i + 1, n1, n2, n3, new MeshPoint[]{mp1, mp3, mp2}));
                                tempTbl[(int) array2[4] - 1][nbElem[(int) array2[4] - 1]] = m.getElem(i);
                                nbElem[(int) array2[4] - 1]++;
                                i++;

                            }
                        }
                        m.setSize(CurrentGlobalNb - 1);
                        m.setNbOfMeshPoints(CurrentGlobalNb - 1);

                        for (int k = 0; k < nbrNo; k++)
                            m.setMeshPoint(k, m.getNode(k));

                        for (int k = 0; k < meshPointArray.size(); k++)
                            m.setMeshPoint(nbrNo + k, (MeshPoint) meshPointArray.get(k));

                        break;


                    case Data.QUADRILATERAL:

                        meshPointArray = new Vector(nbrEl * 3);
                        CurrentGlobalNb = nbrNo + 1;
                        i = 0;
                        while (i < nbrEl) {
                            s2 = in2.readLine();
                            currentLine2++;
                            if (!s2.startsWith("#") && !s2.trim().equals("")) {
                                double[] array2 = readDouble(s2, 6);

                                Node n1 = m.getNode((int) array2[1] - 1);
                                Node n2 = m.getNode((int) array2[2] - 1);
                                Node n3 = m.getNode((int) array2[3] - 1);
                                Node n4 = m.getNode((int) array2[4] - 1);
                                MeshPoint mp1;
                                MeshPoint mp2;
                                MeshPoint mp3;
                                MeshPoint mp4;
                                MeshPoint mp5;

                                double index1 = (n1.getx() + n2.getx()) / 2.;
                                double index2 = (n1.gety() + n2.gety()) / 2.;
                                int i12 = isAtIndex(index1, index2, meshPointArray);

                                index1 = (n2.getx() + n3.getx()) / 2.;
                                index2 = (n2.gety() + n3.gety()) / 2.;
                                int i23 = isAtIndex(index1, index2, meshPointArray);

                                index1 = (n3.getx() + n4.getx()) / 2.;
                                index2 = (n3.gety() + n4.gety()) / 2.;
                                int i34 = isAtIndex(index1, index2, meshPointArray);

                                index1 = (n1.getx() + n4.getx()) / 2.;
                                index2 = (n1.gety() + n4.gety()) / 2.;
                                int i14 = isAtIndex(index1, index2, meshPointArray);

                                if (i12 == -1) {

                                    index1 = (n1.getx() + n2.getx()) / 2.;
                                    index2 = (n1.gety() + n2.gety()) / 2.;
                                    mp1 = new MeshPoint(CurrentGlobalNb, index1, index2);
                                    meshPointArray.add(mp1);
                                    CurrentGlobalNb++;
                                } else
                                    mp1 = (MeshPoint) meshPointArray.get(i12);

                                if (i23 == -1) {
                                    index1 = (n2.getx() + n3.getx()) / 2.;
                                    index2 = (n2.gety() + n3.gety()) / 2.;
                                    mp2 = new MeshPoint(CurrentGlobalNb, index1, index2);
                                    meshPointArray.add(mp2);
                                    CurrentGlobalNb++;
                                } else
                                    mp2 = (MeshPoint) meshPointArray.get(i23);

                                if (i34 == -1) {
                                    index1 = (n3.getx() + n4.getx()) / 2.;
                                    index2 = (n3.gety() + n4.gety()) / 2.;
                                    mp3 = new MeshPoint(CurrentGlobalNb, index1, index2);
                                    meshPointArray.add(mp3);
                                    CurrentGlobalNb++;
                                } else
                                    mp3 = (MeshPoint) meshPointArray.get(i34);

                                if (i14 == -1) {
                                    index1 = (n4.getx() + n1.getx()) / 2.;
                                    index2 = (n4.gety() + n1.gety()) / 2.;
                                    mp4 = new MeshPoint(CurrentGlobalNb, index1, index2);
                                    meshPointArray.add(mp4);
                                    CurrentGlobalNb++;
                                } else
                                    mp4 = (MeshPoint) meshPointArray.get(i14);


                                double tindex11 = (n4.getx() + n1.getx()) / 2.;
                                double tindex12 = (n4.gety() + n1.gety()) / 2.;
                                double tindex21 = (n2.getx() + n3.getx()) / 2.;
                                double tindex22 = (n2.gety() + n3.gety()) / 2.;

                                mp5 = new MeshPoint(CurrentGlobalNb, (tindex11 + tindex21) / 2., (tindex12 + tindex22) / 2.);
                                meshPointArray.add(mp5);
                                CurrentGlobalNb++;

                                m.setElem(i, new Quadrilateral(i + 1, n1, n2, n3, n4, new MeshPoint[]{mp1, mp2, mp3, mp4, mp5}));
                                tempTbl[(int) array2[5] - 1][nbElem[(int) array2[5] - 1]] = m.getElem(i);
                                nbElem[(int) array2[5] - 1]++;
                                i++;

                            }
                        }
                        m.setSize(CurrentGlobalNb - 1);
                        m.setNbOfMeshPoints(CurrentGlobalNb - 1);

                        for (int k = 0; k < nbrNo; k++)
                            m.setMeshPoint(k, m.getNode(k));

                        for (int k = 0; k < meshPointArray.size(); k++)
                            m.setMeshPoint(nbrNo + k, (MeshPoint) meshPointArray.get(k));

                        break;
                }
            }

            for (int k = 0; k < nbDom; k++) {
                m.getDomain(k).setNbOfElem(nbElem[k]);
                for (int l = 0; l < nbElem[k]; l++) {
                    m.getDomain(k).setElem(l, tempTbl[k][l]);
                }
            }

            // get rid of blank lines and comments
            do {
                s2 = in2.readLine();
                currentLine2++;
            } while (s2.startsWith("#") || s2.trim().equals(""));

            // Number of boundary borders
            tbl = readDouble(s2, 1);
            nbrBord = (int) tbl[0];
            m.setNbOfBorders(nbrBord);

            // Startnode and endnode of the boundary borders (and global number)
            i = 0;
            while (i < nbrBord) {
                s2 = in2.readLine();
                currentLine2++;
                if (!s2.startsWith("#") && !s2.trim().equals("")) {
                    double[] array2 = readDouble(s2, 3);
                    Node n1 = m.getNode((int) array2[1] - 1);
                    Node n2 = m.getNode((int) array2[2] - 1);

                    if (m.getFctType() == Data.QUADRATIC) {

                        int ind = isAtIndex((n1.getx() + n2.getx()) / 2., (n1.gety() + n2.gety()) / 2., meshPointArray);
                        if (ind != -1) {
                            MeshPoint mp = (MeshPoint) meshPointArray.get(ind);
                            m.setBorder(i, new MeshBorder(i + 1, n1, n2, new MeshPoint[]{mp}));
                        }

                    } else if (m.getFctType() == Data.LINEAR) {
                        m.setBorder(i, new MeshBorder(i + 1, n1, n2));
                    }

                    i++;
                }
            }


//On revient au fichier .dat :


            // Global number and data (coef a (and b)) of the borders for each border domain
            for (int j = 0; j < nbBor; j++) {
                i = 0;
                while (i < m.getBorderDomain(j).getNbOfBorders()) {
                    s = in.readLine();
                    currentLine++;
                    if (!s.startsWith("#") && !s.trim().equals("")) {
                        double[] array2;
                        switch (m.getBorderDomain(j).getType()) {
                            case 1:
                                try {
                                    array2 = readDouble(s, 3);
                                } catch (NoSuchElementException e) {
                                    double[] tempArray = readDouble(s, 2);
                                    array2 = new double[3];
                                    array2[0] = tempArray[0];
                                    array2[1] = tempArray[1];
                                    array2[2] = tempArray[1];
                                }
                                Border1 b1 = new Border1(m.getBorder((int) array2[0] - 1), array2[1], array2[2]);
                                m.setBorder((int) array2[0] - 1, b1);
                                m.getBorderDomain(j).setBorder(i, b1);

                                if (m.getFctType() == Data.QUADRATIC) {
                                    MeshBorder mb = m.getBorder((int) array2[0] - 1);
                                    mb.getMeshPoint(2).setDirichlet(true);
                                    mb.getMeshPoint(2).setDirichletValue((array2[1] + array2[2]) / 2.);
                                }

                                break;
                            case 2:
                                array2 = readDouble(s, 2);
                                Border2 b2 = new Border2(m.getBorder((int) array2[0] - 1), array2[1]);
                                m.setBorder((int) array2[0] - 1, b2);
                                m.getBorderDomain(j).setBorder(i, b2);
                                break;
                            case 3:
                                array2 = readDouble(s, 3);
                                Border3 b3 = new Border3(m.getBorder((int) array2[0] - 1), array2[1], array2[2]);
                                m.setBorder((int) array2[0] - 1, b3);
                                m.getBorderDomain(j).setBorder(i, b3);
                                break;
                        }
                        i++;

                    }
                }
            }


            Data.setNbOfFct(nbDom);
            // get rid of blank lines and comments
            do {
                s2 = in.readLine();
                currentLine++;
            } while (s2.startsWith("#") || s2.trim().equals(""));

            Data.setFunction(0, s2);
            String firstFct = s2;

            for (int j = 1; j < nbDom; j++) {
                do {
                    s2 = in.readLine();
                    currentLine++;
                } while (s2 != null && (s2.startsWith("#") || s2.trim().equals("")));

                if (s2 != null)
                    Data.setFunction(j, s2);
                else
                    Data.setFunction(j, firstFct);
            }

            in.close();
            in2.close();
        } catch (IOException e) {
            System.err.println("UnknownFile " + fichier1 + " or " + fichier2);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Incompatible Files Line: " + currentLine + " in " + fichier1 + " or Line: " + currentLine2 + " in " + fichier2);
        } catch (Exception e) {
            System.err.println("Error in Readers");
            System.err.println("parsing " + fichier1 + ":" + currentLine + ":" + s);
            System.err.println("     or " + fichier2 + ":" + currentLine + ":" + s2);
        }
    }

}
