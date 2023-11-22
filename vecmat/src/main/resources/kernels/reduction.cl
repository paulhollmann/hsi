__kernel void reduce(__global const float *mat,
                     __global const float *vec,
                     __global float *res)
{
    int gid = get_global_id(0);
    int m = 3; //sizeof(vec) / sizeof(float);
    for(int i = 0; i < m; ++i) {
        res[gid] += mat[gid * m + i] * vec[i];
    }
}