/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/jeez/Attic/JeezTagLibrary.java,v 1.7 2002/08/27 14:04:29 jstrachan Exp $
 * $Revision: 1.7 $
 * $Date: 2002/08/27 14:04:29 $
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
 * $Id: JeezTagLibrary.java,v 1.7 2002/08/27 14:04:29 jstrachan Exp $
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
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.TagLibrary;
import org.apache.commons.jelly.impl.BeanTagScript;
import org.apache.commons.jelly.impl.DynamicTagLibrary;
import org.apache.commons.jelly.impl.DynaTagScript;
import org.apache.commons.jelly.impl.TagFactory;
import org.apache.commons.jelly.tags.ant.AntTagLibrary;
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
 * @version $Revision: 1.7 $
 */
public class JeezTagLibrary extends DynamicTagLibrary {

    /** The Log to which logging calls will be made. */
    private Log log = LogFactory.getLog(JeezTagLibrary.class);

    /** jelly:core taglib. */
    // private TagLibrary coreTagLib = new CoreTagLibrary();

    /** jelly:werkz taglib. */
    private TagLibrary werkzTagLib = new WerkzTagLibrary();

    /** jelly:ant taglib. */
    private AntTagLibrary antTagLib= new AntTagLibrary();

    public TagScript createTagScript(
        final String name,
        Attributes attrs
    ) throws Exception {

        if ( name.equals( "tagdef" ) ) {
            return new BeanTagScript(
                new TagFactory() {
                    public Tag createTag() {
                        return new TagDefTag( JeezTagLibrary.this );
                    }
                }
            );
        }
        if ( name.equals( "target" ) ) {
            return new BeanTagScript(
                new TagFactory() {
                    public Tag createTag() {
                        return new TargetTag();
                    }
                }
            );
        }
        TagScript script = this.werkzTagLib.createTagScript( name, attrs );
        if ( script == null ) {
            script = antTagLib.createCustomTagScript( name, attrs );
            if ( script == null ) {
                return new DynaTagScript(
                    new TagFactory() {
                        public Tag createTag() throws Exception {
                            // lets try create a dynamic tag first
                            Tag tag = JeezTagLibrary.this.createTag(name);
                            if ( tag != null ) {
                                return tag;
                            }
                            else {
                                return antTagLib.createTag( name );
                            }
                        }
                    }
                );                        
            }
        }
        return script;
    }
}
