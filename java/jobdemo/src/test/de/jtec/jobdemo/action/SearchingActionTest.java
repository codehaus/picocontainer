/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.beans.Project;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.search.IndexSearcher;

import webwork.action.Action;
import de.jtec.jobdemo.search.DocumentFactory;
/**
 * base test class for search system testing
 *
 * @author    kostik
 * @created   December 2, 2004
 * @version   $Revision$
 */
public class SearchingActionTest extends AbstractIndexingActionTest {
    Profile         _foobar;
    Profile         _glem;
    Project         _foo;
    Project         _bar;
    Project         _baz;


    /**
     * Constructor for the AbstractSearchingActionTest object
     *
     * @exception Exception  Description of Exception
     */
    public SearchingActionTest() throws Exception {
        super();
    }


    /**
     * create and index profiles and projects to play around
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();

        _foo = new Project();
        _foo.setName("foo");
        _foo.setDescription("foo bar baz bang fleee");

        getProjectManager().createProject(_foo);
        getDocumentIndexer().indexDocument(getDocumentFactory().createDocument(_foo));

        _bar = new Project();
        _bar.setName("bar");
        _bar.setDescription("foo bang blurge baz blem");
        getProjectManager().createProject(_bar);
        getDocumentIndexer().indexDocument(getDocumentFactory().createDocument(_bar));

        _baz = new Project();
        _baz.setName("glem");
        _baz.setDescription("glem glam glim glum blem");
        getProjectManager().createProject(_baz);
        getDocumentIndexer().indexDocument(getDocumentFactory().createDocument(_baz));

        _foobar = new Profile();
        _foobar.setProfile("foo bar baz bang blurge");
        getProfileManager().createProfile(_foobar);
        getDocumentIndexer().indexDocument(getDocumentFactory().createDocument(_foobar));

        _glem = new Profile();
        _glem.setProfile("glem glam glum");
        getProfileManager().createProfile(_glem);
        getDocumentIndexer().indexDocument(getDocumentFactory().createDocument(_glem));

    }


    /**
     * test that projects are found correctly
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectSearching() throws Exception {
        FindProjectsAction action = new FindProjectsAction(new IndexSearcher(getDirectory()), new StandardAnalyzer(), getProfileManager());

        action.setProfileId(_foobar.getId());
        assertEquals(Action.SUCCESS, action.execute());
        assertEquals(2, action.getHits().length());
        assertEquals("1", action.getHits().doc(0).get(DocumentFactory.ID));
        assertEquals("2", action.getHits().doc(1).get(DocumentFactory.ID));

        action.setProfileId(_glem.getId());
        assertEquals(Action.SUCCESS, action.execute());
        assertEquals(1, action.getHits().length());
        assertEquals("3", action.getHits().doc(0).get(DocumentFactory.ID));
    }


    /**
     * test that profile is found
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileSearching() throws Exception {
        FindProfilesAction action = new FindProfilesAction(new IndexSearcher(getDirectory()), new StandardAnalyzer(), getProjectManager());
        action.setProjectId(_foo.getId());
        assertEquals(Action.SUCCESS, action.execute());
        assertEquals(1, action.getHits().length());
        assertEquals("1", action.getHits().doc(0).get(DocumentFactory.ID));
        assertEquals(_foobar.getClass().getName(), action.getHits().doc(0).get(DocumentFactory.CLASS));
    }
}
