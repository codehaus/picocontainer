/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

import junit.framework.Test;
import junit.framework.TestCase;

import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.service.ServiceException;

import org.nanocontainer.ioc.avalon.AvalonComponentAdapter;
import org.nanocontainer.ioc.avalon.PicoAvalonContractException;
import org.nanocontainer.ioc.avalon.PicoConfigurationException;
import org.nanocontainer.ioc.avalon.PicoContextException;
import org.nanocontainer.ioc.avalon.PicoServiceException;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.PicoInvocationTargetInitializationException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class AvalonComponentAdapterTestCase extends TestCase {
    
    private PicoContainer pico;

    protected void setUp() throws Exception {
        super.setUp();
        pico = new DefaultPicoContainer();
    }
    
    public void testConstructor()
    {
        new AvalonComponentAdapter(this, this.getClass());
    }
    
    public void testVerify()
    {
        new AvalonComponentAdapter(this, this.getClass()).verify(pico);
    }
    
    public void testGetComponentInstance()
    {
        final AvalonComponentAdapter a = new AvalonComponentAdapter(this, this.getClass());
        assertTrue( a.getComponentInstance(pico) instanceof Test );
    }

    public void testGetComponentInstanceInstantiationException()
    {
        final AvalonComponentAdapter a = new AvalonComponentAdapter("mock1", Mock1.class);
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
        final AvalonComponentAdapter a = new AvalonComponentAdapter("mock2", Mock2.class);
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
        final AvalonComponentAdapter a = new AvalonComponentAdapter("mock3", Mock3.class);
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
        final AvalonComponentAdapter a = new AvalonComponentAdapter("mock4", Mock4.class);
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
        final AvalonComponentAdapter a = new AvalonComponentAdapter("mock5", Mock5.class);
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
        final AvalonComponentAdapter a = new AvalonComponentAdapter("mock6", Mock6.class);
        try
        {
            a.getComponentInstance(pico);
            fail( "Expected an exception!" );
        }
        catch( PicoAvalonContractException th )
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
