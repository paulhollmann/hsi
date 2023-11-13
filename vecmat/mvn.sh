#!/bin/bash

M2_HOME=~/mvn
MAVEN_VERSION=3.6.3
MAVEN_URL=https://mirrors.estointernet.in/apache/maven/maven-${MAVEN_VERSION}/binaries/apache-maven-${MAVEN_VERSION}-bin.tar.gz
MAVEN_ARCHIVE=apache-maven-${MAVEN_VERSION}-bin.tar.gz

# Check if ~/mvn/ exists
if [ ! -d "$M2_HOME" ]; then
    echo "Downloading Apache Maven..."
    # Download Maven
    wget "$MAVEN_URL"

    # Extract Maven
    tar -xvf "$MAVEN_ARCHIVE"

    # Move Maven to ~/mvn
    mv apache-maven-"$MAVEN_VERSION" "$M2_HOME"

    # Clean up downloaded archive
    rm "$MAVEN_ARCHIVE"

    echo "Apache Maven installed successfully!"
fi

# Execute mvn with the given parameters
"$M2_HOME"/bin/mvn "$@"