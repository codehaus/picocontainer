package nanocontainer.servlet;

import nanocontainer.servlet.holder.RequestScopeObjectHolder;
import nanocontainer.servlet.lifecycle.BaseLifecycleListener;
import javax.servlet.http.HttpServletRequest;
import picocontainer.Container;

public class WebContainer implements Container {

    private Container container;

    public WebContainer(HttpServletRequest request) {
        ObjectHolder holder = new RequestScopeObjectHolder(request, BaseLifecycleListener.CONTAINER_KEY);
        container = (Container)holder.get();
    }

    public boolean hasComponent(Class compType) {
        return container.hasComponent(compType);
    }

    public Object getComponent(Class compType) {
        return container.getComponent(compType);
    }

    public Object[] getComponents() {
        return container.getComponents();
    }

}

