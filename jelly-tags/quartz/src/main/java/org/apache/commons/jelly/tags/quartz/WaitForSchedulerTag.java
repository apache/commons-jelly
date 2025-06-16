package org.apache.commons.jelly.tags.quartz;

import org.apache.commons.jelly.JellyTagException;

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

import org.apache.commons.jelly.XMLOutput;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/** Block and wait for the Quartz scheduler to shutdown.
 */
public class WaitForSchedulerTag extends QuartzTagSupport
{
    /** Construct.
     */
    public WaitForSchedulerTag()
    {
        // intentionally left blank.
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     org.apache.commons.jelly.Tag
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /** Perform this tag.
     *
     *  @param output Output sink.
     *
     *  @throws JellyTagException If an error occurs.
     */
    @Override
    public void doTag(final XMLOutput output) throws JellyTagException
    {
        try {
            final Scheduler sched = getScheduler();

            while ( ! sched.isShutdown() )
            {
                try
                {
                    Thread.sleep( 500 );
                }
                catch (final InterruptedException e)
                {
                    break;
                }
            }
        }
        catch (final SchedulerException e) {
            throw new JellyTagException(e);
        }
    }
}

