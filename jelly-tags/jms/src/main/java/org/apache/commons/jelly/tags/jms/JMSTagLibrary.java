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
package org.apache.commons.jelly.tags.jms;

import org.apache.commons.jelly.TagLibrary;

/** Describes the Taglib. This class could be generated by XDoclet
  */
public class JMSTagLibrary extends TagLibrary {

    public JMSTagLibrary() {
        registerTag("connection", ConnectionTag.class);
        registerTag("destination", DestinationTag.class);
        registerTag("mapEntry", MapEntryTag.class);
        registerTag("mapMessage", MapMessageTag.class);
        registerTag("message", MessageTag.class);
        registerTag("onMessage", OnMessageTag.class);
        registerTag("objectMessage", ObjectMessageTag.class);
        registerTag("property", PropertyTag.class);
        registerTag("receive", ReceiveTag.class);
        registerTag("send", SendTag.class);
        registerTag("stopwatch", StopwatchTag.class);
        registerTag("subscribe", SubscribeTag.class);
        registerTag("textMessage", TextMessageTag.class);
    }
}
