
package org.apache.commons.jelly.tags.werkz;

import org.apache.commons.jelly.XMLOutput;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTask;

import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Stack;

public class JellyBuildListener implements BuildListener
{
    private XMLOutput out;

    private Stack taskStack;

    private boolean debug;

    public JellyBuildListener(XMLOutput out) {
        this.taskStack = new Stack();
        this.out       = out;
        this.debug     = false;
    }

    public boolean isDebug() {
        return this.debug;
    }

    public void isDebug(boolean debug) {
        this.debug = debug;
    }

    public void buildFinished(BuildEvent event) {
    }

    public void buildStarted(BuildEvent event) {
    }

    public void messageLogged(BuildEvent event) {

        // System.err.println( "messageLogged(" + event + ")" );

        if ( event.getPriority() > Project.MSG_INFO
             &&
             ! isDebug() ) {
            return;
        }

        try {
            if ( ! this.taskStack.isEmpty() ) {
                out.write( "    [" + this.taskStack.peek() + "] " );
            }

            switch ( event.getPriority() ) {
                case ( Project.MSG_ERR ): {
                    out.write( "[ERROR] ");
                    break;
                }
                case ( Project.MSG_WARN ): {
                    // out.write( "[WARN] ");
                    break;
                }
                case ( Project.MSG_INFO ): {
                    // normal, do nothing.
                    break;
                }
                case ( Project.MSG_VERBOSE ): {
                    out.write( "[VERBOSE] ");
                    break;
                }
                case ( Project.MSG_DEBUG ): {
                    out.write( "[DEBUG] ");
                    break;
                }
            }
            
            out.write( event.getMessage() + "\n" );
            out.flush();
        } catch (SAXException e) {
            // fall-back to stderr.
            System.err.println( event.getMessage() );
            System.err.flush();
        } catch (IOException e) {
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


