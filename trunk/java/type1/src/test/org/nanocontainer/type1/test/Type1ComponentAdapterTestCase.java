/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.type1.test;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceException;

import org.nanocontainer.type1.PicoConfigurationException;
import org.nanocontainer.type1.PicoContextException;
import org.nanocontainer.type1.PicoServiceException;
import org.nanocontainer.type1.PicoType1ContractException;
import org.nanocontainer.type1.Type1ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class Type1ComponentAdapterTestCase extends TestCase {
    
    private PicoContainer pico;

    protected void setUp() throws Exception {
        super.setUp();
        pico = new DefaultPicoContainer();
    }
    
    public void testConstructor()
    {
        new Type1ComponentAdapter(this, this.getClass());
    }
    
    public void testVerify()
    {
        new Type1ComponentAdapter(this, this.getClass()).verify(pico);
    }
    
    public void testGetComponentInstance()
    {
        final Type1ComponentAdapter a = new Type1ComponentAdapter(this, this.getClass());
        assertTrue( a.getComponentInstance(pico) instanceof Test );
    }

    public void testGetComponentInstanceInstantiationException()
    {
        final Type1ComponentAdapter a = new Type1ComponentAdapter("mock1", Mock1.class);
        try
        {
            a.getComponentInstance(pico);
            fail( "Expected an exception!" );
        }
        catch( PicoInvocationTargetInitializationException th )
        {
            assertTrue( th.getCause() instanceof InstantiationException );
        }
    }
    
    public void testGetComponentIllegalAccessException()
    {
        final Type1ComponentAdapter a = new Type1ComponentAdapter("mock2", Mock2.class);
        try
        {
            a.getComponentInstance(pico);
            fail( "Expected an exception!" );
        }
        catch( PicoInvocationTargetInitializationException th )
        {
            assertTrue( th.getCause() instanceof IllegalAccessException );
        }
    }
    
    public void testGetComponentContextException()
    {
        final Type1ComponentAdapter a = new Type1ComponentAdapter("mock3", Mock3.class);
        try
        {
            a.getComponentInstance(pico);
            fail( "Expected an exception!" );
        }
        catch( PicoContextException th )
        {
            assertTrue( th.getCause() instanceof ContextException );
        }
    }
    
    public void testGetComponentServiceException()
    {
        final Type1ComponentAdapter a = new Type1ComponentAdapter("mock4", Mock4.class);
        try
        {
            a.getComponentInstance(pico);
            fail( "Expected an exception!" );
        }
        catch( PicoServiceException th )
        {
            assertTrue( th.getCause() instanceof ServiceException );
        }
    }
    
    public void testGetComponentConfigurationException()
    {
        final Type1ComponentAdapter a = new Type1ComponentAdapter("mock5", Mock5.class);
        try
        {
            a.getComponentInstance(pico);
            fail( "Expected an exception!" );
        }
        catch( PicoConfigurationException th )
        {
            assertTrue( th.getCause() instanceof ConfigurationException );
        }
    }

    public void testGetComponentSomeException()
    {
        final Type1ComponentAdapter a = new Type1ComponentAdapter("mock6", Mock6.class);
        try
        {
            a.getComponentInstance(pico);
            fail( "Expected an exception!" );
        }
        catch( PicoType1ContractException th )
        {
            assertTrue( th.getCause() instanceof SomeException );
        }
    }
    
    public static class Mock1
    {
        public Mock1() throws InstantiationException {
            throw new InstantiationException();
        }
    }

    public static class Mock2
    {
        public Mock2() throws IllegalAccessException {
            throw new IllegalAccessException();
        }
    }

    public static class Mock3
    {
        public Mock3() throws ContextException {
            throw new ContextException("blah");
        }
    }

    public static class Mock4
    {
        public Mock4() throws ServiceException {
            throw new ServiceException("blah", "blah");
        }
    }

    public static class Mock5
    {
        public Mock5() throws ConfigurationException {
            throw new ConfigurationException("blah");
        }
    }

    public static class Mock6
    {
        public Mock6() throws SomeException {
            throw new SomeException();
        }
    }
    
    public static class SomeException extends Exception {}
}
