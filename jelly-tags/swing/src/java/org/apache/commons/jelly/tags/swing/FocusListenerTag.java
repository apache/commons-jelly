package org.apache.commons.jelly.tags.swing;

import java.awt.event.*;

import org.apache.commons.jelly.*;
import org.apache.commons.jelly.tags.swing.*;
import org.apache.commons.logging.*;

public class FocusListenerTag extends TagSupport
{
  protected static final Log log = LogFactory.getLog(FocusListenerTag.class);

  protected String var;
  protected Script gained;
  protected Script lost;

  /**
   * 
   */
  public FocusListenerTag()
  {
    super();
  }

  /**
  * @param var
  */
  public void setVar(String var)
  {
    this.var = var;
  }

  /**
   * @param gained
   */
  public void setGained(Script gained)
  {
    this.gained = gained;
  }

  /**
   * @param lost
   */
  public void setLost(Script lost)
  {
    this.lost = lost;
  }

  public void doTag(final XMLOutput output) throws JellyTagException
  {
    // now lets add this action to its parent if we have one
    ComponentTag tag = (ComponentTag)findAncestorWithClass(ComponentTag.class);
    if (tag != null)
    {
      FocusListener listener = new FocusListener()
      {
        public void focusGained(FocusEvent e)
        {
          invokeScript(output, e, gained);
        }

        public void focusLost(FocusEvent e)
        {
          invokeScript(output, e, lost);
        }
      };
      tag.addFocusListener(listener);
    }
  }

  protected void invokeScript(XMLOutput output, FocusEvent event, Script script)
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
