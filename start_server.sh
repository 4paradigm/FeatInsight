#!/bin/bash

set -ex

cp ./src/main/resources/application.yml ./target/

cd ./target/

java -jar ./featinsight-0.1.0-SNAPSHOT.jar
