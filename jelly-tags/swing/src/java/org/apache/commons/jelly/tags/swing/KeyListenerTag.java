package org.apache.commons.jelly.tags.swing;

import java.awt.event.*;

import org.apache.commons.jelly.*;
import org.apache.commons.jelly.tags.swing.*;
import org.apache.commons.logging.*;

public class KeyListenerTag extends TagSupport
{
  protected static final Log log = LogFactory.getLog(KeyListenerTag.class);

  protected String var;
  protected Script pressed;
  protected Script typed;
  protected Script released;

  public KeyListenerTag()
  {
    super();
  }

  public void setVar(String var)
  {
    this.var = var;
  }

  public void setPressed(Script pressed)
  {
    this.pressed = pressed;
  }

  public void setReleased(Script released)
  {
    this.released = released;
  }

  public void setTyped(Script typed)
  {
    this.typed = typed;
  }

  public void doTag(final XMLOutput output) throws JellyTagException
  {
    // now lets add this action to its parent if we have one
    ComponentTag tag = (ComponentTag)findAncestorWithClass(ComponentTag.class);
    if (tag != null)
    {
      KeyListener listener = new KeyListener()
      {
        public void keyTyped(KeyEvent e)
        {
          invokeScript(output, e, typed);
        }

        public void keyPressed(KeyEvent e)
        {
          invokeScript(output, e, pressed);
        }

        public void keyReleased(KeyEvent e)
        {
          invokeScript(output, e, released);
        }
      };
      tag.addKeyListener(listener);
    }
  }

  protected void invokeScript(XMLOutput output, KeyEvent event, Script script)
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
    catch (Exception e)
    {
      log.error("Caught exception processing window event: " + event, e);
    }
  }

}
