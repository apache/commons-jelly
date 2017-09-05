Building the 1.X branch.
========================

As this code is fairly old, we have constructed a docker container for the purpose of building the `1.X` branch. For the sake of completeness, the code for that container is availble below.

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