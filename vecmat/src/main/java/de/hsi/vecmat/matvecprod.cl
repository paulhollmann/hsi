__kernel void vecmat(__global const float *mat,"+
             __global const float *vec,
             __global float *res,
             __global int m)
{
    int gid = get_global_id(0);
    res[gid] = 0;
    for(int i = 0; i < m; ++i) {
        res[gid] += mat[gid * m + i] * vec[i];
    }
}