package org.nanocontainer.nanowar.webwork2;

import org.nanocontainer.nanowar.PicoServletContainerListener;

import javax.servlet.ServletContextEvent;

import com.opensymphony.xwork.ObjectFactory;

public class WebWork2PicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ObjectFactory.setObjectFactory(new PicoObjectFactory());
    }
}
