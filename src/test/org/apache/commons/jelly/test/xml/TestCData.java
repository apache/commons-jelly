/*
 * Created on Sep 15, 2003
 *
 */
package org.apache.commons.jelly.test.xml;

import org.apache.commons.jelly.Jelly;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;

import junit.framework.TestCase;

/**
 * @author mdelagrange
 *
 */
public class TestCData extends TestCase {

    public TestCData(String arg) {
        super(arg);
    }

    /**
     * CDATA sections should be retained in the output.
     * 
     * @throws Exception
     */
    public void testCData() throws Exception {
        Jelly jelly = new Jelly();
        jelly.setScript("file:src/test/org/apache/commons/jelly/test/xml/testCData.jelly");
        Script script = jelly.compileScript();
        JellyContext context = new JellyContext();
        script.run(context, XMLOutput.createDummyXMLOutput());
        
        String output = (String) context.getVariable("foo");
        assertTrue("'foo' is not null", output != null);
        
        String golden = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n";
        golden += "<!DOCTYPE foo [\n";
        golden += "  <!ELEMENT foo (#PCDATA)>\n";
        golden += "]><foo></foo>";
        
        assertEquals("output should contain the CDATA section", output, golden);
    }

}
