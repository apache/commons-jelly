
package org.apache.commons.jelly.tags.ant;

import org.apache.commons.beanutils.ConvertingWrapDynaBean;

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.commons.beanutils.MethodUtils;
import org.apache.commons.beanutils.PropertyUtils;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

import java.lang.reflect.Method;

/** Tag supporting ant's dynamic runtime behaviour for
 *  'unknown' tags.
 *
 *  <p>
 *  This tag duplicates much of the functionality from
 *  TaskTag and DataTypeTag and should probably be refactored.
 *  </p>
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class OtherAntTag extends AntTagSupport {

    private static final Class[] addTaskParamTypes = { String.class };

    /** The general object underlying this tag. */
    protected Object object;

    /** The name of this tag. */
    protected String tagName;

    /** Task, if this tag represents a task. */
    protected Task task;

    /** Construct with a project and tag name.
     *
     *  @param project The Ant project.
     *  @param tagName The name on the tag.
     */
    public OtherAntTag(Project project,
                       String tagName) {
        super( project );
        this.tagName = tagName;
    }

    public void doTag(XMLOutput output) throws Exception {

        Object obj = getObject();

        if ( this.task != null ) {
            Method method = MethodUtils.getAccessibleMethod( this.task.getClass(),
                                                             "addText",
                                                             addTaskParamTypes );            
            
            if (method != null) {
                String text = getBodyText();
                
                Object[] args = { text };
                method.invoke(this.task, args);
            } else {
                getBody().run(context, output);
            }
            
            this.task.perform(); 
        } else {
            getBody().run( context, output );

            AntTagSupport parent = (AntTagSupport) findAncestorWithClass(AntTagSupport.class);

            if ( parent != null ) {
                // otherwise it -must- be a top-level, non-parented datatype.
                
                Object parentObj =  parent.getObject();
                
                if ( parentObj != null )
                {
                    IntrospectionHelper ih = IntrospectionHelper.getHelper( parentObj.getClass() );
                    
                    try
                    {
                        ih.storeElement( getAntProject(),
                                         parentObj,
                                         getObject(),
                                         getTagName() );
                    }
                    catch (Exception e) {
                        
                    }
                }
            }
        }
    }

    public String getTagName() {
        return this.tagName;
    }

    /** Retrieve the general object underlying this tag.
     *
     *  @return The object underlying this tag.
     */
    public Object getObject() {
        return this.object;
    }
    
    /** Set the object underlying this tag.
     *
     *  @param object The object.
     */
    public void setObject(Object object) {
        this.object = object;
        setDynaBean( new ConvertingWrapDynaBean( object ) );
    }

    public void beforeSetAttributes() throws Exception {

        Project project = getAntProject();
        String  tagName = getTagName();

        if ( project.getTaskDefinitions().containsKey( tagName ) ) {
            Task task = createTask( tagName );

            if ( task instanceof TaskAdapter ) {
                setObject( ((TaskAdapter)task).getProxy() );
            } else {
                setObject( task );
            }
            this.task = task;
            return;
            
        } else {
            // must be a datatype.

            AntTagSupport ancestor = (AntTagSupport) findAncestorWithClass( AntTagSupport.class );

            Object nested = null;
            
            if ( ancestor != null ) {
                nested = ancestor.createNestedObject( getTagName() );
            }
            
            if ( nested == null ) {
                nested = createDataType( getTagName() );
            }

            if ( nested != null ) {
                setObject( nested );

                try
                {
                    PropertyUtils.setProperty( nested, "name", "goober" );
                }
                catch (Exception e) {
                }
                return;
            } 
        }
    }

    public void setAttribute(String name, Object value) throws Exception {

        // System.err.println( this + " OtherAntTag.setAttribute(" + name + ", " + value + ")" );
        if ( "id".equals( name ) ) {
            try
            {
                Object obj = getObject();

                getAntProject().addReference( (String) value, getObject() );
            }
            catch (Exception e)
            {
                e.printStackTrace();
                // ignore?
            }
            return;
        }
        
        super.setAttribute( name, value );
    }

    public String toString()
    {
        return "[OtherAntTag: name=" + getTagName() + "]";
    }
    
}

    
