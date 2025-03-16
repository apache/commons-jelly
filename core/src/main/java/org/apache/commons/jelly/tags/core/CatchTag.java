/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.jelly.tags.core;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;

/**
 * A tag which catches exceptions thrown by its body.
 * This allows conditional logic to be performed based on if exceptions
 * are thrown or to do some kind of custom exception logging logic.
 */
public class CatchTag extends TagSupport {

    /**
     * Exception class list separated by ";"
     */
	private String exceptions;
	
	/**
	 * 
	 */
	private Class[] exceptionArray;
	/**
	 * Var to store cause exception class
	 */
	private String cause;
	
    private String var;

    public CatchTag() {
    }

    // Tag interface
    //-------------------------------------------------------------------------
    @Override
    public void doTag(XMLOutput output) throws JellyTagException {

        /**
         * Buid exception set
         */
		if ( exceptionArray == null ) {
			try {
			    buildExceptionArray();
			} catch (ClassNotFoundException e) {
				throw new JellyTagException(e);
			}
		}
		
        if (var != null) {
            context.removeVariable(var);
        }
        
	    if ( cause != null ) {
	        context.removeVariable(cause);
	    }
        try {
            invokeBody(output);
        }
        catch (Throwable t) {        	
            if (var != null) {
                context.setVariable(var, t);
            }
            
    		Throwable c = getRealException(t); 
    		
		    if ( cause != null ) {
		        context.setVariable( cause, c );
		    }
		    
            if ( exceptionArray != null ) {

        	    /**
        	     * if exception is not expected throw exception 
        	     */    		    
        		if ( ! isExpected(c)) {
        			throw new JellyTagException(t);
        		}        		
        	}            
        }
    }

    // Properties
    //-------------------------------------------------------------------------

    /**
     * Sets the name of the variable which is exposed with the Exception that gets
     * thrown by evaluating the body of this tag or which is set to null if there is
     * no exception thrown.
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    /**
     * Dissect Exception stack to get the real exception throughout the JellyTagException wrapping
     * @param t
     * @return the first exception in stack that's not a JellyTagException
     */
    protected Throwable getRealException(Throwable t) {
        Throwable c = t.getCause();
        Throwable realException = null; 
        
        if ( c != null ) {
            if ( c instanceof JellyTagException ) {
                realException = getRealException(c);
                
                if ( realException == null ) {
                    realException = c;
                }
                
            } else {
                realException = c;
            }
        }
        return realException;
    }

	
	/**
	 * Build exception classes set
	 * @throws ClassNotFoundException
	 * 
	 */
	private void buildExceptionArray() throws ClassNotFoundException {
		if ( exceptions != null ) {
		    String[] strings = exceptions.split(";");
		    
			if ( exceptionArray == null ) {
			    
				int size = ( strings.length > 0) ? strings.length : 1 ;
				exceptionArray = new Class[size];
				
				for ( int i = 0; i < strings.length; i ++) {
					Class clazz = Class.forName(strings[i]);
					exceptionArray[i] = clazz;
				}				
			}
		}
	}	

	/**
	 * @return the exceptions.
	 */
	public String getExceptions() {
		return exceptions;
	}
	/**
	 * @param exceptionList The exceptions to set. Must be separated by ";"
	 */
	public void setExceptions(String exceptionList) {
		this.exceptions = exceptionList;
	}
    
    /**
     * @param cause The cause to set.
     */
    public void setCause(String cause) {
        this.cause = cause;
    }
    
    /**
     * 
     * @param t
     * @return true if t is expected
     */
    public boolean isExpected(Throwable t) {
        if ( exceptionArray == null ) {
            return true;
        }
        Class clazz = null;
        for ( int i = 0; i < exceptionArray.length; i ++ ) {
            clazz = exceptionArray[i];
            if ( clazz.isAssignableFrom(t.getClass())){
                return true;
            }
        }
        return false;
    }
}
