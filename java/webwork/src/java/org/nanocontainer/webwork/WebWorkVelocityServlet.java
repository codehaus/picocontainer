/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.webwork;


import org.apache.velocity.tools.view.servlet.VelocityViewServlet;
import org.apache.velocity.context.Context;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.Template;

import org.apache.commons.collections.ExtendedProperties;

import webwork.config.Configuration;
import webwork.action.ServletActionContext;
import webwork.util.ServletValueStack;

import javax.servlet.ServletConfig;

import org.picocontainer.PicoContainer;

import java.io.IOException;
import java.io.FileNotFoundException;

import org.nanocontainer.servlet.KeyConstants;
import org.nanocontainer.servlet.RequestScopeObjectReference;

import webwork.view.velocity.WebWorkUtil;

/**
 * velocity integration servlet. integrates container hieararchy into velocity context 
 * as well webwork specific objects. This servlet is not derived from standart webwork 
 * velocity servlet because it inherits from obsolete velocity servlet ( which does not 
 * allow resource loading from webapp ). acc configuration is done  like original velocity 
 * servlet does 
 * @author Konstantin Pribluda
 */ 
public class WebWorkVelocityServlet extends VelocityViewServlet implements KeyConstants {
	
    static final String WEBWORK_UTIL = "webwork";
	
	
    protected Context createContext(javax.servlet.http.HttpServletRequest request,
                                   javax.servlet.http.HttpServletResponse response)
    {
		Context ctx = new NanocontainerVelocityContext(
	   		(PicoContainer)(new RequestScopeObjectReference(request, REQUEST_CONTAINER)).get(),
			ServletValueStack.getStack(request)
			);
		ctx.put(REQUEST, request);
		ctx.put(RESPONSE, response);
		return ctx;		
	}

   
   /**
    * Get the template to show.
    */
   protected Template handleRequest(javax.servlet.http.HttpServletRequest aRequest,
                                    javax.servlet.http.HttpServletResponse aResponse,
                                    Context ctx)
      throws java.lang.Exception
   {
      // Bind standard WebWork utility into context

      ServletActionContext.setContext(aRequest, aResponse, getServletContext(), null);
      ctx.put(WEBWORK_UTIL, new WebWorkUtil(ctx));

      String servletPath = (String)aRequest.getAttribute("javax.servlet.include.servlet_path");
      if (servletPath == null)
         servletPath = aRequest.getServletPath();
      return getTemplate(servletPath);
   }
   
   static class NanocontainerVelocityContext extends VelocityContext {
	   	PicoContainer container;
	    ServletValueStack stack;
	   	NanocontainerVelocityContext(PicoContainer container, ServletValueStack stack) {
			this.container = container;
			this.stack = stack;
		}
	   
	   	public boolean internalContainsKey(java.lang.Object key)
		{
			boolean contains = super.internalContainsKey(key);
			if(contains) 
				return contains;
			
			contains = stack.test(key.toString());
			if(contains)
				return contains;
					
			return  container.getComponentAdapter(key) != null;
		}

		public Object internalGet(String key)
		{
			if(super.internalContainsKey(key))
				return 	super.internalGet(key);
					
			if(stack.test(key.toString())) 
				return stack.findValue(key);
				
			return container.getComponentInstance(key);
		}
   	}
}

