package org.nanocontainer.nanowar.nanoweb;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nanocontainer.nanowar.ServletContainerFinder;
import org.nanocontainer.nanowar.nanoweb.defaults.OgnlExpressionEvaluator;
import org.nanocontainer.nanowar.nanoweb.impl.ParameterComparator;
import org.nanocontainer.nanowar.nanoweb.result.Redirect;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

public class NanoWebServletComponent {

	private final transient Log log = LogFactory.getLog(NanoWebServletComponent.class);

	private final transient Comparator forOrderSakeParameterComparator = new ParameterComparator();

	private final transient ServletContainerFinder containerFinder = new ServletContainerFinder();

	private final ActionFactory actionFactory;

	private final ExpressionEvaluator expressionEvaluator;

	public NanoWebServletComponent(final ActionFactory actionFactory) {
		this(actionFactory, new OgnlExpressionEvaluator());
	}

	public NanoWebServletComponent(final ActionFactory actionClassLoader, final ExpressionEvaluator expressionEvaluator) {
		this.actionFactory = actionClassLoader;
		this.expressionEvaluator = expressionEvaluator;
	}

	public void service(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws ServletException,
			IOException {

		String servletPath = getServletPath(httpServletRequest);
		int sepIndex = servletPath.indexOf("!");

		String actionPath;
		String actionMethod;
		if (sepIndex < 0) {
			actionPath = servletPath;
			actionMethod = "execute";
		} else {
			actionPath = servletPath.substring(0, sepIndex);
			actionMethod = servletPath.substring(sepIndex + 1);
		}

		MutablePicoContainer container = getContainer(httpServletRequest, httpServletResponse);
		Object action;

		try {
			action = actionFactory.getInstance(container, actionPath);
		} catch (ScriptException e) {
			throw new ServletException(e);
		}

		if (action == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND, "\"" + actionPath + "\" not found.");
			return;
		}

		setProperties(container, httpServletRequest, action);

		Object result = null;
		Interceptor interceptor = (Interceptor) container.getComponentInstanceOfType(Interceptor.class);
		if (interceptor != null) {
			try {
				result = interceptor.before(action, httpServletRequest, httpServletResponse);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		if (result == null) {
			result = execute(action, actionMethod);
		}

		if (interceptor != null) {
			try {
				result = interceptor.after(action, result, httpServletRequest, httpServletResponse);
			} catch (Exception e) {
				throw new ServletException(e);
			}
		}

		if (result == null) {
			httpServletResponse.sendError(HttpServletResponse.SC_NOT_FOUND);
		} else if (result instanceof String) {
			setViewContext(action, httpServletRequest, httpServletResponse);
			httpServletRequest.getRequestDispatcher((String) result).forward(httpServletRequest, httpServletResponse);
		} else if (result instanceof Redirect) {
			httpServletResponse.sendRedirect(((Redirect) result).getUrl());
		} else {
			throw new ServletException("Not a valid result.");
		}
	}

	private void setViewContext(Object action, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse)
			throws ServletException {
		BeanInfo beanInfo;
		try {
			beanInfo = Introspector.getBeanInfo(action.getClass());
		} catch (IntrospectionException e) {
			throw new ServletException(e);
		}

		PropertyDescriptor[] propDescriptors = beanInfo.getPropertyDescriptors();

		for (int i = 0; i < propDescriptors.length; i++) {
			PropertyDescriptor propDescriptor = propDescriptors[i];
			if (!"class".equals(propDescriptor.getName())) {
				Method readMethod = propDescriptor.getReadMethod();
				if (readMethod != null) {
					try {
						httpServletRequest.setAttribute(propDescriptor.getName(), readMethod.invoke(action, null));
					} catch (IllegalArgumentException e) {
						// Do nothing, just an getAnyThink which has parameters. Its just
						// ignored.
					} catch (IllegalAccessException e) {
						// Do nothing, just an getAnyThink which is private. Its just ignored.
					} catch (InvocationTargetException e) {
						throw new ServletException(e.getCause());
					}
				}
			}
		}
	}

	private MutablePicoContainer getContainer(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
		PicoContainer nanowarContainer = containerFinder.findContainer(httpServletRequest);
		MutablePicoContainer thisRequestContainer = new DefaultPicoContainer(nanowarContainer);
		thisRequestContainer.registerComponentInstance(httpServletRequest);
		thisRequestContainer.registerComponentInstance(httpServletResponse);
		thisRequestContainer.registerComponentInstance(httpServletRequest.getSession(true));

		return thisRequestContainer;
	}

	private void setProperties(PicoContainer pico, HttpServletRequest servletRequest, Object action) throws ServletException {
		Map parameterMap = orderParameterMap(servletRequest.getParameterMap());
		Set parameterKeys = parameterMap.keySet();

		for (Iterator iterator = parameterKeys.iterator(); iterator.hasNext();) {
			String parameterKey = (String) iterator.next();
			String[] value = (String[]) parameterMap.get(parameterKey);

			try {
				log.debug("Setting value from parameter '" + parameterKey + "' (value='" + value + "')");
				expressionEvaluator.set(pico, action, parameterKey, value);
			} catch (Exception e) {
				log.trace(e);
				throw new ServletException(e);
			}
		}
	}

	private Map orderParameterMap(Map params) {
		Map newParamMap = new TreeMap(forOrderSakeParameterComparator);
		newParamMap.putAll(params);
		return newParamMap;
	}

	private Object execute(Object action, String actionMethod) throws ServletException {
		try {
			Method execute = action.getClass().getMethod(actionMethod, null);

			if (execute == null) {
				return null;
			}

			return execute.invoke(action, null);
		} catch (Exception e) {
			throw new ServletException(e);
		}
	}

	private static String getServletPath(HttpServletRequest httpServletRequest) {
		String servletPath = (String) httpServletRequest.getAttribute("javax.servlet.include.servlet_path");
		if (servletPath == null) {
			servletPath = httpServletRequest.getServletPath();
		}

		return servletPath.substring(0, servletPath.lastIndexOf("."));
	}

}
