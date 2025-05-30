/**
* Licensed to the Apache Software Foundation (ASF) under one or more
* contributor license agreements.  See the NOTICE file distributed with
* this work for additional information regarding copyright ownership.
* The ASF licenses this file to You under the Apache License, Version 2.0
* (the "License"); you may not use this file except in compliance with
* the License.  You may obtain a copy of the License at
* 
*      https://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.apache.commons.jelly.tags.jaxme;

import java.io.File;
import java.io.IOException;

import javax.xml.bind.UnmarshallerHandler;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.JAXBException;
import javax.xml.bind.JAXBContext;

import org.apache.commons.jelly.TagSupport;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.JellyTagException;

import org.apache.ws.jaxme.generator.Generator;
import org.apache.ws.jaxme.generator.SchemaReader;
import org.apache.ws.jaxme.generator.sg.impl.JAXBSchemaReader;
import org.apache.ws.jaxme.generator.sg.SchemaSG;
import org.apache.ws.jaxme.generator.impl.GeneratorImpl;

import org.xml.sax.SAXException;

/** 
 * <p>Unmarshalls XML documents into java objects.</p>
 * <p>
 * This tag unmarshalls the XML content contained 
 * into the JaxMe generated java objects in the packages specified.
 * </p>
 */
public class UnmarshallTag extends TagSupport {
       
    private String packages;
    private String var;
    
    public String getPackages() {
        return packages;
    }
    
    /**
     * Defines the generated objects to which the XML should be unmarshalled. 
     */
    public void setPackages(String packages) {
        this.packages = packages;
    }
    
    public String getVar() {
        return var;
    }
    
    /**
     * Sets the name of the jelly variable to which 
     * the unmarshalled java object should be bound.
     */
    public void setVar(String var) {
        this.var = var;
    }
    
    @Override
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException {
        if (packages == null) {
            throw new MissingAttributeException( "packages" );
        }
        if ( var == null ) {
            throw new MissingAttributeException( "var" );
        }
        try {   

            JAXBContext jaxbContext = JAXBContext.newInstance(packages);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            UnmarshallerHandler handler = unmarshaller.getUnmarshallerHandler();
            
            handler.startDocument();
            
            XMLOutput newOutput = new XMLOutput( handler );
            
            invokeBody(newOutput);
            handler.endDocument();
            
            Object result = handler.getResult();
            context.setVariable( var, result );
            
        } catch (JAXBException ex)  {
            throw new JellyTagException(ex);
        } catch (SAXException ex) {
            throw new JellyTagException(ex);
        }
        
    }
}
