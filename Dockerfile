FROM m3base:1.0

MAINTAINER Page <xupei@quicksand.com>

ADD ./target/bdt-query-engine.jar /app.jar

ENTRYPOINT ["nohup","java","-jar","-agentlib:jdwp=transport=dt_socket,address=9101,server=y,suspend=n","/app.jar","&"]