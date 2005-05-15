/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.search;
import org.picocontainer.Startable;
import org.apache.lucene.store.Directory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

/**
 * DOCUMENT CLASS - {0}
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public class DirectoryInitializer implements Startable {
    Directory       _directory;


    /**
     * Constructor for the DirectoryInitializer object
     *
     * @param directory  Description of Parameter
     */
    public DirectoryInitializer(Directory directory) {
        _directory = directory;
    }


    /**
     * DOCUMENT METHOD
     */
    public void start() {
        try {
            (new IndexWriter(_directory, new StandardAnalyzer(), true)).close();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }


    /**
     * DOCUMENT METHOD
     */
    public void stop() {
    }
}
