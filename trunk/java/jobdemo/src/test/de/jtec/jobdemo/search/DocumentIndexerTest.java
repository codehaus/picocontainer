/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.search;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.beans.Project;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * test capabilities of docuemtn indexer
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public class DocumentIndexerTest extends AbstractSearchTest {
    RAMDirectory    _directory;
    DocumentIndexer _indexer;


    /**
     * set up everything for reading
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();
        _directory = new RAMDirectory();
        (new DirectoryInitializer(_directory)).start();
        _indexer = new DocumentIndexer(_directory, new StandardAnalyzer());
    }


    /**
     * test that document is indexed properly
     *
     * @exception Exception  Description of Exception
     */
    public void testDocumentIndexing() throws Exception {
        Profile profile = new Profile();
        profile.setId(new Integer(239));
        profile.setProfile("blee blaaa bluu");

        _indexer.indexDocument(getFactory().createDocument(profile));

        assertEquals(1, IndexReader.open(_directory).numDocs());

        Project project = new Project();
        project.setId(new Integer(555));
        _indexer.indexDocument(getFactory().createDocument(project));
        assertEquals(2, IndexReader.open(_directory).numDocs());

        // indexing again does not harm...
        _indexer.indexDocument(getFactory().createDocument(profile));
        assertEquals(2, IndexReader.open(_directory).numDocs());

    }

}

