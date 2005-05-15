/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;
import de.jtec.jobdemo.beans.Profile;
import org.apache.lucene.index.IndexReader;

import webwork.action.Action;

/**
 * test actions for profile indexing
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public class ProfileIndexingTest extends AbstractIndexingActionTest {
    Profile         _profile;


    /**
     * Constructor for the ProfileIndexingTest object
     *
     * @exception Exception  Description of Exception
     */
    public ProfileIndexingTest() throws Exception {
        super();
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();

        _profile = new Profile();
        _profile.setProfile("foo bar baz");
        getProfileManager().createProfile(_profile);
    }


    /**
     * A unit test for JUnit
     *
     * @exception Exception  Description of Exception
     */
    public void testIndexingAndUnindeing() throws Exception {
        IndexProfileAction index = new IndexProfileAction(getDocumentFactory(), getDocumentIndexer(), getProfileManager());

        index.setProfileId(_profile.getId());
        assertEquals(Action.SUCCESS, index.execute());

        IndexReader reader = IndexReader.open(getDirectory());
        assertEquals(1, IndexReader.open(_directory).numDocs());
        reader.close();

        UnindexProfileAction unindex = new UnindexProfileAction(getDocumentFactory(), getDocumentIndexer(), getProfileManager());
        unindex.setProfileId(_profile.getId());
        assertEquals(Action.SUCCESS, unindex.execute());

        reader = IndexReader.open(getDirectory());
        assertEquals(0, IndexReader.open(_directory).numDocs());

    }
}
