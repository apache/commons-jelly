<!--
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
-->

Building the 1.X branch.
========================

The release build was last run with `java-1_5_0_22` and `ant-1.6.0`. However, the build should be runnable with any `java-5` and up to `ant-1.9.9`. If you can not find a way to install these, we have included the mechanics for building a [docker container](#docker), in which you can run the ant build.

### Ant targets

__Default target:__ jar
__Test target:__ test
__Release Target:__ dist

* *clean* - Clean up the generated directories
* *compile* - Compile the code
* *dist* - Create a distribution
* *init* - Initializes some properties
* *jar* - Create the jar
* *javadoc* - Generate javadoc
* *javadoc-jar* - Generate javadoc-jar
* *sources-jar* - Generate sources-jar
* *test* - Run the test cases
* *test-sources-jar* - Generate test-sources-jar
* *tests-jar* - Generate tests-jar

__Other targets:__

* *get-dep-commons-beanutils.jar* - Download the dependency : commons-beanutils.jar
* *get-dep-commons-cli.jar* - Download the dependency : commons-cli.jar
* *get-dep-commons-collections.jar* - Download the dependency : commons-collections.jar
* *get-dep-commons-discovery.jar* - Download the dependency : commons-discovery.jar
* *get-dep-commons-jexl.jar* - Download the dependency : commons-jexl.jar
* *get-dep-commons-lang.jar* - Download the dependency : commons-lang.jar
* *get-dep-commons-logging.jar* - Download the dependency : commons-logging.jar
* *get-dep-dom4j.jar* - Download the dependency : dom4j.jar
* *get-dep-forehead.jar* - Download the dependency : forehead.jar
* *get-dep-jaxen.jar* - Download the dependency : jaxen.jar
* *get-dep-jstl.jar* - Download the dependency : jstl.jar
* *get-dep-junit.jar* - Download the dependency : junit.jar
* *get-dep-servletapi.jar* - Download the dependency : servletapi.jar
* *get-dep-xerces.jar* - Download the dependency : xerces.jar
* *get-dep-xml-apis.jar* - Download the dependency : xml-apis.jar

__Targets with no descriptions:__

* compile-tests
* get-custom-dep-commons-beanutils.jar
* get-custom-dep-commons-cli.jar
* get-custom-dep-commons-collections.jar
* get-custom-dep-commons-discovery.jar
* get-custom-dep-commons-jexl.jar
* get-custom-dep-commons-lang.jar
* get-custom-dep-commons-logging.jar
* get-custom-dep-dom4j.jar
* get-custom-dep-forehead.jar
* get-custom-dep-jaxen.jar
* get-custom-dep-jstl.jar
* get-custom-dep-junit.jar
* get-custom-dep-servletapi.jar
* get-custom-dep-xerces.jar
* get-custom-dep-xml-apis.jar
* get-deps
* install-maven
* internal-test
* junit-present
* noProxy
* setProxy


As this code is fairly old, we have constructed a docker container for the purpose of building the `1.X` branch. For the sake of completeness, the code for that container is availble below.

## docker
### Building the container.

You need to begin with downloading `jdk-1_5_0_22-linux-amd64.bin` from oracle. Note this is available at the [Java SE 5 downloads page](http://www.oracle.com/technetwork/java/javasebusiness/downloads/java-archive-downloads-javase5-419410.html). Once you have the aformentioned installer downloaded to this directory as `jdk-1_5_0_22-linux-amd64.bin` (assuming you have a local copy of the project), you can run

`docker build -t commons-jelly-build-env .`

from the directory containing the files, assuming that you have docker installed.


### Running the container.

We begin by checking the container out into a directory of our choosing on our machine. And, we'll want to make sure that we have properly shared the directory with the docker daemon running locally such that we can map a directory into the container. After that we run the following:

```
docker run -v /path/to/checked/out/commons/jelly:/root/commons-jelly-1.x commons-jelly-build-env ant <ant targets>
```

assuming that you ran the exact build command from above (naming the container `commons-jelly-build-env`). Once here you will be logged in on the command line in the container as the `root` user with the above installed and your `commons-jelly` directory shared into the directory on the container `/root/commons-jelly-1.x`. From here you can `cd` to that directory and run any of the ant commands you wish in the project.

For more details on running within the container refer to the following email on the development mailing list: http://markmail.org/message/i6lk2zfrcexs3lgq
