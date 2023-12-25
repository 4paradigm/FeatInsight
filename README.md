# OpenMLDB Feature Platform

* [English Version](README.md)
* [中文版本](README-zh.md)

## Introduction

The OpenMLDB Feature Platform is a sophisticated feature store service, leveraging [OpenMLDB](https://github.com/4paradigm/OpenMLDB) for efficient feature management and orchestration.

## Core Concepts

* Feature: Data obtained through feature extraction from raw data that can be directly used for model training and inference.
* Feature Group: A set of features defined by a single SQL computation statement.
* Data Table: In OpenMLDB, data tables include online storage that supports real-time queries and distributed offline storage.
* Online Scenario: By deploying online feature services, it provides hard real-time online feature extraction interfaces using online data.
* Offline Scenario: Uses distributed computing to process offline data for feature computation and exports sample files needed for machine learning.
* Online-Offline Consistency: Ensuring that the feature results computed in online and offline scenarios are consistent through the same SQL definitions.

## Installation

### Java

Download the jar file.

```
wget https://openmldb.ai/download/feature-platform/openmldb-feature-platform-0.8-SNAPSHOT.jar
```

Prepare the config file which may be named as `application.yml`.

```
server:
  port: 8888
 
openmldb:
  zk_cluster: 127.0.0.1:2181
  zk_path: /openmldb
  apiserver: 127.0.0.1:9080
```

Start the feature platform server.

```
java -jar ./openmldb-feature-platform-0.8-SNAPSHOT.jar
```

### Docker

Prepare the config file `application.yml` and start the docker container.

```
docker run -d -p 8888:8888 -v `pwd`/application.yml:/app/application.yml registry.cn-shenzhen.aliyuncs.com/tobe43/openmldb-feature-platform
```

### Compiling from Source

Clone the source code and build from scratch.

```
git clone https://github.com/4paradigm/feature-platform

cd ./feature-platform/frontend/
npm run build

cd ../
mvn clean package
```

Start the server with local config file.

```
./start_server.sh
```

## Usage

Access the feature platform by navigating to http://127.0.0.1:8888/ using any conventional web browser.

1. Importing Data: Create databases, create data tables, import online data, and import offline data using SQL commands or frontend forms.
2. Creating Features: Define feature groups using SQL statements. The feature platform will use a SQL compiler to analyze the features and create corresponding entities.
3. Offline Scenario: Select the desired features to import. You can choose features from different feature groups simultaneously and use distributed computing to import sample files into local or distributed storage.
4. Online Scenario: Select the desired features to go live. Publish them as an online feature extraction service with one click, and then use an HTTP client to request and return online feature extraction results.
5. SQL Debugging: Execute any online or offline computing SQL statement and view the execution results and logs on the web frontend.