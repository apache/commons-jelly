package org.apache.commons.jelly.tags.quartz;

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

import org.apache.commons.jelly.TagSupport;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;

/** Basic support for all tags requiring a Quartz scheduler.
 */
public abstract class QuartzTagSupport extends TagSupport
{
    /** The scheduler variable name in the JellyContext. */
    public static final String SCHED_VAR_NAME = "org.apache.commons.jelly.quartz.Scheduler";

    /** Retrieve or create a scheduler.
     *
     *  <p>
     *  If a scheduler has already been created an installed
     *  in the variable {@link #SCHED_VAR_NAME}, then that scheduler
     *  will be returned.  Otherwise, a new StdScheduler will be
     *  created, started, and installed.  Additionally, a runtime
     *  shutdown hook will be added to cleanly shutdown the scheduler.
     *
     *  @return The scheduler.
     *
     *  @throws SchedulerException If there is an error creating the
     *          scheduler.
     */
    public Scheduler getScheduler() throws SchedulerException
    {
        Scheduler sched = (Scheduler) getContext().getVariable( SCHED_VAR_NAME );

        if ( sched == null )
        {
            final StdSchedulerFactory factory = new StdSchedulerFactory();

            final Scheduler newSched = factory.getScheduler();

            sched = newSched;

            getContext().setVariable( SCHED_VAR_NAME,
                                      newSched );

            Runtime.getRuntime().addShutdownHook(
                new Thread() {
                    @Override
                    public void run()
                    {
                        try
                        {
                            if ( ! newSched.isShutdown() )
                            {
                                newSched.shutdown();
                            }
                        }
                        catch (final SchedulerException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                );
            newSched.start();
        }

        return sched;
    }
}

