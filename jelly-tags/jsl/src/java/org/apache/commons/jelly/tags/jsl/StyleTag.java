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
 *    any, must include the following acknowledgement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgement may appear in the software itself,
 *    if and wherever such third-party acknowledgements normally appear.
 *
 * 4. The names "The Jakarta Project", "Tomcat", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Software Foundation.
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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.xpath.XPathTagSupport;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.rule.Stylesheet;
import org.jaxen.JaxenException;
import org.jaxen.XPath;

/** 
 * This tag performs a JSL stylesheet which was previously
 * created via an &lt;stylesheet&gt; tag.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.8 $
 */
public class StyleTag extends XPathTagSupport {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(StyleTag.class);

    
    /** Holds the stylesheet which will be applied to the source context. */
    private Stylesheet stylesheet;
    
    /** The XPath expression to evaluate. */    
    private XPath select;
    
    public StyleTag() {
    }
        
	// Tag interface
	//-------------------------------------------------------------------------                    
	public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
		Stylesheet stylesheet = getStylesheet();
		if (stylesheet == null) {
			throw new MissingAttributeException("stylesheet");
		}

        if (stylesheet instanceof JellyStylesheet) {
			JellyStylesheet jellyStyle = (JellyStylesheet) stylesheet;
			jellyStyle.setOutput(output);
		}
        
        // dom4j only seems to throw Exception
        try {
        	Object source = getSource();            
			if (log.isDebugEnabled()) {
				log.debug("About to evaluate stylesheet on source: " + source);
			}

			stylesheet.run(source);
        } catch (Exception e) {
            throw new JellyTagException(e);
        }
	}
    
    
    // Properties
    //-------------------------------------------------------------------------                
    
    public Stylesheet getStylesheet() {
        return stylesheet;
    }

    /**
     * Sets the stylesheet to use to style this tags body
     */
    public void setStylesheet(Stylesheet stylesheet) {
        this.stylesheet = stylesheet;
    }    
    
    /** Sets the XPath expression to evaluate. */
    public void setSelect(XPath select) {
        this.select = select;
    }
    
    // Implementation methods
    //-------------------------------------------------------------------------                

    /** @return the source on which the stylesheet should run
     */
    protected Object getSource() throws JaxenException {
        Object source = getXPathContext();
        if ( select != null ) {
            return select.evaluate(source);
        }
        return source;
    }    
}
