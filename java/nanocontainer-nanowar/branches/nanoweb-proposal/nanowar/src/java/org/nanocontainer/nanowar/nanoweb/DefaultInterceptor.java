package org.nanocontainer.nanowar.nanoweb;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DefaultInterceptor implements Interceptor {

	public Object before(Object action, HttpServletRequest req, HttpServletResponse res) throws Exception {
		return null;
	}

	public Object after(Object action, Object result, HttpServletRequest req, HttpServletResponse res) throws Exception {
		return result;
	}

}
