/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.hibernate;
import de.jtec.jobdemo.beans.Project;
import java.util.Collection;
import de.jtec.jobdemo.AbstractTestBase;

/**
 * test capabilities of hibernate project manager
 *
 * @author    $author$
 * @created   November 20, 2004
 * @version   $Revision$
 */
public class HibernateProjectManagerTest extends AbstractTestBase {

    /**
     * Constructor for the HibernateProjectManagerTest object
     *
     * @exception Exception  Description of Exception
     */
    public HibernateProjectManagerTest() throws Exception {
        super();
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * test that new project is created properly. all collections shall be empty
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectCreation() throws Exception {
        Project project = new Project();
        project.setName("foo");
        project.setDescription("bar");
        project.setShortDescription("blurge");

        project = getProjectManager().createProject(project);
        assertNotNull(project.getId());

        flushSession();
        Project loaded = (Project) getSession().get(Project.class, project.getId());

        assertEquals(project, loaded);
        assertEquals(project.getDescription(), loaded.getDescription());
        assertEquals(project.getName(), loaded.getName());
        assertEquals(project.getShortDescription(), loaded.getShortDescription());

    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectUpdate() throws Exception {
        Project project = new Project();
        project = getProjectManager().createProject(project);
        project.setName("foo");
        project.setDescription("bar");
        project.setShortDescription("blurge");
        getProjectManager().updateProject(project);

        flushSession();

        Project loaded = (Project) getSession().get(Project.class, project.getId());

        assertEquals(project, loaded);
        assertEquals(project.getDescription(), loaded.getDescription());
        assertEquals(project.getName(), loaded.getName());
        assertEquals(project.getShortDescription(), loaded.getShortDescription());

    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectLoadingById() throws Exception {
        Project project = new Project();
        getProjectManager().createProject(project);

        flushSession();
        Project loaded = getProjectManager().getProject(project.getId());
        assertEquals(project, loaded);
    }


    /**
     * test project selection ( name sorted )
     *
     * @exception Exception  Description of Exception
     */
    public void testProejctSelection() throws Exception {
        Project project = new Project();
        project.setName("foo");
        getProjectManager().createProject(project);
        project = new Project();
        project.setName("bar");
        getProjectManager().createProject(project);

        flushSession();

        Collection projects = getProjectManager().getProjects();
        assertEquals(2, projects.size());
        assertEquals("bar", ((Project) projects.toArray()[0]).getName());
        assertEquals("foo", ((Project) projects.toArray()[1]).getName());
    }

}
