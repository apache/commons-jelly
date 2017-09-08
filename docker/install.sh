#!/bin/bash -x
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

cd /tmp
mv something.bin jdk-1_5_0_22-linux-amd64.bin
chmod a+x jdk-1_5_0_22-linux-amd64.bin
./jdk-1_5_0_22-linux-amd64.bin < answer.txt
mv ./jdk1.5.0_22/* /usr/java

mkdir -p /opt/ant
curl http://archive.apache.org/dist/ant/binaries/apache-ant-1.6.0-bin.tar.gz -o /tmp/apache-ant-1.6.0-bin.tar.gz
tar -xf apache-ant-1.6.0-bin.tar.gz -C /opt/ant --strip-components 1

mkdir -p /root/commons-jelly-1.X

mkdir -p /root/.maven/repository/servletapi/jars
mkdir -p /root/.maven/repository/commons-cli/jars
mkdir -p /root/.maven/repository/commons-lang/jars
mkdir -p /root/.maven/repository/commons-discovery/jars
mkdir -p /root/.maven/repository/forehead/jars
mkdir -p /root/.maven/repository/jstl/jars
mkdir -p /root/.maven/repository/junit/jars
mkdir -p /root/.maven/repository/commons-jexl/jars
mkdir -p /root/.maven/repository/xml-apis/jars
mkdir -p /root/.maven/repository/commons-beanutils/jars
mkdir -p /root/.maven/repository/commons-collections/jars
mkdir -p /root/.maven/repository/commons-logging/jars
mkdir -p /root/.maven/repository/dom4j/jars
mkdir -p /root/.maven/repository/jaxen/jars
mkdir -p /root/.maven/repository/xerces/jars

curl https://search.maven.org/remotecontent?filepath=javax/servlet/servlet-api/2.3/servlet-api-2.3.jar -o /root/.maven/repository/servletapi/jars/servletapi-2.3.jar
curl https://search.maven.org/remotecontent?filepath=commons-cli/commons-cli/1.0/commons-cli-1.0.jar -o /root/.maven/repository/commons-cli/jars/commons-cli-1.0.jar
curl https://search.maven.org/remotecontent?filepath=commons-lang/commons-lang/2.0/commons-lang-2.0.jar -o /root/.maven/repository/commons-lang/jars/commons-lang-2.0.jar
curl https://search.maven.org/remotecontent?filepath=commons-discovery/commons-discovery/20030211.213356/commons-discovery-20030211.213356.jar -o /root/.maven/repository/commons-discovery/jars/commons-discovery-20030211.213356.jar
curl https://search.maven.org/remotecontent?filepath=forehead/forehead/1.0-beta-5/forehead-1.0-beta-5.jar -o /root/.maven/repository/forehead/jars/forehead-1.0-beta-5.jar
curl https://search.maven.org/remotecontent?filepath=javax/servlet/jstl/1.0.6/jstl-1.0.6.jar -o /root/.maven/repository/jstl/jars/jstl-1.0.6.jar
curl https://search.maven.org/remotecontent?filepath=junit/junit/3.8.1/junit-3.8.1.jar -o /root/.maven/repository/junit/jars/junit-3.8.1.jar
curl https://search.maven.org/remotecontent?filepath=commons-jexl/commons-jexl/1.0/commons-jexl-1.0.jar -o /root/.maven/repository/commons-jexl/jars/commons-jexl-1.0.jar
curl https://search.maven.org/remotecontent?filepath=xml-apis/xml-apis/1.0.b2/xml-apis-1.0.b2.jar -o /root/.maven/repository/xml-apis/jars/xml-apis-1.0.b2.jar
curl https://search.maven.org/remotecontent?filepath=commons-beanutils/commons-beanutils/1.6/commons-beanutils-1.6.jar -o /root/.maven/repository/commons-beanutils/jars/commons-beanutils-1.6.jar
curl https://search.maven.org/remotecontent?filepath=commons-collections/commons-collections/2.1/commons-collections-2.1.jar -o /root/.maven/repository/commons-collections/jars/commons-collections-2.1.jar
curl https://search.maven.org/remotecontent?filepath=commons-logging/commons-logging/1.0.3/commons-logging-1.0.3.jar -o /root/.maven/repository/commons-logging/jars/commons-logging-1.0.3.jar
curl https://search.maven.org/remotecontent?filepath=dom4j/dom4j/1.5.2/dom4j-1.5.2.jar -o /root/.maven/repository/dom4j/jars/dom4j-1.5.2.jar
curl https://search.maven.org/remotecontent?filepath=jaxen/jaxen/1.1-beta-4/jaxen-1.1-beta-4.jar -o /root/.maven/repository/jaxen/jars/jaxen-1.1-beta-4.jar
curl https://search.maven.org/remotecontent?filepath=xerces/xercesImpl/2.2.1/xercesImpl-2.2.1.jar -o /root/.maven/repository/xerces/jars/xerces-2.2.1.jar

cp /root/.maven/repository/junit/jars/junit-3.8.1.jar /opt/ant/lib/junit-3.8.1.jar