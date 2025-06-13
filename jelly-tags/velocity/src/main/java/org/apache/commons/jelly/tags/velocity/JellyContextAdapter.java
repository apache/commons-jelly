package org.apache.commons.jelly.tags.velocity;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.jelly.JellyContext;
import org.apache.velocity.context.Context;

/**
 * Adapts a JellyContext for use as a Velocity Context.  This context
 * can be used in either read-only or read-write mode.  When used as a
 * read-only adapter, items <code>put</code> or <code>remove</code>ed from the
 * Velocity context are not permitted to propagate to the JellyContext,
 * which is the default behavior.  The adapter can also be used in a
 * read-write mode.  This permits changes made by Velocity to propagate
 * to the JellyContext.
 */
public class JellyContextAdapter implements Context
{
    /** Flag to indicate read-only or read-write mode */
    private boolean readOnly = true;

    /** The JellyContext being adapted */
    private final JellyContext jellyContext;

    /** The store for Velocity in the event the adapter is read-only */
    private final HashMap privateContext = new HashMap();

    /**
     * Constructs a new instance.
     *
     * @param jellyContext The JellyContext to adapt
     */
    public JellyContextAdapter( final JellyContext jellyContext )
    {
        this.jellyContext = jellyContext;
    }

    /**
     * Sets the read-only flag for this adapter.  If the read-only flag
     * is set, changes to the Velocity Context will not be propagated to
     * the JellyContext.  Turning the read-only flag off enables changes
     * to propagate.
     *
     * @param readOnly If this parameter is <code>true</code>, the adapter
     * becomes read-only.  Setting the parameter to <code>false</code> the
     * adapter becomes read-write.
     */
    public void setReadOnly(final boolean readOnly)
    {
        this.readOnly = readOnly;
    }

    /**
     * Tests if the adapter is read-only.
     *
     * @return <code>true</code> if the adapter is read-only; otherwise
     * returns <code>false</code>.
     */
    public boolean isReadOnly()
    {
        return readOnly;
    }

    @Override
    public boolean containsKey( final Object key )
    {
        if ( key == null )
        {
            return false;
        }

        if ( readOnly && privateContext.containsKey( key ) )
        {
            return true;
        }

        return jellyContext.getVariable( key.toString() ) != null ? true : false;
    }

    @Override
    public Object get( final String key )
    {
        if ( key == null )
        {
            return null;
        }

        if ( readOnly && privateContext.containsKey( key ) )
        {
            return privateContext.get( key );
        }

        return jellyContext.getVariable( key );
    }

    @Override
    public Object[] getKeys()
    {
        Set keys = jellyContext.getVariables().keySet();

        if ( readOnly )
        {
            final HashSet combinedKeys = new HashSet( keys );
            combinedKeys.addAll( privateContext.keySet() );
            keys = combinedKeys;
        }

        return keys.toArray();
    }

    @Override
    public Object put( final String key, final Object value )
    {
        Object oldValue;

        if ( key == null || value == null )
        {
            return null;
        }

        if ( readOnly )
        {
            oldValue = privateContext.put( key, value );
        }
        else
        {
            oldValue = jellyContext.getVariable( key );
            jellyContext.setVariable( key, value );
        }

        return oldValue;
    }

    @Override
    public Object remove( final Object key )
    {
        Object oldValue;

        if ( key == null )
        {
            return null;
        }

        if ( readOnly )
        {
            oldValue = privateContext.remove( key );
        }
        else
        {
            oldValue = jellyContext.getVariable( key.toString() );
            jellyContext.removeVariable( key.toString() );
        }

        return oldValue;
    }
}

