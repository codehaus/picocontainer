package nanocontainer.servlet.lifecycle;


import picocontainer.Container;
import javax.servlet.ServletContext;
import nanocontainer.servlet.holder.ApplicationScopeObjectHolder;
import nanocontainer.servlet.containerfactory.XmlConfiguredNanoFactory;
import nanocontainer.servlet.ContainerFactory;
import nanocontainer.servlet.ObjectHolder;

public class BaseLifecycleListener {

    public static final String CONTAINER_KEY = "nano.container";
    public static final String FACTORY_KEY = "nano.containerfactory";

    protected ContainerFactory getFactory(ServletContext context) {

        ObjectHolder holder = new ApplicationScopeObjectHolder(context, FACTORY_KEY);
        ContainerFactory result = (ContainerFactory) holder.get();
        if (result == null) {
            result = new XmlConfiguredNanoFactory(context);
            holder.put(result);
        }
        return result;
    }

    protected void destroyContainer(ServletContext context, ObjectHolder holder) {
        Container container = (Container) holder.get();
        getFactory(context).destroyContainer(container);
    }

}

