/*
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999 The Apache Software Foundation.  All rights
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

package org.apache.commons.jelly.tags.werkz;

import com.werken.werkz.Goal;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * This tag outputs a sorted Map of Maps all of the goals, indexed by their prefix and their
 * goal name. This is output to a variable. This map of maps makes it easy to navigate the 
 * available Goals.
 * <p>
 * So if the goals is output to a variable called 'g' then you can access a specific goal via
 * a Jexl expression ${g.java.compile} or to find all the 'java' goals you can use ${g.java} which
 * returns a sorted Map. 
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.7 $
 */
public class UseGoalsTag extends WerkzTagSupport {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(UseGoalsTag.class);
    
    /** the name of the variable to export */
    private String var;
    
    public UseGoalsTag() {
    }


    // Tag interface
    //------------------------------------------------------------------------- 
    
    /** 
     * Evaluate the body to register all the various goals and pre/post conditions
     * then run all the current targets
     */
    public void doTag(final XMLOutput output) throws Exception {
        if (var == null) {
            throw new MissingAttributeException("var");
        }
        
        Map answer = createMap();
        
        
        Iterator iter = getProject().getGoals().iterator();
        while (iter.hasNext()) {
            Goal goal = (Goal) iter.next();
            String name = goal.getName();
            String prefix = name;
            int idx = name.indexOf(":");
            if (idx >= 0) {
                prefix = name.substring(0, idx);
                name = name.substring(idx+1);
            }
            else {
                name = "[default]";
            }
            
            Map map = (Map) answer.get(prefix);
            if (map == null) {
                map = createMap();
                answer.put(prefix, map);
            }
            map.put(name, goal);
        }
        
        context.setVariable(var, answer);
    }

    
    // Properties
    //------------------------------------------------------------------------- 
    /**
     * Sets the variable for which the Map of Map of goals will be exported
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * Factory method to create a new sorted map
     */        
    protected Map createMap() {
        return new TreeMap();
    }
}
