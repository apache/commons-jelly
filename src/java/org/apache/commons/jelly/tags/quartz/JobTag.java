package org.apache.commons.jelly.tags.quartz;

/*
 * $Header: /home/jerenkrantz/tmp/commons/commons-convert/cvs/home/cvs/jakarta-commons//jelly/src/java/org/apache/commons/jelly/tags/quartz/Attic/JobTag.java,v 1.1 2002/07/25 01:51:20 werken Exp $
 * $Revision: 1.1 $
 * $Date: 2002/07/25 01:51:20 $
 *
 * ====================================================================
 *
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 1999-2001 The Apache Software Foundation.  All rights
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

import org.apache.commons.jelly.XMLOutput;
import org.apache.commons.jelly.MissingAttributeException;

import org.quartz.Scheduler;
import org.quartz.JobDetail;
import org.quartz.JobDataMap;

/** Defines a schedulable job.
 *
 *  @author <a href="mailto:bob@eng.werken.com">bob mcwhirter</a>
 */
public class JobTag extends QuartzTagSupport
{
    // ------------------------------------------------------------
    //     Instance members
    // ------------------------------------------------------------

    /** Group of the job. */
    private String group;

    /** Name of the job. */
    private String name;

    // ------------------------------------------------------------
    //     Constructors
    // ------------------------------------------------------------

    /** Construct.
     */
    public JobTag()
    {
        // intentionally left blank.
    }

    // ------------------------------------------------------------
    //     Instance methods
    // ------------------------------------------------------------

    /** Set the name of this job.
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

    /** Set the group of this job.
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
     *  @throws Exception If an error occurs.
     */
    public void doTag(XMLOutput output) throws Exception
    {
        if ( getName() == null )
        {
            throw new MissingAttributeException( "name" );
        }

        if ( getGroup() == null )
        {
            throw new MissingAttributeException( "group" );
        }

        Scheduler sched = getScheduler();

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

        sched.addJob( detail,
                      true );
    }
}

