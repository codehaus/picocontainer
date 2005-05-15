/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.Action;
import de.jtec.jobdemo.AbstractTestBase;
import de.jtec.jobdemo.beans.Project;

/**
 * test capabilities of project action
 *
 * @author    kostik
 * @created   November 23, 2004
 * @version   $Revision$
 */
public class ProjectActionTest extends AbstractTestBase {

    /**
     * Constructor for the ProjectActionTest object
     *
     * @exception Exception  Description of Exception
     */
    public ProjectActionTest() throws Exception {
        super();
    }


    /**
     * test that action creates project properly
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectCreation() throws Exception {
        CreateProjectAction action = new CreateProjectAction(getProjectManager());
        action.getProject().setName("foo");

        assertEquals(Action.SUCCESS, action.execute());

        flushSession();
        assertEquals(1, getProjectManager().getProjects().size());
        assertEquals("foo", ((Project) getProjectManager().getProjects().iterator().next()).getName());
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectLoading() throws Exception {
        Project project = new Project();
        project.setName("blurge");
        getProjectManager().createProject(project);
        flushSession();

        LoadProjectAction action = new LoadProjectAction(getProjectManager());
        action.setProjectId(project.getId());
        assertEquals(Action.SUCCESS, action.execute());
        assertEquals(project.getId(), action.getProject().getId());
        assertEquals("blurge", action.getProject().getName());
    }


    /**
     * test that action removes proejct properly
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectRemove() throws Exception {
        Project project = new Project();
        project.setName("blurge");
        getProjectManager().createProject(project);
        flushSession();

        RemoveProjectAction action = new RemoveProjectAction(getProjectManager());
        action.setProjectId(project.getId());
        assertEquals(Action.SUCCESS, action.execute());
        flushSession();
        assertNull(getProjectManager().getProject(project.getId()));

    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testProejctUpdate() throws Exception {
        Project project = new Project();
        project.setName("blurge");
        getProjectManager().createProject(project);
        flushSession();

        UpdateProjectAction action = new UpdateProjectAction(getProjectManager());
        action.setProjectId(project.getId());
        action.getProject().setName("blam");
        assertEquals(Action.SUCCESS, action.execute());
        flushSession();
        assertEquals("blam", getProjectManager().getProject(project.getId()).getName());
    }
}
