/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.ant.task;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;

/*

<taskdef
   name="nested"
   classname="jellybug.NestedTask"
>
   <classpath>
      <pathelement path="somewhere"/>
   </classpath>
</taskdef>

<nested>
   <ding/>
   <dang/>
   <dong/>
   <hiphop/>
   <wontstop/>
   <tillyoudrop/>
   <hipHop/>
   <wontStop/>
   <tillYouDrop/>
</nested>

Ant:
   [nested] a
   [nested] b
   [nested] c
   [nested] d
   [nested] e
   [nested] f
   [nested] g
   [nested] h
   [nested] i

Maven/Jelly:
a
b
c
d
e
f
g
h
i

*/

/**
 * A sample Task to test out the Ant introspection logic
 */
public class DummyTask extends Task {
    public static class Thingy {
    }
    private int i = 0;
    private final String[] messages = { "a", "b", "c", "d", "e", "f", "g", "h", "i" };

    private boolean force;

    public void addConfiguredDong(final Thingy thingy) {
        System.out.println("addConfiguredDong: " + messages[i++]);
    }

    public void addConfiguredTillYouDrop(final Thingy thingy) {
        System.out.println("addConfiguredTillYouDrop: " + messages[i++]);
    }

    public void addDang(final Thingy thingy) {
        System.out.println("addDang: " + messages[i++]);
    }

    public void addWontStop(final Thingy thingy) {
        System.out.println("addWontStop: " + messages[i++]);
    }

    public Thingy createDing() {
        System.out.println("createDing: " + messages[i++]);
        return new Thingy();
    }

    public Thingy createHipHop() {
        System.out.println("createHipHop: " + messages[i++]);
        return new Thingy();
    }

    @Override
    public void execute() throws BuildException {
        if (!force) {
            throw new BuildException("Should have set force to be true!");
        }
    }

    public boolean isForce() {
        return force;
    }

    public void setForce(final boolean force) {
        this.force = force;
    }
}