package org.apache.commons.jelly.tags.quartz;

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/jelly-tags/quartz/src/java/org/apache/commons/jelly/tags/quartz/CronTriggerTag.java,v 1.2 2003/01/26 07:01:35 morgand Exp $
 * $Revision: 1.2 $
 * $Date: 2003/01/26 07:01:35 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2002 The Apache Software Foundation.  All rights
 * reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * 3. The end-user documentation included with the redistribution, if
 *    any, must include the following acknowlegement:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowlegement may appear in the software itself,
 *    if and wherever such third-party acknowlegements normally appear.
 *
 * 4. The names "The Jakarta Project", "Commons", and "Apache Software
 *    Foundation" must not be used to endorse or promote products derived
 *    from this software without prior written permission. For written
 *    permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache"
 *    nor may "Apache" appear in their names without prior written
 *    permission of the Apache Group.
 *
 * THIS SOFTWARE IS PROVIDED ``AS IS'' AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */

import java.text.ParseException;
 
import org.apache.commons.jelly.JellyTagException;
import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.MissingAttributeException;

import org.quartz.CronTrigger;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;

import java.util.Date;

/** Define a trigger using a cron time spec.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class CronTriggerTag extends QuartzTagSupport
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

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

    // ------------------------------------------------------------
    //     COnstructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public CronTriggerTag()
    {
        // intentionally left blank.
    }

    // ------------------------------------------------------------
    // ------------------------------------------------------------

    /** Set the name.
     *
     *  @param name.
     */
    public void setName(String name)
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

    /** Set the group
     *
     *  @param group The group
     */
    public void setGroup(String group)
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

    /** Set the cron time spec.
     *
     *  @param spec The cron time spec.
     */
    public void setSpec(String spec)
    {
        this.spec = spec;
    }

    /** Retrieve the cron time spec.
     *
     *  @param spec The cron time spec.
     */
    public String getSpec()
    {
        return this.spec;
    }

    /** Set the job name.
     *
     *  @param jobName The job name.
     */
    public void setJobName(String jobName)
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

    /** Set the job group.
     *
     *  @param jobGroup The job group.
     */
    public void setJobGroup(String jobGroup)
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
     *  @throws Exception If an error occurs.
     */
    public void doTag(XMLOutput output) throws MissingAttributeException, JellyTagException
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

        CronTrigger trigger = new CronTrigger( getName(),
                                               getGroup() );
        try {
            trigger.setCronExpression( getSpec() );
        }
        catch (ParseException e) {
            throw new JellyTagException(e);
        }
        trigger.setJobName( getJobName() );
        trigger.setJobGroup( getJobGroup() );
        trigger.setStartTime( new Date() );
        
        try {
            Scheduler sched = getScheduler();
            sched.scheduleJob( trigger );
        }
        catch (SchedulerException e) {
            throw new JellyTagException(e);
        }
    }
}
