package nanocontainer.servlet.lifecycle;

import nanocontainer.servlet.holder.ApplicationScopeObjectHolder;
import nanocontainer.servlet.holder.SessionScopeObjectHolder;
import nanocontainer.servlet.lifecycle.BaseLifecycleListener;
import nanocontainer.servlet.ObjectHolder;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletContext;

import picocontainer.Container;

public class SessionLifecycleListener extends BaseLifecycleListener implements HttpSessionListener {

    public void sessionCreated(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        // grab the parent container
        ObjectHolder parentHolder = new ApplicationScopeObjectHolder(context, CONTAINER_KEY);
        Container parentContainer = (Container)parentHolder.get();

        // build a container
        Container container = getFactory(context).buildContainerWithParent(parentContainer, "session");

        // and hold on to it
        ObjectHolder holder = new SessionScopeObjectHolder(session, CONTAINER_KEY);
        holder.put(container);
    }

    public void sessionDestroyed(HttpSessionEvent event) {
        HttpSession session = event.getSession();
        ServletContext context = session.getServletContext();

        // shutdown container
        destroyContainer(context, new SessionScopeObjectHolder(session, CONTAINER_KEY));
    }

}
