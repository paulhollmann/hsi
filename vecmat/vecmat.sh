#!/bin/bash
#SBATCH -J vecmat
#SBATCH -A kurs00069
#SBATCH -p kurs00069
#SBATCH --reservation=kurs00069
#SBATCH --mail-type=FAIL
#SBATCH --mail-user=paul.hollmann@stud.tu-darmstadt.de
#SBATCH -e /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/logs/err.%j
#SBATCH -o /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/logs/out.%j

#SBATCH --mem-per-cpu=3800
#SBATCH --time=00:05:00
#SBATCH -n 1
#SBATCH -c 8

#SBATCH --gres=gpu:v100:4

 # first remove all modules and load required modules
module purge
module load java
module load cuda


cd /work/home/kurse/kurs00069/ph84wuqa/hsi/vecmat/ || exit

# build the project
./mvn.sh
./mvn.sh assembly:single

 # call to the parallel program
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 1
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 10
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 1000
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 2000
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 4000
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 8000
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 15000
#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 100000


max=1024

for i in $(seq 1 $max)
do
    java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 10000 "$i"
done



#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 1000000


