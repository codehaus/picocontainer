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
 * deindex specified profile.
 *
 * @author           kostik
 * @created          December 1, 2004
 * @version          $Revision$
 * @webwork.action   name="unindexProfile" success="removeProfile.action"
 *      error="/failure.vm"
 */
public class UnindexProfileAction extends AbstractProfileIndexingAction {

    /**
     * Constructor for the IndexProfileAction object
     *
     * @param factory  Description of Parameter
     * @param indexer  Description of Parameter
     * @param manager  Description of Parameter
     */
    public UnindexProfileAction(DocumentFactory factory, DocumentIndexer indexer, ProfileManager manager) {
        super(factory, indexer, manager);
    }


    /**
     * remove profile from index directory
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            removeIndex(getProfile());
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}
