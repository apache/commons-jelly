/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/jeez/src/java/org/apache/commons/jelly/tags/jeez/Attic/JeezTagLibrary.java,v 1.4 2002/07/19 23:05:21 jstrachan Exp $
 * $Revision: 1.4 $
 * $Date: 2002/07/19 23:05:21 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2002 The Apache Software Foundation.  All rights
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
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
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
 * $Id: JeezTagLibrary.java,v 1.4 2002/07/19 23:05:21 jstrachan Exp $
 */

package org.apache.commons.jelly.tags.jeez;

import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.JellyException;
import org.apache.commons.jelly.impl.TagScript;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.tags.ant.AntTagLibrary;
import org.apache.commons.jelly.tags.define.DynamicTagLibrary;
import org.apache.commons.jelly.tags.werkz.WerkzTagLibrary;
// import org.apache.commons.jelly.tags.core.CoreTagLibrary;

import org.apache.tools.ant.Project;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.xml.sax.Attributes;

/** Convenience taglib that puts jelly:core, jelly:werkz and jelly:ant
 *  into a single namespace.
 *
 * @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @version $Revision: 1.4 $
 */
public class JeezTagLibrary extends DynamicTagLibrary {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(JeezTagLibrary.class);

    /** jelly:core taglib. */
    // private TagLibrary coreTagLib;

    /** jelly:werkz taglib. */
    private TagLibrary werkzTagLib;

    /** jelly:ant taglib. */
    private AntTagLibrary antTagLib;

    private Set runtimeTasks;

    private Project project;

    /**
     * Use a default ant Project
     */
    public JeezTagLibrary() {
        this( AntTagLibrary.createProject() );
    }

    
    /** Construct.
     *
     *  @param antProject The ant Project.
     */
    public JeezTagLibrary(Project antProject) {

        this.project      = antProject;
        this.runtimeTasks = new HashSet();

        // this.coreTagLib   = new CoreTagLibrary();
        this.antTagLib    = new AntTagLibrary( antProject );
        this.werkzTagLib  = new WerkzTagLibrary();

        // #### this is a bit of a hack. 
        // when we introduce the concept of TagFactory objects
        // we could do a cleaner mechanism for this...
        TagDefTag.tagLibrary = this;
        
        registerTag( "target",
                     TargetTag.class );
        registerTag( "tagdef",
                     TagDefTag.class );
    }

    public TagScript createTagScript(String name,
                                     Attributes attrs) throws Exception
    {
        TagScript script = super.createTagScript( name, attrs );

        if ( script == null ) {

            // script = this.coreTagLib.createTagScript( name, attrs );

            if ( script == null ) {
                
                script = this.werkzTagLib.createTagScript( name, attrs );
                
                if ( script == null ) {

                    if ( isRuntimeTask( name ) ) {
                        if ( ! project.getTaskDefinitions().containsKey( name ) ) {
                            script = this.antTagLib.createRuntimeTaskTagScript( name, attrs );
                        } else {
                            this.runtimeTasks.remove( name );
                        }
                    }

                    if ( script == null ) {
                        script = this.antTagLib.createTagScript( name, attrs );
                    }

                    if ( name.equals( "taskdef" ) ) {
                        addRuntimeTask( attrs.getValue( "name" ) );
                    } 
                }
            }
        }

        return script;
    }

    protected void addRuntimeTask(String name) {
        this.runtimeTasks.add( name );
    }

    protected boolean isRuntimeTask(String name) {
        return this.runtimeTasks.contains( name );
    }
}
