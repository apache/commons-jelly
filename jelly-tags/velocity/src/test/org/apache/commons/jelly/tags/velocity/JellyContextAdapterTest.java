package org.apache.commons.jelly.tags.velocity;

/* ====================================================================
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
 */

import java.util.Set;
import java.util.HashSet;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.apache.commons.jelly.JellyContext;
import org.apache.commons.collections.CollectionUtils;

/**
 * Unit test for <code>JellyContextAdapter</code>.
 *
 * @author <a href="mailto:pete-apache-dev@kazmier.com">Pete Kazmier</a>
 * @version $Id: JellyContextAdapterTest.java,v 1.1 2003/01/07 13:04:46 dion Exp $
 */
public class JellyContextAdapterTest extends TestCase
{
    JellyContext jellyContext;
    JellyContextAdapter adapter;

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public JellyContextAdapterTest( String testName )
    {
        super( testName );
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite()
    {
        return new TestSuite( JellyContextAdapterTest.class );
    }

    public void setUp()
    {
        jellyContext = new JellyContext();
        adapter = new JellyContextAdapter( jellyContext );
    }

    /** 
     * Test the behavior of null keys.
     */
    public void testNullKey()
    {
        adapter.setReadOnly( false );
        adapter.put( null, new Object() );

        assertTrue( adapter.get( null ) == null );
    }

    /** 
     * Test the behavior of null values.
     */
    public void testNullValue()
    {
        adapter.setReadOnly( false );
        adapter.put( "key", null );

        assertTrue( adapter.get( "key" ) == null );
    }

    /** 
     * Test that items can be added and retrieved from a read-write
     * adpater.  Also verify the key/value pair was actually inserted
     * into the JellyContext.
     */
    public void testReadWritePut()
    {
        Object value = new Object();

        adapter.setReadOnly( false );
        adapter.put( "key", value );

        assertTrue( "adapter: did not return the original value",
                adapter.get( "key" ) == value );

        assertTrue( "jellyContext: did not return the original value",
                jellyContext.getVariable( "key" ) == value );
    }

    /** 
     * Test that items can be added and retrieved from a read-only
     * adapter.  Also verify the key/value pair was not inserted into
     * the JellyContext.
     */
    public void testReadOnlyPut()
    {
        Object value = new Object();

        adapter.setReadOnly( true );
        adapter.put( "key", value );

        assertTrue( "adapter: did not return the original value",
                adapter.get( "key" ) == value );

        assertTrue( "jellyContext: must return null when adapter is readonly",
                jellyContext.getVariable( "key" ) == null );
    }

    /** 
     * Test that items can be removed from a read-write context.  Also
     * verify that the item is removed from the JellyContext.
     */
    public void testReadWriteRemove()
    {
        Object value = new Object();

        adapter.setReadOnly( false );
        adapter.put( "key", value );
        Object oldValue = adapter.remove( "key" );

        assertTrue( "Value returned from remove() is not the original",
                value == oldValue );

        assertTrue( "adapter: after removal of key, value should be null",
                adapter.get( "key" ) == null );

        assertTrue( "jellyContext: after removal of key, value should be null",
                jellyContext.getVariable( "key" ) == null );

        assertTrue( "Removal of non-existent key should return null",
                adapter.remove( "non-existent key" ) == null );
    }

    /** 
     * Test that items can be removed from a read-only context.  Also
     * verify that the JellyContext is not impacted by removal of keys.
     */
    public void testReadOnlyRemove()
    {
        Object value = new Object();

        adapter.setReadOnly( true );
        adapter.put( "key", value );

        Object oldValue = adapter.remove( "key" );

        assertTrue( "Value returned from remove() is not the original", 
                value == oldValue );

        assertTrue( "adapter: after removal of key, value should be null",
                adapter.get( "key" ) == null );

        assertTrue( "jellyContext: value should not be affected.", 
                jellyContext.getVariable( "key" ) == null );

        assertTrue( "Removal of non-existent key should return null",
                adapter.remove( "non-existent key" ) == null );
    }

    /** 
     * Test that items can shadow or hide items in the JellyContext.
     * Removal of a key in the private context will unveil the key in
     * the JellyContext if it exists.
     */
    public void testReadOnlyShadowingRemove()
    {
        Object value1 = new Object();
        Object value2 = new Object();

        adapter.setReadOnly( true );
        adapter.put( "key", value1 );
        jellyContext.setVariable( "key", value2 );

        assertTrue( "adapter: before removal of key, value should be 1",
                adapter.get( "key" ) == value1 );

        adapter.remove( "key" );

        assertTrue( "adapter: after removal of key, value should be 2",
                adapter.get( "key" ) == value2 );

        assertTrue( "jellyContext: value should not be affected.", 
                jellyContext.getVariable( "key" ) == value2 );
    }

    /** 
     * Test the containsKey method in a read-write adapter.
     */
    public void testReadWriteContainsKey()
    {
        Object value1 = new Object();
        Object value2 = new Object();

        adapter.setReadOnly( false );
        adapter.put( "key1", value1 );
        jellyContext.setVariable( "key2", value2 );

        assertTrue( "adapter: did not contain the key",
                adapter.containsKey( "key1" ) );

        assertTrue( "adapter: should contain the key",
                adapter.containsKey( "key2" ) );

        assertTrue( "jellyContext: did not contain the key",
                jellyContext.getVariable( "key1" ) != null );
    }

    /** 
     * Test the containsKey method in a read-only adapter.
     */
    public void testReadOnlyContainsKey()
    {
        Object value1= new Object();
        Object value2 = new Object();

        adapter.setReadOnly( true );
        adapter.put( "key1", value1 );
        jellyContext.setVariable( "key2", value2 );

        assertTrue( "adapter: did not contain the key",
                adapter.containsKey( "key1" ) );

        assertTrue( "adapter: should not contain the key",
                adapter.containsKey( "key2" ) );

        assertTrue( "jellyContext: should not contain the key",
                jellyContext.getVariable( "key1" ) == null );
    }

    /** 
     * Test the getKeys method of a read-write adapter.
     */
    public void testReadWriteGetKeys()
    {
        Object value1 = new Object();
        Object value2 = new Object();
        Object value3 = new Object();

        adapter.setReadOnly( false );
        adapter.put( "key1", value1 );
        adapter.put( "key2", value2 );
        jellyContext.setVariable( "key3", value3 );

        Set expectedKeys = new HashSet();
        expectedKeys.add( "key1" );
        expectedKeys.add( "key2" );
        expectedKeys.add( "key3" );
        
        Set actualKeys = new HashSet();
        CollectionUtils.addAll(actualKeys, adapter.getKeys());

        assertTrue( "adapter: does not contain the correct key set",
                actualKeys.containsAll( expectedKeys ) );
    }

    /** 
     * Test the getKeys method of a read-only adapter.
     */
    public void testReadOnlyGetKeys()
    {
        Object value1 = new Object();
        Object value2 = new Object();
        Object value3 = new Object();

        adapter.setReadOnly( true );
        adapter.put( "key1", value1 );
        adapter.put( "key2", value2 );
        jellyContext.setVariable( "key3", value3 );

        Set expectedKeys = new HashSet();
        expectedKeys.add( "key1" );
        expectedKeys.add( "key2" );
        
        Set actualKeys = new HashSet();
        CollectionUtils.addAll(actualKeys, adapter.getKeys());

        assertTrue( "adapter: does not contain the correct key set",
                actualKeys.containsAll( expectedKeys ) );
    }
}
