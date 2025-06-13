/*
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
*
*     https://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.commons.jelly.tags.swing;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KeyListenerTag extends TagSupport
{
  protected static final Log log = LogFactory.getLog(KeyListenerTag.class);

  protected String var;
  protected Script pressed;
  protected Script typed;
  protected Script released;

  public KeyListenerTag()
  {
  }

  @Override
public void doTag(final XMLOutput output) throws JellyTagException
  {
    // now lets add this action to its parent if we have one
    final ComponentTag tag = (ComponentTag)findAncestorWithClass(ComponentTag.class);
    if (tag != null)
    {
      final KeyListener listener = new KeyListener()
      {
        @Override
        public void keyPressed(final KeyEvent e)
        {
          invokeScript(output, e, pressed);
        }

        @Override
        public void keyReleased(final KeyEvent e)
        {
          invokeScript(output, e, released);
        }

        @Override
        public void keyTyped(final KeyEvent e)
        {
          invokeScript(output, e, typed);
        }
      };
      tag.addKeyListener(listener);
    }
  }

  protected void invokeScript(final XMLOutput output, final KeyEvent event, final Script script)
  {
    if (var != null)
    {
      // define a variable of the event
      context.setVariable(var, event);
    }

    try
    {
      if (script != null)
      {
        script.run(context, output);
      }
      else
      {
        // invoke the body
        invokeBody(output);
      }
    }
    catch (final Exception e)
    {
      log.error("Caught exception processing window event: " + event, e);
    }
  }

  public void setPressed(final Script pressed)
  {
    this.pressed = pressed;
  }

  public void setReleased(final Script released)
  {
    this.released = released;
  }

  public void setTyped(final Script typed)
  {
    this.typed = typed;
  }

  public void setVar(final String var)
  {
    this.var = var;
  }

}
