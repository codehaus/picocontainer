/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.hibernate;

import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.HibernateException;

import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.TestCase;

/** 
 * test that sesison is reobtained on close  
 * 
 * @author Konstantin Pribluda
 * @version $Revision$ 
 */

public class SessionFailoverComponentAdapterTestCase extends TestCase {
	
	DefaultPicoContainer container;
	
	public void setUp() throws Exception {
		super.setUp();
		container = new DefaultPicoContainer();
        container.registerComponentImplementation(Configuration.class, ConstructableConfiguration.class);
        container.registerComponent(new SessionFactoryComponentAdapter());

        container.registerComponent(new SessionFailoverComponentAdapter(new SessionComponentAdapter()));
	}
	
	public void testThatSessionIsCached() throws Exception {
		
		Session session = (Session)container.getComponentInstanceOfType(Session.class);
		
		assertSame(session,container.getComponentInstanceOfType(Session.class));
		
	}
	
	
	public void testThatSessionIsRecreatedOnClose() throws Exception {
		Session session = (Session)container.getComponentInstanceOfType(Session.class);
		session.close();
		assertNotSame(session,container.getComponentInstanceOfType(Session.class));
	}
	
	
	public void testThatSessionIsInvalidatedOnError() throws Exception {
		Session session = (Session)container.getComponentInstanceOfType(Session.class);
		
		// provike exception
		try {
			session.save(this);
		} catch(Exception ex) {
			// that's ok... 
		}
		assertNotSame(session,container.getComponentInstanceOfType(Session.class));
	}
}

