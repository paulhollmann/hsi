package de.hsi.vecmat;

import static org.jocl.CL.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.jocl.*;
import org.jocl.samples.JOCLDeviceQuery;

public class Main {

    private static cl_context context;
    private static cl_command_queue commandQueue;
    private static cl_program program;
    private static cl_kernel kernel;

    public static void main(String[] args)
    {
        int m = 3;
        Random r = new Random();
        var mat = new float[m*m];
        var vec = new float[m];

        for(int i = 0; i < m; i++)
        {
            vec[i] = 100* r.nextFloat();
            for(int j = 0; j < m; j++)
            {
                mat[i*m + j] = 100 * r.nextFloat();
            }
        }

        System.out.println("-----Host-----");

        long start = System.currentTimeMillis();
        var result = getMatVecProd(mat, vec);
        long end = System.currentTimeMillis();
        System.out.printf("Matrix-Vektor-Produkt completed in %dms%n", end - start);

        System.out.println(result[0]);
        System.out.println(result[1]);
        System.out.println(result[2]);

        System.out.println("-----Kernel-----");

        int localWorkSize = 128;
        int numWorkGroups = 64;
        float[] outputArray = new float[m];

        // Allocate the memory objects for the input- and output data
        cl_mem inputMem = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * mat.length + Sizeof.cl_float * vec.length, Pointer.to(mat), null); // here there are two calls to createBuffer needed or how to separate the buffer into mat and vec???
        cl_mem outputMem = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                Sizeof.cl_float * m, Pointer.to(outputArray), null);

        // Create the program from the source code
        String programSource = readFile("reduction.cl");
        program = clCreateProgramWithSource(context,
                1, new String[]{ programSource }, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        kernel = clCreateKernel(program, "reduce", null);

        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(inputMem));
        clSetKernelArg(kernel, a++, Sizeof.cl_float * localWorkSize, null); // two different inputMems?
        clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{m})); // why array ???
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(outputMem));

        // Compute the number of work groups and the global work size
        long globalWorkSize = numWorkGroups * localWorkSize;

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                new long[]{ globalWorkSize }, new long[]{ localWorkSize},
                0, null, null);


        // Read the output data
        clEnqueueReadBuffer(commandQueue, outputMem, CL_TRUE, 0,
                numWorkGroups * Sizeof.cl_float, Pointer.to(outputArray),
                0, null, null);


        // Release memory objects
        clReleaseMemObject(inputMem);
        clReleaseMemObject(outputMem);

        JOCLDeviceQuery.main(args);
    }

    /* Size of double mat needs to be square double size(vec)
     */
    public static float[] getMatVecProd(float [] mat, float[] vec){
        assert Math.sqrt(mat.length) == vec.length;
        var result = new float[vec.length];
        for (int i = 0; i < vec.length; i++) {
            for (int j = 0; j < vec.length; j++) {
                result[i]+= mat[i * vec.length + j] * vec[j];
            }
        }
        return result;
    }

    /**
     * Read the contents of the file with the given name, and return
     * it as a string
     *
     * @param fileName The name of the file to read
     * @return The contents of the file
     */
    private static String readFile(String fileName)
    {
        BufferedReader br = null;
        try
        {
            br = new BufferedReader(new FileReader(fileName));
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
