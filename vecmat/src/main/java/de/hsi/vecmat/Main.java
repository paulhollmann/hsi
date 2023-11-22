package de.hsi.vecmat;

import static de.hsi.vecmat.FileFinder.readFile;
import static org.jocl.CL.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

import org.jocl.*;
import org.jocl.samples.JOCLDeviceQuery;

public class Main {

    public static void main(String[] args)
    {
        int m = 3;
        Random r = new Random();
        var mat = new float[m*m];
        var vec = new float[m];
        float[] outputArray = new float[m];

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

        System.out.println(result[0] + " " + result[1] + " " + result[2] + " ");


        System.out.println("-----Kernel-----");

        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_ALL;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(true); //TODO DISABLE ERROR LOGGING

        // Obtain the number of platforms
        int numPlatformsArray[] = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id platforms[] = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int numDevicesArray[] = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id devices[] = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        // Create a context for the selected device
        cl_context context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(
                context, device, properties, null);



        // Allocate the memory objects for the input- and output data
        cl_mem inputMemVec = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * vec.length, Pointer.to(vec), null);
        cl_mem inputMemMat= clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * mat.length, Pointer.to(mat), null);

        cl_mem outputMemVec = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                Sizeof.cl_float * m, null, null);

        // Create the program from the source code
        String programSource = readFile("kernels/reduction.cl");
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{ programSource }, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "reduce", null);

        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(inputMemMat));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(inputMemVec));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(outputMemVec));

        long[] global_work_size = new long[]{m};
        long[] local_work_size = new long[]{1};

        // Execute the kernel
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, local_work_size,
                0, null, null);


        // Read the output data
        clEnqueueReadBuffer(commandQueue, outputMemVec, CL_TRUE, 0,
                m * Sizeof.cl_float, Pointer.to(outputArray),
                0, null, null);


        // Release memory objects
        clReleaseMemObject(inputMemVec);
        clReleaseMemObject(inputMemMat);
        clReleaseMemObject(outputMemVec);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);

        // Verify the result
        System.out.println(outputArray[0] + " " + outputArray[1] + " " +outputArray[2] );

        //JOCLDeviceQuery.main(args);
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

}
