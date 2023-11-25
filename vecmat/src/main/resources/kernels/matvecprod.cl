__kernel void vecmatprod(__global const float *mat,
             __global const float *vec,
             __global float *res,
             const int m)
{
    int gid = get_global_id(0);
    float sum = 0.0f;
    for(int i = 0; i < m; ++i) {
        sum += mat[gid * m + i] * vec[i];
    }
    res[gid] = sum;
}