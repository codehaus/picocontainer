package nanocontainer.servlet.lifecycle;

import nanocontainer.servlet.holder.ApplicationScopeObjectHolder;
import nanocontainer.servlet.ObjectHolder;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletContext;
import picocontainer.Container;

public class ApplicationLifecycleListener extends BaseLifecycleListener implements ServletContextListener {

    public void contextInitialized(ServletContextEvent event) {

        ServletContext context = event.getServletContext();

        // build a container
        Container container = getFactory(context).buildContainer("application");

        // and hold on to it
        ObjectHolder holder = new ApplicationScopeObjectHolder(context, CONTAINER_KEY);
        holder.put(container);

    }


    public void contextDestroyed(ServletContextEvent event) {
        ServletContext context = event.getServletContext();

        // shutdown container
        destroyContainer(context, new ApplicationScopeObjectHolder(context, CONTAINER_KEY));
    }

}

