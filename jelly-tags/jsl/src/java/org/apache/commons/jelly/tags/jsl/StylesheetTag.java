/*
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
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
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
 */
package org.apache.commons.jelly.tags.jsl;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathSource;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Rule;
import org.dom4j.rule.Stylesheet;
import org.jaxen.XPath;


/** 
 * This tag implements a JSL stylesheet which is similar to an 
 * XSLT stylesheet but can use Jelly tags inside it
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class StylesheetTag extends XPathTagSupport implements XPathSource {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(StylesheetTag.class);

    
    /** Holds the stylesheet which will be applied to the source context. */
    private Stylesheet stylesheet;
    
    /** Holds value of property mode. */
    private String mode;    

    /** The variable which the stylesheet will be output as */
    private String var;
        
    /** The XPath expression to evaluate. */    
    private XPath select;
    
    /** The XPath source used by TemplateTag and ApplyTemplatesTag to pass XPath contexts */
    private Object xpathSource;
    
    public StylesheetTag() {
    }
        

	/**
	 * @return the XMLOutput from the stylesheet if available
	 */
	public XMLOutput getStylesheetOutput() {
		if (stylesheet instanceof JellyStylesheet) {
			JellyStylesheet jellyStyle = (JellyStylesheet) stylesheet;
			return jellyStyle.getOutput();
		}
		return null;
	}
	
	/**
	 * Sets the XMLOutput to use by the current stylesheet	 */
	public void setStylesheetOutput(XMLOutput output) {
		if (stylesheet instanceof JellyStylesheet) {
			JellyStylesheet jellyStyle = (JellyStylesheet) stylesheet;
			jellyStyle.setOutput(output);
		}
	}
	
    /** 
     * Adds a new template rule to this stylesheet
     */    
    public void addTemplate( Rule rule ) {
        getStylesheet().addRule( rule );
    }
    
    // XPathSource interface
    //-------------------------------------------------------------------------                    

    /**
     * @return the current XPath iteration value
     *  so that any other XPath aware child tags to use
     */
    public Object getXPathSource() {
        return xpathSource;
    }
    
    
    // Tag interface
    //-------------------------------------------------------------------------                    
	public void doTag(XMLOutput output) throws Exception {
		stylesheet = createStylesheet(output);

		// run the body to add the rules
		invokeBody(output);
		stylesheet.setModeName(getMode());

		if (var != null) {
			context.setVariable(var, stylesheet);
		} 
        else {
			Object source = getSource();

			if (log.isDebugEnabled()) {
				log.debug("About to evaluate stylesheet on source: " + source);
			}

			stylesheet.run(source);
		}
	}
    
    
    // Properties
    //-------------------------------------------------------------------------                
    
    /** 
     * Getter for property mode.
     * @return Value of property mode.
     */
    public String getMode() {
        return mode;
    }
    
    /** 
     * Sets the mode.
     * @param mode New value of property mode.
     */
    public void setMode(String mode) {
        this.mode = mode;
    }   

    public Stylesheet getStylesheet() {
        return stylesheet;
    }
    
    /** Sets the variable name to define for this expression
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /** Sets the XPath expression to evaluate. */
    public void setSelect(XPath select) {
        this.select = select;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                

    /** @return the source on which the stylesheet should run
     */
    protected Object getSource() throws Exception {
        Object source = getXPathContext();
        if ( select != null ) {
            return select.evaluate(source);
        }
        return source;
    }    
    
    
    /**
     * Factory method to create a new stylesheet 
     */
    protected Stylesheet createStylesheet(final XMLOutput output) {
    	JellyStylesheet answer = new JellyStylesheet();
    	answer.setOutput(output);
    	return answer;
    }
        
    /**
     * Sets the xpathSource.
     * @param xpathSource The xpathSource to set
     */
    void setXPathSource(Object xpathSource) {
        this.xpathSource = xpathSource;
    }

}
