package de.hsi.vecmat;

import static de.hsi.vecmat.FileFinder.readFile;
import static org.jocl.CL.*;

import java.util.Random;

import org.jocl.*;

public class Main {

    public static void main(String[] args)
    {
        //System.out.println("###########################################");
        //System.out.println();

        int m = 10000;
        int n = -1;

        if(args.length >= 1) {
            m = Integer.parseInt(args[0]);
        }
        if(args.length >= 2) {
            n = Integer.parseInt(args[1]);
        }
        if(m > (int) Math.sqrt(Integer.MAX_VALUE)){
            m = (int) Math.sqrt(Integer.MAX_VALUE);
            System.out.println("Max array size " + Integer.MAX_VALUE + " elements, so about " + (int) Math.sqrt(Integer.MAX_VALUE) + "*" + (int) Math.sqrt(Integer.MAX_VALUE) + "-> m=" +m);
        }
        long max_floats = Runtime.getRuntime().maxMemory() / Float.BYTES;
        if(1.2 * m * m > max_floats){
            m = (int) (Math.sqrt(max_floats) / 1.2);
            System.out.println("Max mem " + Runtime.getRuntime().maxMemory() / (1024 * 1024) + " MB, so about " + max_floats + "floats -> m=" +m );
        }
        long[] global_work_size = new long[]{m};
        long[] local_work_size = new long[]{n};
        if(n>0){
            global_work_size = new long[]{m%n == 0 ? m : (m/n + 1) * n};
        }
        //System.out.println("Chosen problem size: m=" + m + " with in local_work_size=" + (n >0 ? n : "auto") + " and global_work_size=" + global_work_size[0]);

        //System.out.println();

        Random r = new Random(69L);
        var mat = new float[m*m];
        var vec = new float[m];
        float[] result_device = new float[m];

        for(int i = 0; i < m; i++)
        {
            vec[i] = r.nextFloat();
            for(int j = 0; j < m; j++)
            {
                mat[i*m + j] = r.nextFloat();
            }
        }


        //System.out.println("-----Host-----");
        long cpu_start = System.nanoTime();
        float[] result_cpu = getMatVecProd(mat, vec);
        long cpu_end = System.nanoTime();
        double h_time = (double)(cpu_end-cpu_start) / 1e6;
        //System.out.printf("completed in %f ms %n", h_time);


        //System.out.println("-----Device-----");

        final int platformIndex = 0;
        final long deviceType = CL_DEVICE_TYPE_GPU;
        final int deviceIndex = 0;

        // Enable exceptions and subsequently omit error checks in this sample
        CL.setExceptionsEnabled(false); //TODO DISABLE ERROR LOGGING

        // Obtain the number of platforms
        int[] numPlatformsArray = new int[1];
        clGetPlatformIDs(0, null, numPlatformsArray);
        int numPlatforms = numPlatformsArray[0];

        // Obtain a platform ID
        cl_platform_id[] platforms = new cl_platform_id[numPlatforms];
        clGetPlatformIDs(platforms.length, platforms, null);
        cl_platform_id platform = platforms[platformIndex];

        // Initialize the context properties
        cl_context_properties contextProperties = new cl_context_properties();
        contextProperties.addProperty(CL_CONTEXT_PLATFORM, platform);

        // Obtain the number of devices for the platform
        int[] numDevicesArray = new int[1];
        clGetDeviceIDs(platform, deviceType, 0, null, numDevicesArray);
        int numDevices = numDevicesArray[0];

        // Obtain a device ID
        cl_device_id[] devices = new cl_device_id[numDevices];
        clGetDeviceIDs(platform, deviceType, numDevices, devices, null);
        cl_device_id device = devices[deviceIndex];

        {
            int[] buffer = {0};
            clGetDeviceInfo(device, CL_DEVICE_MAX_WORK_GROUP_SIZE, Sizeof.size_t, Pointer.to(buffer), null);
            int maxWorkGroupSize = buffer[0];
            //System.out.printf("CL_DEVICE_MAX_WORK_GROUP_SIZE:\t\t%d\n", maxWorkGroupSize);
            if(n > maxWorkGroupSize) n = maxWorkGroupSize;
        }

        // Create a context for the selected device
        cl_context context = clCreateContext(
                contextProperties, 1, new cl_device_id[]{device},
                null, null, null);

        // Create the program from the source code
        String programSource = readFile("kernels/matvecprod.cl");
        cl_program program = clCreateProgramWithSource(context,
                1, new String[]{ programSource }, null, null);

        // Build the program
        clBuildProgram(program, 0, null, null, null, null);

        // Create the kernel
        cl_kernel kernel = clCreateKernel(program, "vecmatprod", null);

        // Create a command-queue for the selected device
        cl_queue_properties properties = new cl_queue_properties();
        properties.addProperty(CL_QUEUE_PROPERTIES, CL_QUEUE_PROFILING_ENABLE);
        cl_command_queue commandQueue = clCreateCommandQueueWithProperties(
                context, device, properties, null);


        long before_device = System.nanoTime();


        // Allocate the memory objects for the input- and output data
        cl_mem inputMemMat = clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * mat.length, Pointer.to(mat), null);
        cl_mem inputMemVec= clCreateBuffer(context,
                CL_MEM_READ_ONLY | CL_MEM_COPY_HOST_PTR,
                (long) Sizeof.cl_float * vec.length, Pointer.to(vec), null);

