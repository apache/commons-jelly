# DESCRIPTION:    commons-jelly-build-env
# REQUIRED:       jdk-1_5_0_22-linux-amd64.bin downloaded from oracle before building.

FROM library/ubuntu:12.04

RUN apt-get -qq update && apt-get install -y curl wget pgp subversion

RUN mkdir -p /usr/java

ADD jdk-1_5_0_22-linux-amd64.bin /tmp
ADD docker/answer.txt /tmp
ADD docker/install.sh /tmp

RUN chmod +x /tmp/install.sh && sh /tmp/install.sh

ENV JAVA_HOME=/usr/java
ENV PATH=${PATH}:${JAVA_HOME}/bin:/opt/ant/bin
WORKDIR /root/commons-jelly-1.X