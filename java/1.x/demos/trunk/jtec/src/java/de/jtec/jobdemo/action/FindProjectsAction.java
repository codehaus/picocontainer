/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;
import de.jtec.jobdemo.beans.Project;
import de.jtec.jobdemo.beans.Profile;
import de.jtec.jobdemo.ProfileManager;
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
 * @webwork.action   name="findProjects" success="/search/projects.vm"
 *      error="/failure.vm"
 */
public class FindProjectsAction extends AbstractBaseFindAction {

    private ProfileManager _profileManager;

    private Integer _profileId;


    /**
     * Constructor for the FindProjectsAction object
     *
     * @param analyzer  Description of Parameter
     * @param manager   Description of Parameter
     * @param searcher  Description of Parameter
     */
    public FindProjectsAction(Searcher searcher, Analyzer analyzer, ProfileManager manager) {
        super(searcher, analyzer);
        setProfileManager(manager);
    }


    /**
     * Gets the ProfileId attribute of the FindProjectsAction object
     *
     * @return   The ProfileId value
     */
    public Integer getProfileId() {
        return _profileId;
    }


    /**
     * Gets the ProfileManager attribute of the FindProjectsAction object
     *
     * @return   The ProfileManager value
     */
    public ProfileManager getProfileManager() {
        return _profileManager;
    }


    /**
     * Sets the ProfileId attribute of the FindProjectsAction object
     *
     * @param profileId  The new ProfileId value
     */
    public void setProfileId(Integer profileId) {
        _profileId = profileId;
    }


    /**
     * Sets the ProfileManager attribute of the FindProjectsAction object
     *
     * @param profileManager  The new ProfileManager value
     */
    public void setProfileManager(ProfileManager profileManager) {
        _profileManager = profileManager;
    }


    /**
     * perform search of project by profile full text
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            Profile profile = getProfileManager().getProfile(getProfileId());
            BooleanQuery query = new BooleanQuery();
            String escaped = profile.getProfile().replaceAll("[\"'()+-:&,.*?]", "");
            query.add(QueryParser.parse(escaped, DocumentFactory.FULL_TEXT, getAnalyzer()), true, false);
            query.add(new TermQuery(new Term(DocumentFactory.CLASS, Project.class.getName())), true, false);
            find(query);
        } catch (Exception ex) {
            addErrorMessage(ex.toString());
            return ERROR;
        }
        return SUCCESS;
    }
}
