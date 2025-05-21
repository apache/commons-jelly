/*
 * Copyright 2002,2004 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * Created on 18 nov. 2004
 */
package org.apache.commons.jelly.core;

import java.lang.reflect.Constructor;

/**
 * TODO Do documentation.
 * @version 0.0
 */
public class ModularExceptionBean {

    public static void main(String[] args) {
        
        ModularExceptionBean bean = new ModularExceptionBean();
        bean.setException(NullPointerException.class.getName());
        
        try {
            bean.throwIt("Boooooooo");
            
        } catch ( Throwable e) {
            System.out.println(e.getMessage());
        }
    }
    
    
    private String exception;
    public ModularExceptionBean(){
        
    }
    
    /**
     * @return the exception.
     */
    public String getException() {
        return exception;
    }
    
    
    /**
     * @param exception The exception to set.
     */
    public void setException(String exception) {
        this.exception = exception;
    }
    public void throwIt(String message) throws Throwable{
        if ( exception != null ) {
            Class clazz = Class.forName(exception);
            Constructor c = clazz.getConstructor( new Class[] {String.class});
            Object obj = c.newInstance(new Object[] {message});
            throw (Throwable) obj;
        }
    }
}
