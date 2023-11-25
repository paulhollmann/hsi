#!/bin/bash
#SBATCH -J vecmat
#SBATCH -A kurs00069
#SBATCH -p kurs00069
#SBATCH --reservation=kurs00069
#SBATCH --mail-type=ERR
#SBATCH --mail-user=paul.hollmann@stud.tu-darmstadt.de
#SBATCH -e /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/logs/err.%j
#SBATCH -o /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/logs/out.%j

#SBATCH --mem-per-cpu=1000
#SBATCH --time=00:30
#SBATCH -n 4

#SBATCH --gres=gpu:v100:4

 # first remove all modules and load required modules
module purge
module load java
module load cuda

rm /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/target/vecmat-0.0.1-SNAPSHOT.jar

/home/kurse/kurs00069/ph84wuqa/hsi/vecmat/mvn.sh assembly:single

 # call to the parallel program
java -jar /work/home/kurse/kurs00069/ph84wuqa/hsi/vecmat/target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar