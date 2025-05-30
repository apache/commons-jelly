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
package org.apache.commons.jelly.tags.core;

import org.apache.commons.jelly.TagLibrary;

/**
  * This is the core tag library for jelly and contains commonly
  * used tags.
  * This class could be generated by XDoclet
  */
public class CoreTagLibrary extends TagLibrary {

    public CoreTagLibrary() {
        registerTag("jelly", JellyTag.class);

        // core tags
        registerTag("out", ExprTag.class);
        registerTag("catch", CatchTag.class);
        registerTag("forEach", ForEachTag.class);
        registerTag("set", SetTag.class);
        registerTag("remove", RemoveTag.class);
        registerTag("while", WhileTag.class);

        // conditional tags
        registerTag("if", IfTag.class);
        registerTag("choose", ChooseTag.class);
        registerTag("when", WhenTag.class);
        registerTag("otherwise", OtherwiseTag.class);
        registerTag("switch", SwitchTag.class);
        registerTag("case", CaseTag.class);
        registerTag("default", DefaultTag.class);

        // other tags
        registerTag("include", IncludeTag.class);
        registerTag("import", ImportTag.class);
        registerTag("mute", MuteTag.class);

        // extensions to JSTL
        registerTag("arg", ArgTag.class);
        registerTag("break", BreakTag.class);
        registerTag("expr", ExprTag.class);
        registerTag("file", FileTag.class);
        registerTag("getStatic", GetStaticTag.class);
        registerTag("invoke", InvokeTag.class);
        registerTag("invokeStatic", InvokeStaticTag.class);
        registerTag("new", NewTag.class);
        registerTag("parse", ParseTag.class);
        registerTag("scope", ScopeTag.class);
        registerTag("setProperties", SetPropertiesTag.class);
        registerTag("thread", ThreadTag.class);
        registerTag("useBean", UseBeanTag.class);
        registerTag("useList", UseListTag.class);
        registerTag("whitespace", WhitespaceTag.class);
    }

}
