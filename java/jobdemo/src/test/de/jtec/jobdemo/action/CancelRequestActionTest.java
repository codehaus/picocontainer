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
import de.jtec.jobdemo.beans.Request;

/**
 * test capabilities of request cancelation action
 *
 * @author    kostik
 * @created   November 24, 2004
 * @version   $Revision$
 */
public class CancelRequestActionTest extends AbstractTestBase {
    /**
     * Constructor for the CancelRequestActionTest object
     *
     * @exception Exception  Description of Exception
     */
    public CancelRequestActionTest() throws Exception {
        super();
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testThatRequestIsCancelled() throws Exception {
        Project project = new Project();
        project.setName("foo");
        getProjectManager().createProject(project);

        Profile profile = new Profile();
        getProfileManager().createProfile(profile);

        Request request = getRequestManager().requestProject(project, profile);
        flushSession();

        CancelRequestAction action = new CancelRequestAction(getRequestManager());
        action.setRequestId(request.getId());
        assertEquals(Action.SUCCESS, action.execute());
        flushSession();
        assertNull(getSession().get(Request.class, request.getId()));
    }

}

