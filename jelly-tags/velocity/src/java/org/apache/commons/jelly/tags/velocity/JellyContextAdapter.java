package org.apache.commons.jelly.tags.velocity;

/* ====================================================================
 * The Apache Software License, Version 1.1
 *
 * Copyright (c) 2001 The Apache Software Foundation.  All rights
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
 * 3. The end-user documentation included with the redistribution,
 *    if any, must include the following acknowledgment:
 *       "This product includes software developed by the
 *        Apache Software Foundation (http://www.apache.org/)."
 *    Alternately, this acknowledgment may appear in the software itself,
 *    if and wherever such third-party acknowledgments normally appear.
 *
 * 4. The names "Apache" and "Apache Software Foundation" and
 *    "Apache Maven" must not be used to endorse or promote products
 *    derived from this software without prior written permission. For
 *    written permission, please contact apache@apache.org.
 *
 * 5. Products derived from this software may not be called "Apache",
 *    "Apache Maven", nor may "Apache" appear in their name, without
 *    prior written permission of the Apache Software Foundation.
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
 * ====================================================================
 */

import java.util.Set;
import java.util.HashSet;
import java.util.HashMap;

import org.apache.commons.jelly.JellyContext;
import org.apache.velocity.context.Context;

/** 
 * Adapts a JellyContext for use as a Velocity Context.  This context
 * can be used in either read-only or read-write mode.  When used as a
 * read-only adapter, items <tt>put</tt> or <tt>remove</tt>ed from the
 * Velocity context are not permitted to propogate to the JellyContext,
 * which is the default behavior.  The adapter can also be used in a
 * read-write mode.  This permits changes made by Velocity to propogate
 * to the JellyContext.
 * 
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id: JellyContextAdapter.java,v 1.2 2003/03/03 20:49:37 werken Exp $
 */
public class JellyContextAdapter implements Context
{
    /** Flag to indicate read-only or read-write mode */
    private boolean readOnly = true;

    /** The JellyContext being adapted */
    private JellyContext jellyContext;

    /** The store for Velocity in the event the adpater is read-only */
    private HashMap privateContext = new HashMap();

    /** 
     * Constructor.
     * 
     * @param jellyContext The JellyContext to adapt
     */
    public JellyContextAdapter( JellyContext jellyContext )
    {
        this.jellyContext = jellyContext;
    }
    
    /**
     * Sets the read-only flag for this adapter.  If the read-only flag
     * is set, changes to the Velocity Context will not be propogated to
     * the JellyContext.  Turning the read-only flag off enables changes
     * to propogate.  
     *
     * @param readOnly If this parameter is <tt>true</tt>, the adapter
     * becomes read-only.  Setting the parameter to <tt>false</tt> the
     * adapter becomes read-write.
     */
    public void setReadOnly(boolean readOnly)
    {
        this.readOnly = readOnly;
    }
    
    /**
     * Tests if the adapter is read-only.
     *
     * @return <tt>true</tt> if the adpater is read-only; otherwise
     * returns <tt>false</tt>.
     */
    public boolean isReadOnly()
    {
        return readOnly;
    }

    public boolean containsKey( Object key )
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

    public Object get( String key )
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

    public Object[] getKeys()
    {
        Set keys = jellyContext.getVariables().keySet();

        if ( readOnly )
        {
            HashSet combinedKeys = new HashSet( keys );
            combinedKeys.addAll( privateContext.keySet() );
            keys = combinedKeys;
        }

        return keys.toArray();
    }

    public Object put( String key, Object value )
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

    public Object remove( Object key )
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

