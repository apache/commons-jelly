/*
 * /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/swt/SwtTagLibrary.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 * 1.1
 * 2002/12/18 15:27:49
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
 * SwtTagLibrary.java,v 1.1 2002/12/18 15:27:49 jstrachan Exp
 */
package org.apache.commons.jelly.tags.jface;

import java.util.Map;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.tags.core.UseBeanTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.swt.widgets.Event;

/**
 * This tag creates an JFace Action
 * 
 * @author <a href="mailto:ckl@dacelo.nl">Christiaan ten Klooster</a> 
 */
public class ActionTag extends UseBeanTag {

    /**
     *  Implementing Action class
     */
    class ActionImpl extends Action {
        public void runWithEvent(Event event) {
            try {
                context.setVariable(var, event);
                invokeBody(output);
            } catch (Exception e) {
                log.error(
                    "Caught exception: "
                        + e
                        + " while processing event: "
                        + event,
                    e);
            }
        }
    }

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(ActionTag.class);
    private XMLOutput output;
    private String var = "event";
    
    /**
     * @param arg0
     */
    public ActionTag(Class arg0) {
        super(arg0);
    }

    /**
     * @return IContributionManager
     */
    protected IContributionManager getParentContributionManager() {
        MenuManagerTag tag =
            (MenuManagerTag) findAncestorWithClass(MenuManagerTag.class);
        if (tag != null) {
            return tag.getMenuManager();
        }
        return null;
    }

    /**
      * @see org.apache.commons.jelly.Tag#doTag(org.apache.commons.jelly.XMLOutput)
      */
    public void doTag(XMLOutput output)
        throws MissingAttributeException, JellyTagException {

        Map attributes = getAttributes();
		
        Action action = new ActionImpl();
        setBeanProperties(action, attributes);

        IContributionManager cm = getParentContributionManager();
        if (cm != null) {
            cm.add(action);
        }

        this.output = output;
    }

}