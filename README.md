# OpenMLDB Feature Platform

## Introduction

OpenMLDB Feature Platform is the feature store service which is based on [OpenMLDB](https://github.com/4paradigm/OpenMLDB).

## Install

### Use Docker

Prepare a spring boot config file like this.

```
server:
  port: 8888
 
openmldb:
  zk_cluster: 10.120.2.40:2181
  zk_path: /openmldb
  apiserver: 10.120.2.40:9080
```

Start official docker container and mount the config file.

```
docker run -d -p 8888:8888 -v `pwd`/application.yml:/app/application.yml registry.cn-shenzhen.aliyuncs.com/tobe43/openmldb-feature-platform
```

### Build from scratch

Clone the source code and build frontend.

```
git clone https://github.com/4paradigm/feature-platform

cd ./feature-platform/frontend/
npm run build
```

Build the backend with frontend resources.

```
cd ../
mvn clean package -DskipTests=true -Dscalatest.skip=true -Dwagon.skip=true -Dmaven.test.skip=true
```

Start the server with local config file.

```
./start_server.sh
```

## Usage

Go to http://localhost:8888/ with your web browser.

## Development

### Develop Frontend

We can build the frontend without refreshing or restarting the service when editing source code.

```
cd ./feature-platform/frontend/

npm run dev
```

Then we can go to http://127.0.0.1:5173/ and it will update the frontend once the source code chagnes.

