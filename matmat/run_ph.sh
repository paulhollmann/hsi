#!/bin/bash
#SBATCH -J matmat
#SBATCH -A kurs00069
#SBATCH -p kurs00069
#SBATCH --reservation=kurs00069
#SBATCH --mail-type=FAIL
#SBATCH --mail-user=paul.hollmann@stud.tu-darmstadt.de
#SBATCH -e /home/kurse/kurs00069/ph84wuqa/hsi/matmat/logs/err.%j
#SBATCH -o /home/kurse/kurs00069/ph84wuqa/hsi/matmat/logs/out.%j

#SBATCH --mem-per-cpu=15200
#SBATCH --time=00:15:00
#SBATCH -n 4
#SBATCH -c 1
#SBATCH --gres=gpu:v100:4

cd /work/home/kurse/kurs00069/ph84wuqa/hsi/matmat/ || exit

./build.sh

module load intel
module load java


export MPJ_HOME=/work/home/kurse/kurs00069/ph84wuqa/hsi/matmat/lib/mpj-v0_44
export PATH=$MPJ_HOME/bin:$PATH

srun mpjrun.sh -np 4  out/matmat.jar
