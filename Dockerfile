#   Licensed to the Apache Software Foundation (ASF) under one or more
#   contributor license agreements.  See the NOTICE file distributed with
#   this work for additional information regarding copyright ownership.
#   The ASF licenses this file to You under the Apache License, Version 2.0
#   (the "License"); you may not use this file except in compliance with
#   the License.  You may obtain a copy of the License at
#
#        http://www.apache.org/licenses/LICENSE-2.0
#
#   Unless required by applicable law or agreed to in writing, software
#   distributed under the License is distributed on an "AS IS" BASIS,
#   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#   See the License for the specific language governing permissions and
#   limitations under the License.
#
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