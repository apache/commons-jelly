/*
 * $Header: /home/cvs/jakarta-commons-sandbox/jelly/src/java/org/apache/commons/jelly/tags/ant/AntTagSupport.java,v 1.4 2002/06/25 20:43:30 werken Exp $
 * $Revision: 1.4 $
 * $Date: 2002/06/25 20:43:30 $
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
 * $Id: AntTagSupport.java,v 1.4 2002/06/25 20:43:30 werken Exp $
 */

package org.apache.commons.jelly.tags.ant;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.grant.DefaultPropsHandler;
import org.apache.commons.grant.GrantProject;
import org.apache.commons.grant.PropsHandler;
import org.apache.commons.jelly.MapTagSupport;
import org.apache.commons.jelly.Tag;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.impl.BeanSource;
import org.apache.commons.jelly.impl.StaticTag;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.tools.ant.IntrospectionHelper;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.types.DataType;

/**
 * Tag supporting ant's Tasks as well as
 * dynamic runtime behaviour for 'unknown' tags.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 * @author <a href="mailto:jstrachan@apache.org">James Strachan</a>
 */
public class AntTag extends MapTagSupport implements TaskSource {

    /** The Log to which logging calls will be made. */
    private static final Log log = LogFactory.getLog(AntTag.class);

    private static final Class[] addTaskParamTypes = { String.class };

    /** store the name of the manifest tag for special handling */
    private static final String ANT_MANIFEST_TAG = "manifest";

    /** The name of this tag. */
    protected String tagName;

    /** The general object underlying this tag. */
    protected Object object;

    /** Task, if this tag represents a task. */
    protected Task task;

    /** Construct with a project and tag name.
     *
     *  @param project The Ant project.
     *  @param tagName The name on the tag.
     */
    public AntTag(String tagName) {
        this.tagName = tagName;
    }

    public String toString() {
        return "[AntTag: name=" + getTagName() + "]";
    }

