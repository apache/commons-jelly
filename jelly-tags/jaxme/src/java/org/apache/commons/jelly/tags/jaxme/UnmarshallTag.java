/**
* Copyright 2004 The Apache Software Foundation.
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*      http://www.apache.org/licenses/LICENSE-2.0
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
 * Tag that generates source from schema.
 * Based on JaxMe XJCTask.
 *
 * @author <a href="mailto:joe@ispsoft.de">Jochen Wiedmann</a>
 * @author <a href="mailto:commons-dev at jakarta.apache.org">Jakarta Commons Development Team</a>
 * @version $Revision: 1.1 $
 */
public class UnmarshallTag extends TagSupport {
        
    private String packages;
    private String var;
    
    public String getPackages() {
        return packages;
    }
    
    public void setPackages(String packages) {
        this.packages = packages;
    }
    
    public String getVar() {
        return var;
    }
    
    public void setVar(String var) {
        this.var = var;
    }
    
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
