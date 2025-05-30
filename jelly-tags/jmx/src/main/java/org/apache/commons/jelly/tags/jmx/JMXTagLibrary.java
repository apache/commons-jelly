/*
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
package org.apache.commons.jelly.tags.jmx;

import javax.management.ObjectName;

import org.apache.commons.beanutils2.ConvertUtils;
import org.apache.commons.jelly.tags.bean.BeanTag;
import org.apache.commons.jelly.tags.bean.BeanTagLibrary;

/**
 * A Tag library for creating an instantiating Java Beans and MBeans
 * and registering them with JMX. Support for setting JMX attributes
 * and invoking JMX operations is also supported.
 */
public class JMXTagLibrary extends BeanTagLibrary {

    static {
        // register the various BeanUtils Converters from Strings to various JMX types
        ConvertUtils.register( new ObjectNameConverter(), ObjectName.class );
    }

    public JMXTagLibrary() {
        registerTag("mbean", BeanTag.class);
        registerTag("operation", OperationTag.class);
        registerTag("register", RegisterTag.class);
        registerTag("server", ServerTag.class);
    }
}
