#!/bin/bash

rm -R ./lib/mpj-v0_44

#ml java

./mvn.sh clean compile

./mvn.sh clean package

tar -xf ./lib/mpj-v0_44.tar.gz --directory ./lib

mkdir out

mv target/pcgfem-0.0.1-SNAPSHOT-jar-with-dependencies.jar out/pcgfem.jar

rm -R ./target

