/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/bsf/src/java/org/apache/commons/jelly/tags/bsf/ScriptTag.java,v 1.3 2003/10/09 21:21:16 rdonkin Exp $
 * $Revision: 1.3 $
 * $Date: 2003/10/09 21:21:16 $
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
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
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
 * $Id: ScriptTag.java,v 1.3 2003/10/09 21:21:16 rdonkin Exp $
 */
package org.apache.commons.jelly.tags.bsf;

import java.util.Iterator;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.LocationAware;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.bsf.BSFEngine;
import org.apache.bsf.BSFManager;
import org.apache.bsf.BSFException;

/** 
 * A tag which evaluates its body using the current scripting language
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class ScriptTag extends TagSupport implements LocationAware {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ScriptTag.class.getName() + ".evaluating");
	
    private BSFEngine engine;
    private BSFManager manager;
    private String elementName;
    private String fileName;
    private int columnNumber;
    private int lineNumber;
    
    public ScriptTag(BSFEngine engine, BSFManager manager) {
        this.engine = engine;
        this.manager = manager;
    }

    // Tag interface
    //------------------------------------------------------------------------- 
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        String text = getBodyText();

        log.debug(text);        

        // XXXX: unfortunately we must sychronize evaluations
        // so that we can swizzle in the context.
        // maybe we could create an expression from a context
        // (and so create a BSFManager for a context)
        synchronized (getRegistry()) {
            getRegistry().setJellyContext(context);

            try {            
                // XXXX: hack - there must be a better way!!!
                for ( Iterator iter = context.getVariableNames(); iter.hasNext(); ) {
                    String name = (String) iter.next();
                    Object value = context.getVariable( name );
                    manager.declareBean( name, value, value.getClass() );
                }
                engine.exec(fileName, lineNumber, columnNumber, text);
            }
            catch (BSFException e) {
                throw new JellyTagException("Error occurred with script: " + e, e);
            }
        }
    }
    
    // Properties
    //-------------------------------------------------------------------------                
    /**
     * @return int
     */
    public int getColumnNumber() {
        return columnNumber;
    }

    /**
     * @return String
     */
    public String getElementName() {
        return elementName;
    }

    /**
     * @return BSFEngine
     */
    public BSFEngine getEngine() {
        return engine;
    }

    /**
     * @return String
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return int
     */
    public int getLineNumber() {
        return lineNumber;
    }

    /**
     * Sets the columnNumber.
     * @param columnNumber The columnNumber to set
     */
    public void setColumnNumber(int columnNumber) {
        this.columnNumber = columnNumber;
    }

    /**
     * Sets the elementName.
     * @param elementName The elementName to set
     */
    public void setElementName(String elementName) {
        this.elementName = elementName;
    }

    /**
     * Sets the engine.
     * @param engine The engine to set
     */
    public void setEngine(BSFEngine engine) {
        this.engine = engine;
    }

    /**
     * Sets the fileName.
     * @param fileName The fileName to set
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * Sets the lineNumber.
     * @param lineNumber The lineNumber to set
     */
    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    private JellyContextRegistry getRegistry()
    {
        return (JellyContextRegistry) this.manager.getObjectRegistry();
    }    
}
