#!/bin/bash
#SBATCH -J matmat
#SBATCH -A kurs00069
#SBATCH -p kurs00069
#SBATCH --reservation=kurs00069
#SBATCH --mail-type=FAIL
#SBATCH --mail-user=niklas.beck@stud.tu-darmstadt.de
#SBATCH -e /home/kurse/kurs00069/nb62hoty/hsi/matmat/logs/err.%j
#SBATCH -o /home/kurse/kurs00069/nb62hoty/hsi/matmat/logs/out.%j

#SBATCH --mem-per-cpu=3600
#SBATCH --time=00:15:00
#SBATCH -n 1
#SBATCH -c 32

cd /work/home/kurse/kurs00069/nb62hoty/hsi/matmat/ || exit

./build.sh

module load intel
module load java


export MPJ_HOME=/work/home/kurse/kurs00069/nb62hoty/hsi/matmat/lib/mpj-v0_44
export PATH=$MPJ_HOME/bin:$PATH

srun mpjrun.sh -np 4  out/matmat.jar
