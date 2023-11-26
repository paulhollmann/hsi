package de.hsi.vecmat;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class FileFinder {

    static String[] paths = {
            "src/main/resources/",
            "./",
    };

    /**
     * Read the contents of the file with the given name, and return
     * it as a string
     *
     * @param fileName The name of the file to read
     * @return The contents of the file
     */
    static String readFile(String fileName)
    {

        String filePath = null;

        for (String str : paths) {
            //System.out.print("Searching for " + System.getProperty("user.dir") + "/" + str + fileName );
            if((new File( str + fileName)).isFile()){
                filePath =  str + fileName;
                //System.out.println(" found");
                break;
            }
            //System.out.println(" not found");
        }

        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(filePath));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while (true)
            {
                line = br.readLine();
                if (line == null)
                {
                    break;
                }
                sb.append(line+"\n");
            }
            return sb.toString();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return "";
        }
        finally
        {
            if (br != null)
            {
                try
                {
                    br.close();
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                }
            }
        }
    }

}
