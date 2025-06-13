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

import java.text.ParseException;
import java.util.Date;

import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.MissingAttributeException;
import org.apache.commons.jelly.XMLOutput;
import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

/** Define a trigger using a cron time spec.
 */
public class CronTriggerTag extends QuartzTagSupport
{
    /** Cron time spec. */
    private String spec;

    /** Trigger name. */
    private String name;

    /** Trigger group. */
    private String group;

    /** Job name. */
    private String jobName;

    /** Job group. */
    private String jobGroup;

    /** Construct.
     */
    public CronTriggerTag()
    {
        // intentionally left blank.
    }

    /** Sets the name.
     *
     *  @param name.
     */
    public void setName(final String name)
    {
        this.name = name;
    }

    /** Retrieve the name.
     *
     *  @return The name.
     */
    public String getName()
    {
        return this.name;
    }

    /** Sets the group
     *
     *  @param group The group
     */
    public void setGroup(final String group)
    {
        this.group = group;
    }

    /** Retrieve the group.
     *
     *  @return The group.
     */
    public String getGroup()
    {
        return this.group;
    }

    /** Sets the cron time spec.
     *
     *  @param spec The cron time spec.
     */
    public void setSpec(final String spec)
    {
        this.spec = spec;
    }

    /** Retrieve the cron time spec.
     */
    public String getSpec()
    {
        return this.spec;
    }

    /** Sets the job name.
     *
     *  @param jobName The job name.
     */
    public void setJobName(final String jobName)
    {
        this.jobName = jobName;
    }

    /** Retrieve the job name.
     *
     *  @return The job name.
     */
    public String getJobName()
    {
        return this.jobName;
    }

    /** Sets the job group.
     *
     *  @param jobGroup The job group.
     */
    public void setJobGroup(final String jobGroup)
    {
        this.jobGroup = jobGroup;
    }

    /** Retrieve the job group.
     *
     *  @return The job group.
     */
    public String getJobGroup()
    {
        return this.jobGroup;
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
    public void doTag(final XMLOutput output) throws MissingAttributeException, JellyTagException
    {
        if ( getSpec() == null )
        {
            throw new MissingAttributeException( "spec" );
        }

        if ( getName() == null )
        {
            throw new MissingAttributeException( "name" );
        }

        if ( getGroup() == null )
        {
            throw new MissingAttributeException( "group" );
        }

        if ( getJobName() == null )
        {
            throw new MissingAttributeException( "jobName" );
        }

        if ( getJobGroup() == null )
        {
            throw new MissingAttributeException( "jobGroup" );
        }

        final CronTrigger trigger = new CronTrigger( getName(),
                                               getGroup() );
        try {
            trigger.setCronExpression( getSpec() );
        }
        catch (final ParseException e) {
            throw new JellyTagException(e);
        }
        trigger.setJobName( getJobName() );
        trigger.setJobGroup( getJobGroup() );
        trigger.setStartTime( new Date() );

        try {
            final Scheduler sched = getScheduler();
            sched.scheduleJob( trigger );
        }
        catch (final SchedulerException e) {
            throw new JellyTagException(e);
        }
    }
}
