
package org.apache.commons.jelly.tags.ant;

import org.apache.commons.jelly.DynaBeanTagSupport;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.TaskAdapter;
import org.apache.tools.ant.types.DataType;

import org.apache.commons.beanutils.MethodUtils;

import java.lang.reflect.Method;

public abstract class AntTagSupport extends DynaBeanTagSupport {

    private Project project;

    public AntTagSupport() {
        
    }
    public AntTagSupport(Project project) {
        this.project = project;
    }

    public void setAntProject(Project project) {
        this.project = project;
    }

    public Project getAntProject() {
        return this.project;
    }

    public Object createNestedObject(String name) throws Exception {

        Object object = getObject();

        IntrospectionHelper ih = IntrospectionHelper.getHelper( object.getClass() );

        Object dataType = null;

        try {
            dataType = ih.createElement( getAntProject(), object, name );
        } catch (Exception e) {
            dataType = null;
            e.printStackTrace();
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
        if( o instanceof Task ) {
            task=(Task)o;
        } else {
            TaskAdapter taskA=new TaskAdapter();
            taskA.setProxy( o );
            task=taskA;
        }

        task.setProject(getAntProject());
        task.setTaskName(taskName);

        return task;
    }

    public void setAttribute(String name, Object value) {

        Object obj = null;

        try {
            obj = getObject();
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if ( obj == null ) {
            return;
        }
        
        IntrospectionHelper ih = IntrospectionHelper.getHelper( obj.getClass() );

        if ( value instanceof String ) {
            try {
                ih.setAttribute( getAntProject(), obj, name.toLowerCase(), (String) value );
                return;
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }

        try {
            ih.storeElement( getAntProject(), obj, value, name );
        } catch (Exception e) {
            // e.printStackTrace();

            try
            {
                super.setAttribute( name, value );
            }
            catch (Exception f) {
                // f.printStackTrace();
            }
        }
    }
    
    public abstract Object getObject() throws Exception;
}
