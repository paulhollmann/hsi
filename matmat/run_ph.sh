#!/bin/bash
#SBATCH -J matmat
#SBATCH -A kurs00069
#SBATCH -p kurs00069
#SBATCH --reservation=kurs00069
#SBATCH --mail-type=FAIL
#SBATCH --mail-user=paul.hollmann@stud.tu-darmstadt.de
#SBATCH -e /home/kurse/kurs00069/ph84wuqa/hsi/matmat/logs/err.%j
#SBATCH -o /home/kurse/kurs00069/ph84wuqa/hsi/matmat/logs/out.%j

#SBATCH --mem-per-cpu=3600
#SBATCH --time=00:32:00
#SBATCH -n 1
#SBATCH -c 72


cd /work/home/kurse/kurs00069/ph84wuqa/hsi/matmat/ || exit

./build.sh

module load intel
module load java

logfile="/work/home/kurse/kurs00069/ph84wuqa/hsi/matmat/logs/"

export MPJ_HOME=/work/home/kurse/kurs00069/ph84wuqa/hsi/matmat/lib/mpj-v0_44
export PATH=$MPJ_HOME/bin:$PATH

for i in {1..8}; do
    for p in {1..8}; do
        d=$((250 * i))
        np=$((p * p))
        filename="${logfile}perf4_i_${i}.txt"
        srun mpjrun.sh -np "$np"  out/matmat.jar "$d" "$filename"
    done
done


filename="${logfile}perf4_n_512.txt"
srun mpjrun.sh -np "$((1*1))" out/matmat.jar "$((512 / 1))"  "$filename"
srun mpjrun.sh -np "$((2*2))" out/matmat.jar "$((512 / 2))"  "$filename"
srun mpjrun.sh -np "$((4*4))" out/matmat.jar "$((512 / 4))"  "$filename"
srun mpjrun.sh -np "$((8*8))" out/matmat.jar "$((512 / 8))"  "$filename"