    // Tag interface
    //-------------------------------------------------------------------------
    public void doTag(XMLOutput output) throws Exception {

        Project project = getAntProject();
        String tagName = getTagName();
        Object parentObject = null;
        
        // must be a datatype.
        TaskSource ancestor = (TaskSource) findAncestorWithClass( TaskSource.class );
        if ( ancestor != null ) {
            parentObject = ancestor.getTaskObject();
        }

        // lets assume that Task instances are not nested inside other Task instances
        // for example <manifest> inside a <jar> should be a nested object, where as 
        // if the parent is not a Task the <manifest> should create a ManifestTask

        if ( ! ( parentObject instanceof Task ) && 
            project.getTaskDefinitions().containsKey( tagName ) ) {                            
            
            if ( log.isDebugEnabled() ) {
                log.debug( "Creating an ant Task for name: " + tagName );            
            }
            // the following algorithm follows the lifetime of a tag
            // http://jakarta.apache.org/ant/manual/develop.html#writingowntask
            // kindly recommended by Stefan Bodewig

            // create and set its project reference
            task = createTask( tagName );
            if ( task instanceof TaskAdapter ) {
                setObject( ((TaskAdapter)task).getProxy() );
            }
            else {
                setObject( task );
            }

            // set the task ID if one is given
            Object id = getAttributes().remove( "id" );
            if ( id != null ) {
                project.addReference( (String) id, task );
            }

            // ### we might want to spoof a Target setting here

            // now lets initialize
            task.init();

            // now lets invoke the body to call all the createXXX() or addXXX() methods
            String body = getBodyText();

            // now lets set any attributes of this tag...
            setBeanProperties();

            // now lets set the addText() of the body content, if its applicaable
            Method method = MethodUtils.getAccessibleMethod( task.getClass(),
                                                             "addText",
                                                             addTaskParamTypes );
            if (method != null) {
                String text = getBodyText();

                Object[] args = { text };
                method.invoke(this.task, args);
            }

            // now lets set all the attributes of the child elements
            // XXXX: to do!

            // now we're ready to invoke the task
            // XXX: should we call execute() or perform()?
            task.perform();
        }
        else {

            if ( log.isDebugEnabled() ) {                            
                log.debug( "Creating a nested object name: " + tagName );            
            }

            if ( parentObject == null ) {
                parentObject = findBeanAncestor();
            }

            Object nested = createNestedObject( parentObject, tagName );

            if ( nested == null ) {
                nested = createDataType( tagName );
            }

            if ( nested != null ) {
                setObject( nested );

                // set the task ID if one is given
                Object id = getAttributes().remove( "id" );
                if ( id != null ) {
                    project.addReference( (String) id, nested );
                }

                try{
                    PropertyUtils.setProperty( nested, "name", tagName );
                }
                catch (Exception e) {
                }

                // now lets invoke the body
                String body = getBodyText();
    
                // now lets set any attributes of this tag...
                setBeanProperties();
    
                // now lets add it to its parent
                if ( parentObject != null ) {
                    IntrospectionHelper ih = IntrospectionHelper.getHelper( parentObject.getClass() );
                    try {
                        ih.storeElement( project, parentObject, nested, tagName );
                    }
                    catch (Exception e) {
                        //log.warn( "Caught exception setting nested: " + tagName, e );
                    }
                }
            }
            else {
                // lets treat this tag as static XML...                
                StaticTag tag = new StaticTag("", tagName, tagName);
                tag.setParent( getParent() );
                tag.setBody( getBody() );
    
                tag.setContext(context);
        
                for (Iterator iter = getAttributes().entrySet().iterator(); iter.hasNext();) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    String name = (String) entry.getKey();
                    Object value = entry.getValue();
        
                    tag.setAttribute(name, value);
                }
            
                tag.doTag(output);
            }
        }        
    }


    // Properties
    //-------------------------------------------------------------------------
    public String getTagName() {
        return this.tagName;
    }

    /** Retrieve the general object underlying this tag.
     *
     *  @return The object underlying this tag.
     */
    public Object getTaskObject() {
        return this.object;
    }

    /** Set the object underlying this tag.
     *
     *  @param object The object.
     */
    public void setObject(Object object) {
        this.object = object;
    }

    public Project getAntProject() {
        return AntTagLibrary.getProject(context);
    }

    // Implementation methods
    //-------------------------------------------------------------------------

    /**
     * Sets the properties on the Ant task
     */
    public void setBeanProperties() throws Exception {
        Object object = getTaskObject();
        if ( object != null ) {
            Map map = getAttributes();
            for ( Iterator iter = map.entrySet().iterator(); iter.hasNext(); ) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = (String) entry.getKey();
                Object value = entry.getValue();
                setBeanProperty( object, name, value );
            }
        }
    }

    public void setAttribute(String name, Object value) {
        if ( value == null ) {
            // should we send in null?
            super.setAttribute( name, "" );
        }
        else {
            super.setAttribute( name, value.toString() );
        }
                            
    }

    public void setBeanProperty(Object object, String name, Object value) throws Exception {
        if ( log.isDebugEnabled() ) {
            log.debug( "Setting bean property on: "+  object + " name: " + name + " value: " + value );
        }

        IntrospectionHelper ih = IntrospectionHelper.getHelper( object.getClass() );

        if ( value instanceof String ) {
            try {
                ih.setAttribute( getAntProject(), object, name.toLowerCase(), (String) value );
                return;
            }
            catch (Exception e) {
                // ignore: not a valid property
            }
        }

        try {

            ih.storeElement( getAntProject(), object, value, name );
        }
        catch (Exception e) {

            // let any exceptions bubble up from here
            BeanUtils.setProperty( object, name, value );
        }
    }


    /**
     * Creates a nested object of the given object with the specified name
     */
    public Object createNestedObject(Object object, String name) throws Exception {
        Object dataType = null;
        if ( object != null ) {
            IntrospectionHelper ih = IntrospectionHelper.getHelper( object.getClass() );

            if ( ih != null ) {
                try {
                    dataType = ih.createElement( getAntProject(), object, name );
                }
                catch (Exception e) {
                    log.error(e);
                }
            }
        }

        if ( dataType == null ) {
            dataType = createDataType( name );
        }

        return dataType;
    }

    public Object createDataType(String name) throws Exception {

        Object dataType = null;

        Class type = (Class) getAntProject().getDataTypeDefinitions().get(name);

        if ( type != null ) {

            try {
                Constructor ctor = null;
                boolean noArg = false;

                // DataType can have a "no arg" constructor or take a single
                // Project argument.
                try {
                    ctor = type.getConstructor(new Class[0]);
                    noArg = true;
                }
                catch (NoSuchMethodException nse) {
                    ctor = type.getConstructor(new Class[] { Project.class });
                    noArg = false;
                }

                if (noArg) {
                    dataType = (DataType) ctor.newInstance(new Object[0]);
                }
                else {
                    dataType = (DataType) ctor.newInstance(new Object[] { getAntProject() });
                }
                ((DataType)dataType).setProject( getAntProject() );

            }
            catch (Throwable t) {
                // ignore
                log.error(t);
            }
        }

        return dataType;
    }

    public Task createTask(String taskName) throws Exception {
        return createTask( taskName,
                           (Class) getAntProject().getTaskDefinitions().get( taskName ) );
    }

    public Task createTask(String taskName,
                           Class taskType) throws Exception {
                            
        if (taskType == null) {
            return null;
        }

        Object o = taskType.newInstance();
        Task task = null;
        if ( o instanceof Task ) {
            task = (Task) o;
        }
        else {
            TaskAdapter taskA=new TaskAdapter();
            taskA.setProxy( o );
            task=taskA;
        }

        task.setProject(getAntProject());
        task.setTaskName(taskName);

        return task;
    }
    
    /**
     * Attempts to look up in the parent hierarchy for a tag that implements the BeanSource interface
     * which creates a dynamic bean, or will return the parent tag, which is also a bean.
     */
    protected Object findBeanAncestor() throws Exception {
        Tag tag = getParent();
        if (tag != null) {
            if (tag instanceof BeanSource) {
                BeanSource beanSource = (BeanSource) tag;
                return beanSource.getBean();
            }
        }
        return tag;
    }
}
