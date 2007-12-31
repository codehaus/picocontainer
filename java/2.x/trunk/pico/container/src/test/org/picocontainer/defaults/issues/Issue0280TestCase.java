/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults.issues;
import junit.framework.TestCase;

import org.junit.Test;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoCompositionException;

/**
 * Test case for issue http://jira.codehaus.org/browse/PICO-280
 */
public class Issue0280TestCase extends TestCase
{
    @Test public void testShouldFailIfInstantiationInChildContainerFails()
    {
        MutablePicoContainer parent = new DefaultPicoContainer();
        MutablePicoContainer child = new DefaultPicoContainer(parent);

        parent.addComponent(CommonInterface.class, ParentImplementation.class);
        child.addComponent(CommonInterface.class, ChildImplementation.class);

        parent.start();
        
        try
        {
            Object result = child.getComponent(CommonInterface.class);
            
            // should never get here
            assertFalse(result.getClass() == ParentImplementation.class);
        }
        catch (Exception e)
        {
            assertTrue(e.getClass() == PicoCompositionException.class);
        }

    }

	public interface CommonInterface
	{
		
	}
	
	public static class ParentImplementation implements CommonInterface
	{
	}

	public static class ChildImplementation implements CommonInterface
	{
		public ChildImplementation()
		{
			throw new PicoCompositionException("Problem during initialization");
		}
	}

}
