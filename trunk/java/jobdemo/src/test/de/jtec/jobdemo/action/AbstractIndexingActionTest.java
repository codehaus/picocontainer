/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.AbstractTestBase;
import de.jtec.jobdemo.search.DirectoryInitializer;
import de.jtec.jobdemo.search.DocumentFactory;
import de.jtec.jobdemo.search.DocumentIndexer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.velocity.app.VelocityEngine;
import org.picocontainer.gems.util.ConstructableProperties;

/**
 * abatract base class for actions performing search
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public class AbstractIndexingActionTest extends AbstractTestBase {

    DocumentFactory _factory;
    RAMDirectory    _directory;
    DocumentIndexer _indexer;


    /**
     * Constructor for the AbstractSearchActionTest object
     *
     * @exception Exception  Description of Exception
     */
    public AbstractIndexingActionTest() throws Exception {
        super();
    }


    /**
     * Gets the DocumentFactory attribute of the AbstractIndexingActionTest
     * object
     *
     * @return   The DocumentFactory value
     */
    public DocumentFactory getDocumentFactory() {
        return _factory;
    }


    /**
     * Gets the Directory attribute of the AbstractIndexingActionTest object
     *
     * @return   The Directory value
     */
    public Directory getDirectory() {
        return _directory;
    }


    /**
     * Gets the DocumentIndexer attribute of the AbstractIndexingActionTest
     * object
     *
     * @return   The DocumentIndexer value
     */
    public DocumentIndexer getDocumentIndexer() {
        return _indexer;
    }


    /**
     * The JUnit setup method
     *
     * @exception Exception  Description of Exception
     */
    public void setUp() throws Exception {
        super.setUp();
        VelocityEngine engine = new VelocityEngine(new ConstructableProperties("velocity.properties"));
        _factory = new DocumentFactory(engine);
        _directory = new RAMDirectory();
        (new DirectoryInitializer(_directory)).start();

        _indexer = new DocumentIndexer(_directory, new StandardAnalyzer());
    }
}
