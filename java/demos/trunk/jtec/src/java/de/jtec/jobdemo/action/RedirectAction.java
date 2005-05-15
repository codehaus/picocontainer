/*
 * Copyright (c) 2004 J Tec team
 * sample application source
 * any use except demostration is prohibited
 */
package de.jtec.jobdemo.action;


import webwork.action.ActionSupport;
import webwork.action.ServletActionContext;
import webwork.view.velocity.URLBean;
import webwork.action.ServletRequestAware;
import webwork.action.ServletResponseAware;

/**
 * action performing redurect. we do not use standart webwork action, because
 * it cannot provide context relative redirect. 
 * @author Konstantin Pribluda ( kpriblouda[at]yahoo.com )
 * @webwork.action name="redirect"
 */
public class RedirectAction  extends ActionSupport {
  
    String url = null;

    protected String doExecute() throws Exception {
        URLBean bean = new URLBean();
        bean.setRequest(ServletActionContext.getRequest());
        bean.setResponse(ServletActionContext.getResponse());
        bean.setPage(getUrl());
        String target = ServletActionContext.getResponse().encodeRedirectURL(bean.toString());

        ServletActionContext.getResponse().sendRedirect(target);
        return NONE;
    }

    
	public String getUrl() {
		return url;
	}

	/**
	 * @param url The url to set.
	 */
	public void setUrl(String url) {
		this.url = url;
	}

}
