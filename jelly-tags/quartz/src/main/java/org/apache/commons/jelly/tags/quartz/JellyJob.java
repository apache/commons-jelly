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

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.jelly.Script;
import org.apache.commons.jelly.XMLOutput;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/** Implementation of a quart {@code Job} to execute jellyscript.
 */
public class JellyJob implements Job
{

    /** Construct.
     */
    public JellyJob()
    {
        // intentionally left blank.
    }

    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
    //     org.quartz.Job
    // - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -

    /** Execute this job.
     *
     * @param jobContext Job context data.
     *
     * @throws JobExecutionException If an error occurs during job execution.
     */
    @Override
    public void execute(final JobExecutionContext jobContext) throws JobExecutionException
    {

        final JobDetail  detail = jobContext.getJobDetail();

        final JobDataMap data   = detail.getJobDataMap();

        final Script script = (Script) data.get( "jelly.script" );

        final JellyContext jellyContext = (JellyContext) data.get( "jelly.context" );

        final XMLOutput    output       = (XMLOutput) data.get( "jelly.output" );

        try
        {
            script.run( jellyContext,
                        output );
            output.flush();
        }
        catch (final Exception e)
        {
            e.printStackTrace();
            throw new JobExecutionException( e,
                                             false );
        }
    }
}
