#!/bin/bash

set -ex

cp ./application.yaml ./target/

java -jar ./target/openmldb-feature-platform-0.8-SNAPSHOT.jar
