/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;
import webwork.action.Action;
import de.jtec.jobdemo.AbstractTestBase;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.beans.Project;

/**
 * test project request action
 *
 * @author    kostik
 * @created   November 24, 2004
 * @version   $Revision$
 */
public class RequestProjectActionTest extends AbstractTestBase {

    /**
     * Constructor for the RequestProjectActionTest object
     *
     * @exception Exception  Description of Exception
     */
    public RequestProjectActionTest() throws Exception {
        super();
    }


    /**
     * test that action work properly
     *
     * @exception Exception  Description of Exception
     */
    public void testThatRequestingProjectWorks() throws Exception {
        Project project = new Project();
        project.setName("blam");
        Profile profile = new Profile();

        getProjectManager().createProject(project);
        getProfileManager().createProfile(profile);
        flushSession();

        RequestProjectAction action = new RequestProjectAction(getProjectManager(), getProfileManager(), getRequestManager());
        action.setProjectId(project.getId());
        action.setProfileId(profile.getId());
        assertEquals(Action.SUCCESS, action.execute());

        flushSession();

        project = getProjectManager().getProject(project.getId());
        profile = getProfileManager().getProfile(profile.getId());

        assertNotNull(project.getRequests().get(profile));
        assertNotNull(profile.getRequests().get(project));
        assertSame(project.getRequests().get(profile), profile.getRequests().get(project));
    }


    /**
     * test that incorrect project bombs
     *
     * @exception Exception  Description of Exception
     */
    public void testThatIncorrectInputDataProducessError() throws Exception {
        RequestProjectAction action = new RequestProjectAction(getProjectManager(), getProfileManager(), getRequestManager());
        assertEquals(Action.ERROR, action.execute());
    }
}
