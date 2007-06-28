/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.search.DocumentFactory;
import de.jtec.jobdemo.search.DocumentIndexer;
import de.jtec.jobdemo.ProfileManager;
/**
 * index specified profile.
 *
 * @author           kostik
 * @created          December 1, 2004
 * @version          $Revision$
 * @webwork.action   name="indexProfile" success="redirect.action?url=/profiles/index.vm"
 */
public class IndexProfileAction extends AbstractProfileIndexingAction {

    /**
     * Constructor for the IndexProfileAction object
     *
     * @param factory  Description of Parameter
     * @param indexer  Description of Parameter
     * @param manager  Description of Parameter
     */
    public IndexProfileAction(DocumentFactory factory, DocumentIndexer indexer, ProfileManager manager) {
        super(factory, indexer, manager);
    }


    /**
     * index specified profile
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            indexEntity(getProfile());
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}
