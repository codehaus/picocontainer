package nanocontainer.servlet.lifecycle;



import nanocontainer.servlet.holder.SessionScopeObjectHolder;

import nanocontainer.servlet.holder.RequestScopeObjectHolder;

import nanocontainer.servlet.lifecycle.BaseLifecycleListener;

import nanocontainer.servlet.ObjectHolder;

import nanocontainer.servlet.ObjectInstantiater;



import javax.servlet.*;

import javax.servlet.http.HttpServletRequest;

import javax.servlet.http.HttpSession;

import java.io.IOException;



import picocontainer.PicoContainer;



public class RequestLifecycleFilter extends BaseLifecycleListener implements Filter {



    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest)request;

        HttpSession session = httpRequest.getSession(true);

        ServletContext context = session.getServletContext();



        // grab the parent container

        ObjectHolder parentHolder = new SessionScopeObjectHolder(session, CONTAINER_KEY);

        PicoContainer parentContainer = (PicoContainer)parentHolder.get();



        // build a container

        PicoContainer container = getFactory(context).buildContainerWithParent(parentContainer, "request");



        // and a means to instantiate new objects in the container

        ObjectInstantiater instantiater = getFactory(context).buildInstantiater(container);



        // hold on to them

        ObjectHolder containerHolder = new RequestScopeObjectHolder(httpRequest, CONTAINER_KEY);

        containerHolder.put(container);



        ObjectHolder instantiaterHolder = new RequestScopeObjectHolder(httpRequest, INSTANTIATER_KEY);

        instantiaterHolder.put(instantiater);



        try {



            // process the incoming request

            filterChain.doFilter(request, response);



        } finally {



            // shutdown container

            destroyContainer(context, containerHolder);



        }

    }



    public void init(FilterConfig filterConfig) throws ServletException {

    }



    public void destroy() {

    }

}

