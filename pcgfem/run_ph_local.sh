#!/bin/bash

cd /mnt/c/Users/Hollmann/git/hsi/pcgfem || exit

./build.sh

export MPJ_HOME=/mnt/c/Users/Hollmann/git/hsi/pcgfem/lib/mpj-v0_44
export PATH=$MPJ_HOME/bin:$PATH

mpjrun.sh -np 2  out/pcgfem.jar