/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/core/JellyTestSuite.java,v 1.8 2002/07/06 13:53:39 dion Exp $
 * $Revision: 1.8 $
 * $Date: 2002/07/06 13:53:39 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 * 
 * $Id: JellyTestSuite.java,v 1.8 2002/07/06 13:53:39 dion Exp $
 */
package org.apache.commons.jelly.ant.task;

import org.apache.tools.ant.Task;
import org.apache.tools.ant.BuildException;

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

*/



/** 
 * A sample Task to test out the Ant introspection logic
 *
 * @author Aslak Hellesøy (aslak.hellesoy@bekk.no)
 * @version $Revision: 1.8 $
 */
public class DummyTask extends Task {
   private int i = 0;
   private String[] messages = {
       "a",
       "b",
       "c",
       "d",
       "e",
       "f",
       "g",
       "h",
       "i"
   };

   public Thingy createDing() {
       System.out.println(messages[i++]);
       return new Thingy();
   }

   public void addDang(Thingy thingy) {
       System.out.println(messages[i++]);
   }

   public void addConfiguredDong(Thingy thingy) {
       System.out.println(messages[i++]);
   }

   public Thingy createHipHop() {
       System.out.println(messages[i++]);
       return new Thingy();
   }

   public void addWontStop(Thingy thingy) {
       System.out.println(messages[i++]);
   }

   public void addConfiguredTillYouDrop(Thingy thingy) {
       System.out.println(messages[i++]);
   }

   public static class Thingy {
   }
}