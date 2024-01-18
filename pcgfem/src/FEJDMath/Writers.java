package FEJDMath;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * This class is used to save the data in the .net and .dat files
 */
public class Writers {

    /**
     * Writes the net data of the mesh m in the .net file NetName
     */
    public void NetWriter(Mesh m, String NetName) {

        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(NetName));

            out.println("# " + NetName);
            out.println("# =========================================================");
            out.println("#");

            out.println("# elemType");
            out.println(m.getElemType());
            out.println("#");

            out.println("# nbNodeElem");
            out.println(m.getNbOfNodes() + "  " + m.getNbOfElem());
            out.println("#");

            out.println("# nodeCoord");
            for (int i = 0; i < m.getNbOfNodes(); i++)
                out.println("" + m.getNode(i).getGlobalNb() + "  " + m.getNode(i).getx() + " " + m.getNode(i).gety());

            out.println("#");
            out.println("# tabElem");
            for (int k = 0; k < m.getNbOfDomains(); k++)
                for (int j = 0; j < m.getDomain(k).getNbOfElem(); j++) {
                    Elem el = m.getDomain(k).getElem(j);
                    int gn0 = el.getNode(0).getGlobalNb();
                    int gn1 = el.getNode(1).getGlobalNb();
                    int gn2 = el.getNode(2).getGlobalNb();

                    out.println("" + el.getGlobalNb() + "  " + gn0 + " " + gn1 + " " + gn2 + " " + (k + 1));
                }
            out.println("#");

            out.println("# nbBound");
            out.println(m.getNbOfBorders());
            out.println("#");

            out.println("# tabBound");
            for (int k = 0; k < m.getNbOfBorders(); k++) {
                int gn1 = m.getBorder(k).getNode1().getGlobalNb();
                int gn2 = m.getBorder(k).getNode2().getGlobalNb();
                out.println("" + m.getBorder(k).getGlobalNb() + "  " + gn1 + " " + gn2);
            }

            out.close();
        } catch (IOException e) {
            System.err.println("UnableToSave");
        }

    }

    /**
     * Writes the dat data of the mesh m in the .dat file DatName
     */
    public void DatWriter(Mesh m, String DatName) {

        try {
            PrintWriter out = new PrintWriter(new FileOutputStream(DatName));

            out.println("# datTitle");
            out.println("# =========================================================");
            out.println("#");

            out.println("# DomNb");
            out.println(m.getNbOfDomains());
            out.println("#");

            for (int i = 0; i < m.getNbOfDomains(); i++) {
                out.println("# KxxKyy" + " " + (i + 1));
                out.println(m.getDomain(i).getKxx() + "  " + m.getDomain(i).getKyy());
                out.println("#");
            }

            out.println("# BCNb");
            out.println(m.getNbOfBorderDomains());
            out.println("#");

            for (int i = 0; i < m.getNbOfBorderDomains(); i++) {
                out.println("# BordersNb_Type" + " " + (i + 1));
                BorderDomain bd = m.getBorderDomain(i);
                out.println(bd.getNbOfBorders() + "  " + bd.getType());
            }
            out.println("#");

            for (int i = 0; i < m.getNbOfBorderDomains(); i++) {
                out.println("# GNb_Data" + " " + (i + 1));
                BorderDomain bd = m.getBorderDomain(i);
                if (bd.getType() == 1)
                    for (int j = 0; j < bd.getNbOfBorders(); j++)
                        out.println(bd.getBorder(j).getGlobalNb() + "  " + bd.getBorder(j).getNode1().getDirichletValue() + " " + bd.getBorder(j).getNode2().getDirichletValue());
                else if (bd.getType() == 2)
                    for (int j = 0; j < bd.getNbOfBorders(); j++)
                        out.println(bd.getBorder(j).getGlobalNb() + "  " + ((Border2) bd.getBorder(j)).geta());
                else if (bd.getType() == 3)
                    for (int j = 0; j < bd.getNbOfBorders(); j++)
                        out.println(bd.getBorder(j).getGlobalNb() + "  " + ((Border3) bd.getBorder(j)).geta() + " " + ((Border3) bd.getBorder(j)).getb());
                out.println("#");
            }


            for (int i = 0; i < m.getNbOfDomains(); i++) {
                out.println("# Function" + " " + i);
                out.println(Data.getFunction(i));
            }


            out.close();
        } catch (IOException e) {
            System.err.println("UnableToSave");
        }

    }


}
