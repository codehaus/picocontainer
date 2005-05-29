/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Idea by Rachel Davies, Original code by various                           *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import junit.framework.TestCase;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.cfg.Configuration;

import org.nanocontainer.persistence.hibernate.classic.ConstructableConfiguration;
import org.nanocontainer.persistence.hibernate.classic.SessionFactoryComponentAdapter;
import org.picocontainer.defaults.CachingComponentAdapter;
import org.picocontainer.defaults.ComponentParameter;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * test case for session factory component adapter
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
	
	/**
	* test that component adapter gets proper key. 
	*/ 
	public void testKeyAdapterContruction() throws Exception {
		SessionFactoryComponentAdapter adapter =  new SessionFactoryComponentAdapter("blurge");
		assertEquals("blurge",adapter.getComponentKey());
	}
	
	/**
	 * test that cinstantiation with constant parameter
	 */
	public void testConstantParameterInstantiation() throws Exception {
		SessionFactoryComponentAdapter adapter =  
			new SessionFactoryComponentAdapter(
				"foo",
				new ConstantParameter(new Configuration())
				);

		assertNotNull(adapter.getComponentInstance(null));
	}
	
	/**
	 * test instantiation with default pramaters
	 */
	public void testInstantiationWithDefaultParams() throws Exception {
		DefaultPicoContainer container = new DefaultPicoContainer();
		
		container.registerComponentImplementation(Configuration.class,ConstructableConfiguration.class);
		container.registerComponent(new SessionFactoryComponentAdapter());
		
		SessionFactory factory = (SessionFactory)container.getComponentInstanceOfType(SessionFactory.class);
		assertNotNull(factory);
	}
	
	
	/**
	 *  test instantiation by key & by class
	 */
	public void testInstantiationWithKey() throws Exception {
		
		DefaultPicoContainer container = new DefaultPicoContainer();
		container.registerComponentImplementation("blurge",ConstructableConfiguration.class);
		container.registerComponent(new CachingComponentAdapter(new SessionFactoryComponentAdapter("glarch")));
		
		
		SessionFactory factory = (SessionFactory)container.getComponentInstance("glarch");
		assertNotNull(factory);
		
		SessionFactory anotherInstanceOfFactory = (SessionFactory)container.getComponentInstanceOfType(SessionFactory.class);
		assertSame(factory,anotherInstanceOfFactory);
	}
	
	
	/**
	 * test keyed dependency instantiation
	 */
	public void testKeyedDependency() throws Exception {
		DefaultPicoContainer container = new DefaultPicoContainer();
		container.registerComponentImplementation("blurge",ConstructableConfiguration.class);
		container.registerComponent(new CachingComponentAdapter(new SessionFactoryComponentAdapter("glarch", new ComponentParameter("blurge"))));
		
		
		SessionFactory factory = (SessionFactory)container.getComponentInstance("glarch");
		assertNotNull(factory);
		
	}
 
}