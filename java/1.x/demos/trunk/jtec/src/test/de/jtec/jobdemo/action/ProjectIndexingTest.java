/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;
import de.jtec.jobdemo.beans.Project;
import org.apache.lucene.index.IndexReader;

import webwork.action.Action;

/**
 * test actions for project indexing
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public class ProjectIndexingTest extends AbstractIndexingActionTest {
    Project         _project;


    /**
     * Constructor for the ProjectIndexingTest object
     *
     * @exception Exception  Description of Exception
     */
    public ProjectIndexingTest() throws Exception {
        super();
    }


    /**
     * rig up test environment
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();

        _project = new Project();
        _project.setDescription("foo bar baz");
        getProjectManager().createProject(_project);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testIndexingAndUnindeing() throws Exception {
        IndexProjectAction index = new IndexProjectAction(getDocumentFactory(), getDocumentIndexer(), getProjectManager());

        index.setProjectId(_project.getId());
        assertEquals(Action.SUCCESS, index.execute());

        IndexReader reader = IndexReader.open(getDirectory());
        assertEquals(1, IndexReader.open(_directory).numDocs());
        reader.close();

        UnindexProjectAction unindex = new UnindexProjectAction(getDocumentFactory(), getDocumentIndexer(), getProjectManager());
        unindex.setProjectId(_project.getId());
        assertEquals(Action.SUCCESS, unindex.execute());

        reader = IndexReader.open(getDirectory());
        assertEquals(0, IndexReader.open(_directory).numDocs());

    }
}
