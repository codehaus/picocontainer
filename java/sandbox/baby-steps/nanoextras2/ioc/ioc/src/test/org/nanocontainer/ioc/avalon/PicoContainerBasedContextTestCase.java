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

import org.nanocontainer.ioc.avalon.NullArgumentException;
import org.nanocontainer.ioc.avalon.PicoContainerBasedContext;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.easymock.MockControl;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;

import junit.framework.TestCase;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class PicoContainerBasedContextTestCase extends TestCase {
    public void testConstructor()
    {
        new PicoContainerBasedContext(new DefaultPicoContainer());
        try
        {
            new PicoContainerBasedContext(null);
            
            fail( "Expected an exception!" );
        }
        catch( NullArgumentException th ) {}
    }
    
    public void testGet() throws ContextException {
        final MockControl mockControl = MockControl.createStrictControl(PicoContainer.class);
        final PicoContainer mock = (PicoContainer) mockControl.getMock();
        
        mock.getComponent("blah");
        mockControl.setReturnValue(this);

        mock.getComponent(this);
        mockControl.setReturnValue(mock);

        mock.getComponent(null);
        mockControl.setReturnValue(null);
        
        final PicoException problem = new PicoException() {};
        
        mock.getComponent("test");
        mockControl.setThrowable(problem);
        mockControl.replay();
        
        final Context c = new PicoContainerBasedContext(mock);
        assertEquals( this, c.get("blah") );
        assertEquals( mock, c.get(this) );
        assertEquals( null, c.get(null) );
        try
        {
            c.get("test");
            fail( "Expected an exception!" );
        }
        catch( ContextException th )
        {
            assertEquals( problem, th.getCause() );
        }
        
        mockControl.verify();
    }
}
