package org.apache.commons.jelly.tags.quartz;

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

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.MissingAttributeException;

import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.JobDetail;
import org.quartz.JobDataMap;

/** Defines a schedulable job.
 */
public class JobTag extends QuartzTagSupport
{
    /** Group of the job. */
    private String group;

    /** Name of the job. */
    private String name;

    /** Construct.
     */
    public JobTag()
    {
        // intentionally left blank.
    }

    /** Sets the name of this job.
     *
     *  @param name The name of this job.
     */
    public void setName(String name)
    {
        this.name = name;
    }

    /** Retrieve the name of this job.
     *
     *  @return The name of this job.
     */
    public String getName()
    {
        return this.name;
    }

    /** Sets the group of this job.
     *
     *  @param group The group of this job.
     */
    public void setGroup(String group)
    {
        this.group = group;
    }

    /** Retrieve the group of this job.
     *
     *  @return The group of this job.
     */
    public String getGroup()
    {
        return this.group;
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     org.apache.commons.jelly.Tag
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /** Perform this tag.
     *
     *  @param output Output sink.
     *
     *  @throws MissingAttributeException If an error occurs.
     *  @throws JellyTagException If an error occurs.
     */
    @Override
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException
    {
        if ( getName() == null )
        {
            throw new MissingAttributeException( "name" );
        }

        if ( getGroup() == null )
        {
            throw new MissingAttributeException( "group" );
        }

        JobDetail detail = new JobDetail( getName(),
                                          getGroup(),
                                          JellyJob.class );

        detail.setDurability( true );

        JobDataMap data = new JobDataMap();

        data.put( "jelly.output",
                  output );

        data.put( "jelly.context",
                  getContext() );

        data.put( "jelly.script",
                  getBody() );

        detail.setJobDataMap( data );

        try {
            Scheduler sched = getScheduler();
            sched.addJob( detail,
                          true );
        }
        catch (SchedulerException e) {
            throw new JellyTagException(e);
        }
    }
}

