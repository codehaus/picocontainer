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

import net.sf.hibernate.Session;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.tool.hbm2ddl.SchemaExport;


import junit.framework.TestCase;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ComponentParameter;

/**
 * @author Konstantin Pribluda
 * @version $Revision$
 */
public class SessionComponentAdapterTestCase extends TestCase {
	
	public void testDeafulAdapterConstruction() throws Exception {
		SessionComponentAdapter adapter = new SessionComponentAdapter();
		assertEquals(Session.class,adapter.getComponentKey());
		assertEquals(Session.class,adapter.getComponentImplementation());
	}
	
	
	/**
	* test that component adapter gets proper key. 
	*/ 
	public void testKeyAdapterConstruction() throws Exception {
		SessionComponentAdapter adapter =  new SessionComponentAdapter("blurge");
		assertEquals("blurge",adapter.getComponentKey());
	}

	/**
	 * test that default parameter instantiuation finds dependency
	 */
	public void testInstantiationWithDefaultParams()  throws Exception {
		DefaultPicoContainer container = new DefaultPicoContainer();
		
		container.registerComponentImplementation(Configuration.class,ConstructableConfiguration.class);
		container.registerComponent(new SessionFactoryComponentAdapter());
		
		container.registerComponent(new SessionComponentAdapter());
		
		Session session = (Session)container.getComponentInstanceOfType(Session.class);
		assertNotNull(session);
		
	}
	
	
	/**
	 * test that keyed instantiation works properly
	 */ 
	public void testInstantiationByKey() throws Exception {
		DefaultPicoContainer container = new DefaultPicoContainer();
		
		container.registerComponentImplementation(Configuration.class,ConstructableConfiguration.class);
		container.registerComponent(new SessionFactoryComponentAdapter());
		container.registerComponent(new SessionComponentAdapter("glarch"));
		
		Session session = (Session) container.getComponentInstance("glarch");
		assertNotNull(session);
		
	}
	
	/**
	 * test that we can find our session factory by key
	 */ 
	public void testDependencyByKey() throws Exception {
		DefaultPicoContainer container = new DefaultPicoContainer();
		
		container.registerComponentImplementation(Configuration.class,ConstructableConfiguration.class);
		container.registerComponent(new SessionFactoryComponentAdapter("blurge"));
		
		container.registerComponent(new SessionComponentAdapter("session",new ComponentParameter("blurge")));
		
		Session session = (Session) container.getComponentInstance("session");
		assertNotNull(session);
		
		session = (Session)container.getComponentInstanceOfType(Session.class);
				
	}

}

