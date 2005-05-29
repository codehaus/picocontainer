/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the license.html file.                                                    *
 *                                                                           *
 * Idea by Rachel Davies, Original code by Aslak Hellesoy and Paul Hammant   *
 *****************************************************************************/
package org.nanocontainer.persistence.hibernate.classic;

import net.sf.hibernate.Session;
import net.sf.hibernate.cfg.Configuration;

import org.nanocontainer.persistence.hibernate.classic.ConstructableConfiguration;
import org.nanocontainer.persistence.hibernate.classic.SessionComponentAdapter;
import org.nanocontainer.persistence.hibernate.classic.SessionFactoryComponentAdapter;
import org.nanocontainer.persistence.hibernate.classic.SessionFailoverComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;

import junit.framework.TestCase;

/** 
 * test capabilities of session failover component adapter
 * 
 * @author Konstantin Pribluda
 * @version $Revision$ 
 * @deprecated together with class to be tested 
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
		assertNotNull(session);
		assertSame(session,container.getComponentInstanceOfType(Session.class));
		
	}
	
	
	public void testThatSessionIsRecreatedOnClose() throws Exception {
		Session session = (Session)container.getComponentInstanceOfType(Session.class);
		session.close();
		assertSame(session,container.getComponentInstanceOfType(Session.class));
	}
	
	
	public void testThatSessionIsInvalidatedOnError() throws Exception {
		Session session = (Session)container.getComponentInstanceOfType(Session.class);
		
		// provike exception
		try {
			session.save(this);
		} catch(Exception ex) {
			// that's ok... 
		}
		assertSame(session,container.getComponentInstanceOfType(Session.class));
	}
}

