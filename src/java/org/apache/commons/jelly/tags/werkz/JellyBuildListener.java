
package org.apache.commons.jelly.tags.werkz;

import org.apache.commons.jelly.XMLOutput;

import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildEvent;

import org.xml.sax.SAXException;

import java.util.Stack;

public class JellyBuildListener implements BuildListener
{
    private XMLOutput out;

    private Stack taskStack;

    public JellyBuildListener(XMLOutput out) {
        this.taskStack = new Stack();
        this.out = out;
    }

    public void buildFinished(BuildEvent event) {
    }

    public void buildStarted(BuildEvent event) {
    }

    public void messageLogged(BuildEvent event) {

        try
        {
            if ( this.taskStack.isEmpty() )
            {
                out.write( event.getMessage() + "\n" );
            }
            else
            {
                out.write( "    [" + this.taskStack.peek() + "] " + event.getMessage() + "\n" );
            }
        }
        catch (SAXException e)
        {
            // ignore
        }
    }

    public void targetFinished(BuildEvent event) {
    }

    public void targetStarted(BuildEvent event) {
    }

    public void taskFinished(BuildEvent event) {
        this.taskStack.pop();
    }

    public void taskStarted(BuildEvent event) {
        this.taskStack.push( event.getTask().getTaskName() );
    }

}


