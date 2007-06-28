/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.hibernate;
import de.jtec.jobdemo.beans.Project;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.beans.Request;
import de.jtec.jobdemo.AbstractTestBase;

/**
 * test capabilities of hibernate request manager
 *
 * @author    kostik
 * @created   November 20, 2004
 * @version   $Revision$
 */
public class HibernateRequestManagerTest extends AbstractTestBase {
    Project         _project;
    Profile         _profile;


    /**
     * Constructor for the HibernateProjectManagerTest object
     *
     * @exception Exception  Description of Exception
     */
    public HibernateRequestManagerTest() throws Exception {
        super();
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();

        _project = getProjectManager().createProject(new Project());
        _profile = new Profile();
        getProfileManager().createProfile(_profile);

    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testRequestCreation() throws Exception {

        Request request = getRequestManager().requestProject(_project, _profile);

        assertEquals(_project, request.getProject());
        assertEquals(_profile, request.getProfile());
        assertNotNull(request.getDate());

        assertEquals(request, _project.getRequests().get(_profile));
        assertEquals(request, _profile.getRequests().get(_project));

        flushSession();
        request = (Request) getSession().get(Request.class, request.getId());
        _project = getProjectManager().getProject(_project.getId());
        _profile = getProfileManager().getProfile(_profile.getId());
        assertSame(request, _project.getRequests().get(_profile));
        assertSame(request, _profile.getRequests().get(_project));

        assertSame(_project, request.getProject());
        assertSame(_profile, request.getProfile());
        assertSame(_project.getRequests().get(_profile), _profile.getRequests().get(_project));

    }


    /**
     * test that _project removal removes requests, but not _profiles
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectRemovalCascading() throws Exception {
        Request request = getRequestManager().requestProject(_project, _profile);
        flushSession();

        _project = getProjectManager().getProject(_project.getId());
        getProjectManager().removeProject(_project);

        flushSession();
        assertNull("did not cascade removed request", getSession().get(Request.class, request.getId()));
        assertNotNull("wrongfully removed _profile", getSession().get(Profile.class, _profile.getId()));
    }


    /**
     * test that _profile removal removes requests, but not _projects
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileRemovalCascading() throws Exception {

        Request request = getRequestManager().requestProject(_project, _profile);
        flushSession();

        _profile = getProfileManager().getProfile(_profile.getId());
        getProfileManager().removeProfile(_profile);

        flushSession();
        assertNull("did not cascade removed request", getSession().get(Request.class, request.getId()));
        assertNotNull("wrongfully removed _profile", getSession().get(Project.class, _project.getId()));
    }


    /**
     * test that request is removed properly and deleted from maps
     *
     * @exception Exception  Description of Exception
     */
    public void testRequestCancelation() throws Exception {

        Request request = getRequestManager().requestProject(_project, _profile);
        flushSession();

        request = (Request) getSession().get(Request.class, request.getId());
        _project = request.getProject();
        _profile = request.getProfile();

        getRequestManager().cancelRequest(request);
        assertNull(_project.getRequests().get(_profile));
        assertNull(_profile.getRequests().get(_project));

        flushSession();

        _project = getProjectManager().getProject(_project.getId());
        _profile = getProfileManager().getProfile(_profile.getId());
        assertNull(_project.getRequests().get(_profile));
        assertNull(_profile.getRequests().get(_project));
        assertNull(getSession().get(Request.class, request.getId()));

    }


    /**
     * test that request is loaded properly
     *
     * @exception Exception  Description of Exception
     */
    public void testRequestLoading() throws Exception {
        Request request = getRequestManager().requestProject(_project, _profile);
        flushSession();

        assertEquals(request, getRequestManager().getRequest(request.getId()));
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testRequestingSecondTime() throws Exception {
        Request request = getRequestManager().requestProject(_project, _profile);

        Request anotherRequest = getRequestManager().requestProject(_project, _profile);
        assertSame(request, anotherRequest);
    }
}
