/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import de.jtec.jobdemo.search.DocumentFactory;
import de.jtec.jobdemo.search.DocumentIndexer;
import de.jtec.jobdemo.ProjectManager;
/**
 * index specified project.
 *
 * @author           Konstantin Pribluda
 * @created          December 1, 2004
 * @version          $Revision$
 * @webwork.action   name="indexProject" success="redirect.action?url=/projects/index.vm"
 *      error="/failure.vm"
 */
public class IndexProjectAction extends AbstractProjectIndexingAction {

    /**
     * Constructor for the IndexProjectAction object
     *
     * @param factory  Description of Parameter
     * @param indexer  Description of Parameter
     * @param manager  Description of Parameter
     */
    public IndexProjectAction(DocumentFactory factory, DocumentIndexer indexer, ProjectManager manager) {
        super(factory, indexer, manager);
    }



    /**
     * execute action
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            indexEntity(getProject());
            return SUCCESS;
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}
