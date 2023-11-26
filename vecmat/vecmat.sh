#!/bin/bash
#SBATCH -J vecmat
#SBATCH -A kurs00069
#SBATCH -p kurs00069
#SBATCH --reservation=kurs00069
#SBATCH --mail-type=FAIL
#SBATCH --mail-user=paul.hollmann@stud.tu-darmstadt.de
#SBATCH -e /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/logs/err.%j
#SBATCH -o /home/kurse/kurs00069/ph84wuqa/hsi/vecmat/logs/out.%j

#SBATCH --mem-per-cpu=7600
#SBATCH --time=00:15:00
#SBATCH -n 1
#SBATCH -c 2
#SBATCH --exclusive

#SBATCH --gres=gpu:v100:1

 # first remove all modules and load required modules
module purge
module load java
module load cuda


cd /work/home/kurse/kurs00069/ph84wuqa/hsi/vecmat/ || exit

# build the project
./mvn.sh
./mvn.sh assembly:single

nvidia-smi 1>&2

 # call to the parallel program
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 1
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 10
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 1000
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 2000
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 4000
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 8000
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 15000
java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 100000


max=1024

java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 10000 1

for ((i=16; i<=max; i=i+16))
do
    java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 10000 "$i"
done



#java -jar target/vecmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar 1000000


