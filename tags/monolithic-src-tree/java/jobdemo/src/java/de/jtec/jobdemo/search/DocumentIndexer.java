/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.store.Directory;
import java.io.IOException;
/**
 * performs indexing of supplied documents
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public class DocumentIndexer {
    private Directory _directory;

    private Analyzer _analyzer;


    /**
     * create document indexer with specified reader and writer
     *
     * @param directory  Description of Parameter
     * @param analyzer   Description of Parameter
     */
    public DocumentIndexer(Directory directory, Analyzer analyzer) {
        setDirectory(directory);
        setAnalyzer(analyzer);
    }


    /**
     * Gets the Analyzer attribute of the DocumentIndexer object
     *
     * @return   The Analyzer value
     */
    public Analyzer getAnalyzer() {
        return _analyzer;
    }


    /**
     * Gets the Directory attribute of the DocumentIndexer object
     *
     * @return   The Directory value
     */
    public Directory getDirectory() {
        return _directory;
    }


    /**
     * Sets the Analyzer attribute of the DocumentIndexer object
     *
     * @param analyzer  The new Analyzer value
     */
    public void setAnalyzer(Analyzer analyzer) {
        _analyzer = analyzer;
    }


    /**
     * Sets the Directory attribute of the DocumentIndexer object
     *
     * @param directory  The new Directory value
     */
    public void setDirectory(Directory directory) {
        _directory = directory;
    }



    /**
     * index supplied document removing old version if necessary
     *
     * @param document         Description of Parameter
     * @exception IOException  Description of Exception
     */
    public void indexDocument(Document document) throws IOException {

        removeIndex(document);
        IndexWriter writer = new IndexWriter(getDirectory(), getAnalyzer(), false);
        try {
            writer.addDocument(document);
            writer.optimize();
        } finally {
            writer.close();
        }

    }


    /**
     * remove document from index by UID
     *
     * @param document         Description of Parameter
     * @exception IOException  Description of Exception
     */
    public void removeIndex(Document document) throws IOException {
        IndexReader reader = IndexReader.open(getDirectory());
        Term uid = new Term(DocumentFactory.UID, document.get(DocumentFactory.UID));
        reader.delete(uid);
        reader.close();
    }
}

