#!/bin/bash
#SBATCH -J vecmat
#SBATCH -A kurs00069
#SBATCH -p kurs00069
#SBATCH --reservation=kurs00069
#  #SBATCH --mail-type=ALL
#  #SBATCH --mail-user=paul.hollmann@stud.tu-darmstadt.de
#SBATCH -e /home/ph84wuqa/hsi/vecmat/logs/err.%j
#SBATCH -o /home/ph84wuqa/hsi/vecmat/logs/out.%j

#SBATCH --mem-per-cpu=1000
#SBATCH --time=00:30
#SBATCH -n 1

#SBATCH --gres=gpu:v100:4

 # first remove all modules and load required modules
module purge
module load java
module load cuda

/home/ph84wuqa/hsi/vecmat/mvn.sh assembly:single


 # call to the parallel program
srun java -jar /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/target/vecmat-0.0.1-SNAPSHOT.jar