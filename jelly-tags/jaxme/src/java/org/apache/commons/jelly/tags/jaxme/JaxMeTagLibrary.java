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

import org.apache.commons.jelly.TagLibrary;

/** 
 * A Jelly custom tag library that uses the Betwixt project to parse XML and turn it into beans or 
 * turn beans into XML
 *
 * @author <a href="mailto:commons-dev at jakarta.apache.org">Jakarta Commons Development Team</a>
 * @version $Revision: 1.1 $
 */
public class JaxMeTagLibrary extends TagLibrary {
        
    public JaxMeTagLibrary() {
        registerTag( "generator", GeneratorTag.class );
        registerTag( "marshall", MarshallTag.class );
        registerTag( "unmarshall", UnmarshallTag.class );
    }
}
