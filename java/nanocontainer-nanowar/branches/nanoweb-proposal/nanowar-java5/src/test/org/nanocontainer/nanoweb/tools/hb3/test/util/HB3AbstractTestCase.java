package org.nanocontainer.nanoweb.tools.hb3.test.util;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.cfg.Configuration;

import junit.framework.TestCase;

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
