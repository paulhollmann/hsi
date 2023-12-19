#!/bin/bash

rm -R ./lib/mpj-v0_44
rm -R ./target

ml java

./mvn.sh install:install-file -Dfile=lib/mpj-0.44.jar -DgroupId=mpj -DartifactId=mpj -Dversion=0.44 -Dpackaging=jar

./mvn.sh

./mvn.sh assembly:single

tar -xf ./lib/mpj-v0_44.tar.gz --directory ./lib

export MPJ_HOME=./lib/mpj-v0_44
export PATH=$MPJ_HOME/bin:$PATH

mkdir out

mv target/matmat-0.0.1-SNAPSHOT-jar-with-dependencies.jar out/matmat.jar

rm -R ./target

