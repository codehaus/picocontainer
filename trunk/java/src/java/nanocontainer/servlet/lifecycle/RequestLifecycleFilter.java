package nanocontainer.servlet.lifecycle;

import nanocontainer.servlet.holder.SessionScopeObjectHolder;
import nanocontainer.servlet.holder.RequestScopeObjectHolder;
import nanocontainer.servlet.lifecycle.BaseLifecycleListener;
import nanocontainer.servlet.ObjectHolder;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

import picocontainer.Container;

public class RequestLifecycleFilter extends BaseLifecycleListener implements Filter {

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest)request;
        HttpSession session = httpRequest.getSession(true);
        ServletContext context = session.getServletContext();

        // grab the parent container
        ObjectHolder parentHolder = new SessionScopeObjectHolder(session, CONTAINER_KEY);
        Container parentContainer = (Container)parentHolder.get();

        // build a container
        Container container = getFactory(context).buildContainerWithParent(parentContainer, "request");

        // and hold on to it
        ObjectHolder holder = new RequestScopeObjectHolder(httpRequest, CONTAINER_KEY);
        holder.put(container);

        try {

            // process the incoming request
            filterChain.doFilter(request, response);

        } finally {

            // shutdown container
            destroyContainer(context, holder);

        }
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}
