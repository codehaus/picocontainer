/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;
import webwork.action.ActionSupport;
import de.jtec.jobdemo.RequestManager;

/**
 * cancel specified request
 *
 * @author           kostik
 * @created          November 24, 2004
 * @version          $Revision$
 * @webwork.action   name="cancelRequestProfiles"
 *      success="showProfileProfiles.action" error="/failure.vm"
 * @webwork.action   name="cancelRequestProjects"
 *      success="showProjectProjects.action" error="/failure.vm"
 */
public class CancelRequestAction extends ActionSupport {

    private RequestManager _requestManager;

    private Integer _requestId;


    /**
     * Constructor for the CanceRequestAction object
     *
     * @param manager  Description of Parameter
     */
    public CancelRequestAction(RequestManager manager) {
        setRequestManager(manager);
    }


    /**
     * Gets the RequestManager attribute of the CancelRequestAction object
     *
     * @return   The RequestManager value
     */
    public RequestManager getRequestManager() {
        return _requestManager;
    }


    /**
     * Gets the RequestId attribute of the CancelRequestAction object
     *
     * @return   The RequestId value
     */
    public Integer getRequestId() {
        return _requestId;
    }


    /**
     * Sets the RequestManager attribute of the CancelRequestAction object
     *
     * @param requestManager  The new RequestManager value
     */
    public void setRequestManager(RequestManager requestManager) {
        _requestManager = requestManager;
    }


    /**
     * Sets the RequestId attribute of the CancelRequestAction object
     *
     * @param requestId  The new RequestId value
     */
    public void setRequestId(Integer requestId) {
        _requestId = requestId;
    }


    /**
     * DOCUMENT METHOD
     *
     * @return               Description of the Returned Value
     * @exception Exception  Description of Exception
     */
    public String doExecute() throws Exception {
        getRequestManager().cancelRequest(getRequestManager().getRequest(getRequestId()));
        return SUCCESS;
    }
}

