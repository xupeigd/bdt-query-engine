#!/usr/bin/env bash
mvn clean && cd query-engine-api && mvn install -U -DskipTests=true && cd ../ && mvn package -U -DskipTests=true