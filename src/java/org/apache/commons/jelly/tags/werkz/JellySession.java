
package org.apache.commons.jelly.tags.werkz;

import org.apache.commons.jelly.XMLOutput;

import org.xml.sax.SAXException;

import com.werken.werkz.Session;

public class JellySession extends Session
{
    private XMLOutput out;

    public JellySession(XMLOutput out)
    {
        this.out = out;
    }

    public void info(String message)
    {
        try
        {
            this.out.write( message + "\n" );
        }
        catch (SAXException e)
        {
            // ignore
        }
    }

    public void warn(String message)
    {
        try
        {
            this.out.write( message + "\n" );
        }
        catch (SAXException e)
        {
            // ignore
        }
    }

    public void error(String message)
    {
        try
        {
            this.out.write( message + "\n" );
        }
        catch (SAXException e)
        {
            // ignore
        }
    }
}
