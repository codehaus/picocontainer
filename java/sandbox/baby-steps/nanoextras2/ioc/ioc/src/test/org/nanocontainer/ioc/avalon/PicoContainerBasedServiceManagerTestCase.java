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

import junit.framework.TestCase;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.DefaultServiceManager;
import org.easymock.MockControl;
import org.nanocontainer.ioc.avalon.NullArgumentException;
import org.nanocontainer.ioc.avalon.PicoContainerBasedServiceManager;
import org.picocontainer.*;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.UnsatisfiableDependenciesException;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class PicoContainerBasedServiceManagerTestCase extends TestCase {
    public void testConstructor()
    {
        new PicoContainerBasedServiceManager(new DefaultPicoContainer());
        try
        {
            new PicoContainerBasedServiceManager(null);
            
            fail( "Expected an exception!" );
        }
        catch( NullArgumentException th ) {}
    }
    
    public void testLookup() throws ServiceException {
        final MockControl mockControl = MockControl.createStrictControl(PicoContainer.class);
        final PicoContainer mock = (PicoContainer) mockControl.getMock();
        
        mock.getComponent("blah");
        mockControl.setReturnValue(this);

        mock.getComponent(null);
        mockControl.setReturnValue(null);
        
        mock.getComponent("org.apache.avalon.framework.service.ServiceManager");
        mockControl.setReturnValue(null);
        mock.getComponent(ServiceManager.class);
        final ServiceManager sm = new DefaultServiceManager();
        mockControl.setReturnValue(sm);

        final PicoException problem = new PicoException() {};
        mock.getComponent("test");
        mockControl.setThrowable(problem);

        mock.getComponent("org.apache.avalon.framework.service.NoSuchServiceManager");
        mockControl.setReturnValue(null);

        
        mockControl.replay();

        
        final ServiceManager s = new PicoContainerBasedServiceManager(mock);
        assertEquals( this, s.lookup("blah") );
        assertEquals( null, s.lookup(null) );
        assertEquals( sm, s.lookup("org.apache.avalon.framework.service.ServiceManager") );
        try
        {
            s.lookup("test");
            fail( "Expected an exception!" );
        }
        catch( ServiceException th )
        {
            assertEquals( problem, th.getCause() );
        }
        try
        {
            assertEquals( sm, s.lookup("org.apache.avalon.framework.service.NoSuchServiceManager") );
            fail( "Expected an exception!" );
        }
        catch( ServiceException th )
        {
            assertTrue( th.getCause() instanceof ClassNotFoundException );
        }
        
        
        mockControl.verify();
    }

    public void testHasService() throws ServiceException {
        final MockControl mockControl = MockControl.createStrictControl(PicoContainer.class);
        final PicoContainer mock = (PicoContainer) mockControl.getMock();
        
        final ComponentAdapter a = new ComponentAdapter() {
            public Object getComponentKey() {
                return null;
            }

            public Class getComponentImplementation() {
                return null;
            }

            public Object getComponentInstance(final PicoContainer picoContainer) throws PicoInitializationException, PicoIntrospectionException {
                return null;
            }

            public void verify(final PicoContainer picoContainer) throws UnsatisfiableDependenciesException {
            }

            public void accept(PicoVisitor visitor) {
            }
        };
        
        mock.getComponentAdapter("blah");
        mockControl.setReturnValue(a);

        mock.getComponentAdapter(null);
        mockControl.setReturnValue(null);
        
        final PicoException problem = new PicoException() {};
        
        mock.getComponentAdapter("test");
        mockControl.setThrowable(problem);
        mockControl.replay();
        
        final ServiceManager s = new PicoContainerBasedServiceManager(mock);
        
        assertTrue( s.hasService("blah") );
        assertFalse( s.hasService(null) );
        try
        {
            s.hasService("test");
            fail( "Expected an exception!" );
        }
        catch( PicoException th )
        {
            assertEquals( problem, th );
        }
        
        mockControl.verify();
    }
    
    public void testRelease()
    {
        final ServiceManager s = new PicoContainerBasedServiceManager(new DefaultPicoContainer());
        s.release(this);
        s.release(null);
    }
}