        cl_mem outputMemVec = clCreateBuffer(context,
                CL_MEM_READ_WRITE,
                (long) Sizeof.cl_float * m, null, null);



        int a = 0;
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(inputMemMat));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(inputMemVec));
        clSetKernelArg(kernel, a++, Sizeof.cl_mem, Pointer.to(outputMemVec));
        clSetKernelArg(kernel, a++, Sizeof.cl_int, Pointer.to(new int[]{m}));


        cl_event[] events = new cl_event[] { new cl_event() };


        // Execute the kernel
        //long before_kernel = System.nanoTime();
        clEnqueueNDRangeKernel(commandQueue, kernel, 1, null,
                global_work_size, n <= 0 ? null: local_work_size,
                0, null, events[0]);


        //long after_kernel = System.nanoTime();

        // Read the output data
        clEnqueueReadBuffer(commandQueue, outputMemVec, CL_TRUE, 0,
                (long) m * Sizeof.cl_float, Pointer.to(result_device),
                0, null, null);
        long after_device = System.nanoTime();
        clWaitForEvents(1, events); // JOCL: wait for the event!


        double kernelTime = compute_event_time(events[0]);
        double deviceTime = (double)(after_device-before_device) / 1e6;

        System.out.println("m="+m + ", gws=" + global_work_size[0] + ", lws="+ (n >0 ? n : "auto") + ", dt="+ deviceTime +", kt=" + kernelTime + ", ht=" + h_time);


        // Release memory objects
        clReleaseEvent(events[0]);
        clReleaseMemObject(inputMemVec);
        clReleaseMemObject(inputMemMat);
        clReleaseMemObject(outputMemVec);
        clReleaseKernel(kernel);
        clReleaseProgram(program);
        clReleaseCommandQueue(commandQueue);
        clReleaseContext(context);



        // Verify the result
        //System.out.println();
        //System.out.println();
        verify(result_cpu, result_device, 5e-3f);
    }

    public static boolean verify(float[] vec_a, float[] vec_b, float diff){
        boolean good = vec_a.length == vec_b.length;
        int count = 0;
        for (int i = 0; i < vec_a.length; i++) {
            if(Math.abs(vec_a[i] - vec_b[i]) > diff) {
                good = false;
                System.out.println("diff at i=" + i +" of " + (vec_a[i] - vec_b[i]) );
                count++;
                if(count > 5) break;
            }
        }
        //System.out.println("Vectors of length " + vec_b.length + " match" + (good ? "":" not") + " to " + diff);
        return good;
    }

    public static double compute_event_time(cl_event event) {
        long before[] = new long[1];
        long after[] = new long[1];
        clGetEventProfilingInfo(
                event, CL_PROFILING_COMMAND_START,
                Sizeof.cl_ulong, Pointer.to(before), null);
        clGetEventProfilingInfo(
                event, CL_PROFILING_COMMAND_END,
                Sizeof.cl_ulong, Pointer.to(after), null);
        return (double)(after[0]-before[0]) / 1e6;
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
