/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;

import webwork.action.ActionSupport;
import de.jtec.jobdemo.ProjectManager;
import de.jtec.jobdemo.ProfileManager;
import de.jtec.jobdemo.RequestManager;

/**
 * action requesting project for profile
 *
 * @author           kostik
 * @created          November 24, 2004
 * @version          $Revision$
 * @webwork.action   name="requestProject" success="showProfileProfiles.action"
 */
public class RequestProjectAction extends ActionSupport {
    private ProjectManager _projectManager;

    private ProfileManager _profileManager;

    private RequestManager _requestManager;

    private Integer _projectId;

    private Integer _profileId;


    /**
     * Constructor for the RequestProjectAction object
     *
     * @param project  Description of Parameter
     * @param profile  Description of Parameter
     * @param request  Description of Parameter
     */
    public RequestProjectAction(ProjectManager project, ProfileManager profile, RequestManager request) {
        setProjectManager(project);
        setProfileManager(profile);
        setRequestManager(request);
    }


    /**
     * Gets the ProjectManager attribute of the RequestProjectAction object
     *
     * @return   The ProjectManager value
     */
    public ProjectManager getProjectManager() {
        return _projectManager;
    }


    /**
     * Gets the ProfileManager attribute of the RequestProjectAction object
     *
     * @return   The ProfileManager value
     */
    public ProfileManager getProfileManager() {
        return _profileManager;
    }


    /**
     * Gets the RequestManager attribute of the RequestProjectAction object
     *
     * @return   The RequestManager value
     */
    public RequestManager getRequestManager() {
        return _requestManager;
    }


    /**
     * Gets the ProjectId attribute of the RequestProjectAction object
     *
     * @return   The ProjectId value
     */
    public Integer getProjectId() {
        return _projectId;
    }


    /**
     * Gets the ProfileId attribute of the RequestProjectAction object
     *
     * @return   The ProfileId value
     */
    public Integer getProfileId() {
        return _profileId;
    }


    /**
     * Sets the ProjectManager attribute of the RequestProjectAction object
     *
     * @param projectManager  The new ProjectManager value
     */
    public void setProjectManager(ProjectManager projectManager) {
        _projectManager = projectManager;
    }


    /**
     * Sets the ProfileManager attribute of the RequestProjectAction object
     *
     * @param profileManager  The new ProfileManager value
     */
    public void setProfileManager(ProfileManager profileManager) {
        _profileManager = profileManager;
    }


    /**
     * Sets the RequestManager attribute of the RequestProjectAction object
     *
     * @param requestManager  The new RequestManager value
     */
    public void setRequestManager(RequestManager requestManager) {
        _requestManager = requestManager;
    }


    /**
     * Sets the ProjectId attribute of the RequestProjectAction object
     *
     * @param projectId  The new ProjectId value
     */
    public void setProjectId(Integer projectId) {
        _projectId = projectId;
    }


    /**
     * Sets the ProfileId attribute of the RequestProjectAction object
     *
     * @param profileId  The new ProfileId value
     */
    public void setProfileId(Integer profileId) {
        _profileId = profileId;
    }


    /**
     * request given project
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        try {
            getRequestManager().requestProject(getProjectManager().getProject(getProjectId()), getProfileManager().getProfile(getProfileId()));
            return SUCCESS;
        } catch (Exception ex) {
            log.error("exception occured:", ex);
            addErrorMessage(ex.toString());
            return ERROR;
        }
    }
}

