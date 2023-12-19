# to clone do

git reset --hard && git pull && chmod +x build.sh run.sh mvn.sh


# to build do

./build.sh


# to run do or use sbatch

export MPJ_HOME=/work/home/kurse/kurs00069/ph84wuqa/hsi/matmat/lib/mpj-v0_44
export PATH=$MPJ_HOME/bin:$PATH

mpjrun.sh -np 4  out/matmat.jar


