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
import com.werken.werkz.Action;
import com.werken.werkz.DefaultAction;
import com.werken.werkz.CyclicGoalChainException;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.XMLOutput;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/** 
 * Implements a &lt;target&gt; tag which is similar to the Ant equivalent tag
 * but is based on the Werkz goal engine.
 *
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 * @version $Revision: 1.3 $
 */
public class GoalTag extends WerkzTagSupport {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(GoalTag.class);
    
    /** the name of the target */
    private String name;

    private String prereqs;

    private String description;
    
    public GoalTag() {
    }


    // Tag interface
    //------------------------------------------------------------------------- 
    
    /** 
     * Evaluate the body to register all the various goals and pre/post conditions
     * then run all the current targets
     */
    public void doTag(final XMLOutput output) throws Exception {
        
        log.debug("doTag(..):" + name);

        Goal goal = getProject().getGoal( getName(),
                                          true );

        goal.setDescription( this.description );
        addPrereqs( goal );

        Action action = new DefaultAction() {
                public void performAction() throws Exception {
                    log.debug("Running action of target: " + getName() );
                    getBody().run(context, output);
                }
            };

        goal.setAction( action );
    }

    
    // Properties
    //------------------------------------------------------------------------- 
    /**
     * @return the name of the target
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the target
     */
    public void setName(String name) {
        log.debug("setName(" + name + ")" );
        this.name = name;
    }

    public void setPrereqs(String prereqs) {
        this.prereqs = prereqs;
    }

    public String getPrereqs() {
        return this.prereqs;
    }

    public void setDescription(String description) {
        this.description = description;
    }
        

    protected void addPrereqs(Goal goal) throws CyclicGoalChainException
    {
        String prereqs = getPrereqs();

        if ( prereqs == null )
        {
            return;
        }

        StringTokenizer tokens = new StringTokenizer( getPrereqs(),
                                                      "," );

        String eachToken = null;
        Goal   eachPrereq = null;

        while ( tokens.hasMoreTokens() )
        {
            eachToken = tokens.nextToken().trim();

            eachPrereq = getProject().getGoal( eachToken, true );

            goal.addPrerequisite( eachPrereq );
        }
    }
}
