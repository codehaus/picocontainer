/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.hibernate;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ConstantParameter;

import org.picocontainer.defaults.UnsatisfiableDependenciesException;
import org.picocontainer.ComponentAdapter;

import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import junit.framework.TestCase;

/**
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class SessionFactoryComponentAdapterTestCase extends TestCase {
	
	/**
	* test that defaul adapter key is SessionFactory.class
	*/
	public void testDefaultAdapterConstruction() throws Exception {
		
		SessionFactoryComponentAdapter adapter =  new SessionFactoryComponentAdapter();
		
		assertEquals(SessionFactory.class,adapter.getComponentKey());
		assertEquals(SessionFactory.class,adapter.getComponentImplementation());
		
	}
	
	
	public void testKeyAdapterContruction() throws Exception {
		SessionFactoryComponentAdapter adapter =  new SessionFactoryComponentAdapter("blurge");
		assertEquals("blurge",adapter.getComponentKey());
	}
	
	
	public void testSessionFactoryInstantiation() throws Exception {
		SessionFactoryComponentAdapter adapter =  
			new SessionFactoryComponentAdapter(
				"foo",
				new ConstantParameter(new Configuration())
				);
		
		assertNotNull(adapter.getComponentInstance());
	}
}