Building the 1.X branch.
========================

As this code is fairly old, we have constructed a docker container for the purpose of building the `1.X` branch. For the sake of completeness, the code for that container is availble below.

### Building the container.

Once you have the below 4 files in place, you can run

`docker build -t commons-jelly-build-env .`

from the directory containing the files, assuming that you have docker installed.

#### Dockerfile.

*Note.* You will need the java 1.5.0 installer for this container in the
directory along with the docker file. Specifically, we used:  jdk-1_5_0_22-linux-amd64.bin

```dockerfile
# DESCRIPTION:    commons-jelly-build-env
# SOURCE:         https://github.com/chtompki/Dockerfiles/tree/master/commons-jelly-build-env

FROM library/ubuntu:12.04

RUN apt-get -qq update && apt-get install -y curl wget pgp subversion

RUN mkdir -p /usr/java

ADD jdk-1_5_0_22-linux-amd64.bin /tmp
ADD answer.txt /tmp
ADD install.sh /tmp

RUN chmod +x /tmp/install.sh && sh /tmp/install.sh

ENV JAVA_HOME=/usr/java
ENV PATH=${PATH}:${JAVA_HOME}/bin:/opt/ant/bin
```

#### answer.txt

This is needed mainly as the standard in for the jdk.1.5.0_b22 installer. We couldn't find a way to get around the installer prompting for answers.

```
q
yes
```

#### install.sh

Essentially we're installing java 1.5.0, ant 1.6.0, and downloading the requisite libraries for compilation purposes.

```bash
#!/bin/bash -x

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
```

### Running the container.

We begin by checking the container out into a directory of our choosing on our machine. And, we'll want to make sure that we have properly shared the directory with the docker daemon running locally such that we can map a directory into the container. After that we run the following:

```
docker run -it -v /path/to/checked/out/commons/jelly:/root/commons-jelly-1.x commons-jelly-build-env bash
```

assuming that you ran the exact build command from above (naming the container `commons-jelly-build-env`). Once here you will be logged in on the command line in the container as the `root` user with the above installed and your `commons-jelly` directory shared into the directory on the container `/root/commons-jelly-1.x`. From here you can `cd` to that directory and run any of the ant commands you wish in the project.

For more details on running within the container refer to the following email on the development mailing list: http://markmail.org/message/i6lk2zfrcexs3lgq