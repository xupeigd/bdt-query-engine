FROM python:3.8.13-bullseye

MAINTAINER Page <xupei@quicksand.com>

RUN pip install metricflow && \
    apt-get update && cd /tmp && \
    wget https://builds.openlogic.com/downloadJDK/openlogic-openjdk/8u332-b09/openlogic-openjdk-8u332-b09-linux-x64-deb.deb && \
    apt-get --fix-broken -y install /tmp/openlogic-openjdk-8u332-b09-linux-x64-deb.deb  && \
    rm /tmp/openlogic-openjdk-8u332-b09-linux-x64-deb.deb

ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64