/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;
import de.jtec.jobdemo.beans.Project;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.ProjectManager;
import de.jtec.jobdemo.search.DocumentFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.queryParser.QueryParser;

/**
 * action finding suitable projects for given profile
 *
 * @author           kostik
 * @created          December 2, 2004
 * @version          $Revision$
 * @webwork.action   name="findProfiles" success="/search/profiles.vm"
 *      error="/failure.vm"
 */
public class FindProfilesAction extends AbstractBaseFindAction {

    private ProjectManager _projectManager;

    private Integer _projectId;


    /**
     * Constructor for the FindProjectsAction object
     *
     * @param analyzer  Description of Parameter
     * @param manager   Description of Parameter
     * @param searcher  Description of Parameter
     */
    public FindProfilesAction(Searcher searcher, Analyzer analyzer, ProjectManager manager) {
        super(searcher, analyzer);
        setProjectManager(manager);
    }


    /**
     * Gets the ProfileId attribute of the FindProjectsAction object
     *
     * @return   The ProfileId value
     */
    public Integer getProjectId() {
        return _projectId;
    }


    /**
     * Gets the ProfileManager attribute of the FindProjectsAction object
     *
     * @return   The ProfileManager value
     */
    public ProjectManager getProjectManager() {
        return _projectManager;
    }


    /**
     * Sets the ProfileId attribute of the FindProjectsAction object
     *
     * @param projectId  The new ProfileId value
     */
    public void setProjectId(Integer projectId) {
        _projectId = projectId;
    }


    /**
     * Sets the ProfileManager attribute of the FindProjectsAction object
     *
     * @param projectManager  The new ProfileManager value
     */
    public void setProjectManager(ProjectManager projectManager) {
        _projectManager = projectManager;
    }


    /**
     * perform search of project by profile full text
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            System.err.println("loading project... " + getProjectId());
            Project project = getProjectManager().getProject(getProjectId());
            System.err.println("loaded");
            BooleanQuery query = new BooleanQuery();
            String escaped = project.getDescription().replaceAll("[\"'()+-:&,.*?]", "");
            query.add(QueryParser.parse(escaped, DocumentFactory.FULL_TEXT, getAnalyzer()), true, false);
            query.add(new TermQuery(new Term(DocumentFactory.CLASS, Profile.class.getName())), true, false);
            find(query);
        } catch (Exception ex) {
            ex.printStackTrace(System.err);
            addErrorMessage(ex.toString());
            return ERROR;
        }
        return SUCCESS;
    }
}
