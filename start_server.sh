#!/bin/bash

set -ex

cp ./src/main/resources/application.yml ./target/

cd ./target/

java -jar ./openmldb-feature-platform-0.8-SNAPSHOT.jar
