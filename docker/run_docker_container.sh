#!/bin/bash

set -ex

docker run -it -p 8888:8888 -v /Users/tobe/code/4pd/feature-platform/docker/application.yml:/app/application.yml registry.cn-shenzhen.aliyuncs.com/tobe43/openmldb-feature-platform
