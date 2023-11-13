#!/bin/bash

M2_HOME=~/mvn
MAVEN_URL=https://mirrors.estointernet.in/apache/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz
MAVEN_ARCHIVE=apache-maven-3.6.3-bin
MAVEN_EXTR=apache-maven-3.6.3

# Check if ~/mvn/ exists
if [ ! -d "$M2_HOME" ]; then
    echo "Downloading Apache Maven..."
    # Download Maven
    wget "$MAVEN_URL"

    # Extract Maven
    tar -xvf "$MAVEN_ARCHIVE".tar.gz

    # Move Maven to ~/mvn
    mv "$MAVEN_EXTR" "$M2_HOME"

    # Clean up downloaded archive
    rm "$MAVEN_ARCHIVE".tar.gz

    echo "Apache Maven installed successfully!"
fi

# Execute mvn with the given parameters
"$M2_HOME"/bin/mvn "$@"