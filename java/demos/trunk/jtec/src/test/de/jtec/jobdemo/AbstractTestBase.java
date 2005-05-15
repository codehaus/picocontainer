/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo;

import junit.framework.TestCase;
import de.jtec.jobdemo.hibernate.HibernateProjectManager;
import de.jtec.jobdemo.hibernate.HibernateRequestManager;
import de.jtec.jobdemo.hibernate.HibernateProfileManager;

import net.sf.hibernate.cfg.Configuration;
import net.sf.hibernate.SessionFactory;
import net.sf.hibernate.Session;
import de.jtec.jobdemo.tools.SchemaExport;

/**
 * abstract base class for hibernate dao testing
 *
 * @author    kostik
 * @created   November 23, 2004
 * @version   $Revision$
 */
public abstract class AbstractTestBase extends TestCase {

    Configuration   _configuration;
    SessionFactory  _sessionFactory;
    Session         _session;


    /**
     * Constructor for the AbstractBaseDaoTest object
     *
     * @exception Exception  Description of Exception
     */
    public AbstractTestBase() throws Exception {

        _configuration = new Configuration();
        _configuration.configure();

    }


    /**
     * Gets the Session attribute of the AbstractBaseDaoTest object
     *
     * @return   The Session value
     */
    public Session getSession() {
        return _session;
    }


    /**
     * Gets the ProjectManager attribute of the HibernateProjectManagerTest
     * object
     *
     * @return   The ProjectManager value
     */
    public ProjectManager getProjectManager() {
        return new HibernateProjectManager(getSession());
    }


    /**
     * Gets the Profilemanager attribute of the HibernateProfileManagerTest
     * object
     *
     * @return   The Profilemanager value
     */
    public ProfileManager getProfileManager() {
        return new HibernateProfileManager(getSession());
    }


    /**
     * Gets the RequestManager attribute of the AbstractBaseDaoTest object
     *
     * @return   The RequestManager value
     */
    public RequestManager getRequestManager() {
        return new HibernateRequestManager(getSession());
    }


    /**
     * junit setup method. rig up test environment
     *
     * @throws Exception  DOCUMENT ME!
     */
    public void setUp() throws Exception {
        SchemaExport se = new SchemaExport(_configuration);
        se.start();

        _sessionFactory = _configuration.buildSessionFactory();
        assertNotNull(_sessionFactory);

        _session = _sessionFactory.openSession();
    }


    /**
     * The teardown method for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void tearDown() throws Exception {
        _session.close();
        _sessionFactory.close();
    }


    /**
     * reset session and zap any pending changes
     *
     * @exception Exception  Description of Exception
     */
    public void resetSession() throws Exception {
        _session.clear();
        _session.close();
        _session = _sessionFactory.openSession();
    }


    /**
     * flush hibernate session and restart it.
     *
     * @exception Exception  Description of Exception
     */
    public void flushSession() throws Exception {
        _session.flush();
        _session.close();
        _session = _sessionFactory.openSession();
    }

}
