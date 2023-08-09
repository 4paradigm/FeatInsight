#!/bin/bash

set -ex

docker run -it -p 8888:8888 -v /Users/tobe/code/4pd/feature-platform/docker/application.yaml:/app/application.yaml registry.cn-shenzhen.aliyuncs.com/tobe43/openmldb-feature-platform
