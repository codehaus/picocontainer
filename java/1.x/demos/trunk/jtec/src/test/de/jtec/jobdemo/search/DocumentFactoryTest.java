/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.search;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.beans.Project;
import org.apache.lucene.document.Document;

/**
 * test capabilities of document factory
 *
 * @author    kostik
 * @created   November 30, 2004
 * @version   $Revision$
 */
public class DocumentFactoryTest extends AbstractSearchTest {

    /**
     * The JUnit setup method
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();
    }


    /**
     * test creation of profile document
     *
     * @exception Exception  Description of Exception
     */
    public void testProfileDocumentCreation() throws Exception {

        Profile profile = new Profile();
        profile.setId(new Integer(239));
        profile.setProfile("foo bar baz blurge");
        Document document = getFactory().createDocument(profile);
        assertNotNull(document);
        assertEquals(profile.getClass().getName(), document.get(DocumentFactory.CLASS));
        assertEquals("239", document.get(DocumentFactory.ID));
        assertEquals(profile.getClass().getName() + "|239", document.get(DocumentFactory.UID));
        assertEquals("foo bar baz blurge", document.get(DocumentFactory.FULL_TEXT));
    }


    /**
     * test generation of project document
     *
     * @exception Exception  Description of Exception
     */
    public void testProjectDocumentCreation() throws Exception {

        Project project = new Project();
        project.setId(new Integer(555));
        project.setDescription("blam glem glee");
        Document document = getFactory().createDocument(project);

        assertNotNull(document);
        assertEquals(project.getClass().getName(), document.get(DocumentFactory.CLASS));
        assertEquals("555", document.get(DocumentFactory.ID));
        assertEquals(project.getClass().getName() + "|555", document.get(DocumentFactory.UID));
        assertEquals("blam glem glee", document.get(DocumentFactory.FULL_TEXT));
    }
}
