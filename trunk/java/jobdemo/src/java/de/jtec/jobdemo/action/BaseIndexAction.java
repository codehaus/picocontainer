/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.beans.BaseEntity;
import de.jtec.jobdemo.search.DocumentFactory;
import de.jtec.jobdemo.search.DocumentIndexer;
import webwork.action.ActionSupport;

/**
 * base functionality for indexing
 *
 * @author    kostik
 * @created   December 1, 2004
 * @version   $Revision$
 */
public abstract class BaseIndexAction extends ActionSupport {

    private DocumentFactory _factory;

    private DocumentIndexer _indexer;


    /**
     * Constructor for the BaseIndexAction object
     *
     * @param factory  Description of Parameter
     * @param indexer  Description of Parameter
     */

    public BaseIndexAction(DocumentFactory factory, DocumentIndexer indexer) {
        setFactory(factory);
        setIndexer(indexer);
    }


    /**
     * Gets the Factory attribute of the BaseIndexAction object
     *
     * @return   The Factory value
     */
    public DocumentFactory getFactory() {
        return _factory;
    }


    /**
     * Gets the Indexer attribute of the BaseIndexAction object
     *
     * @return   The Indexer value
     */
    public DocumentIndexer getIndexer() {
        return _indexer;
    }


    /**
     * Sets the Factory attribute of the BaseIndexAction object
     *
     * @param factory  The new Factory value
     */
    public void setFactory(DocumentFactory factory) {
        _factory = factory;
    }


    /**
     * Sets the Indexer attribute of the BaseIndexAction object
     *
     * @param indexer  The new Indexer value
     */
    public void setIndexer(DocumentIndexer indexer) {
        _indexer = indexer;
    }


    /**
     * create index for entity. 
     *
     * @param entity         Description of Parameter
     * @exception Exception  Description of Exception
     */
    public void indexEntity(BaseEntity entity) throws Exception {
        getIndexer().indexDocument(getFactory().createDocument(entity));
    }


    /**
     * remove index for given entity
     *
     * @param entity         Description of Parameter
     * @exception Exception  Description of Exception
     */
    public void removeIndex(BaseEntity entity) throws Exception {
        getIndexer().removeIndex(getFactory().createDocument(entity));
    }
}
