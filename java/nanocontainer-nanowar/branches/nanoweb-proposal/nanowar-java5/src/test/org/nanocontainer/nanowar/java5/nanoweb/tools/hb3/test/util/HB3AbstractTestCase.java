package org.nanocontainer.nanowar.java5.nanoweb.tools.hb3.test.util;

import junit.framework.TestCase;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

public class HB3AbstractTestCase extends TestCase {

	protected Configuration configuration;

	protected SessionFactory sessionFactory;

	protected void setUp() throws Exception {
		super.setUp();

		AnnotationConfiguration cfg = new AnnotationConfiguration();
		cfg.addAnnotatedClass(Dummy.class);
		cfg.configure();

		sessionFactory = cfg.buildSessionFactory();
		configuration = cfg;
	}

}
