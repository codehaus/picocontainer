package org.nanocontainer.nanowar.struts2;

import org.nanocontainer.nanowar.PicoServletContainerListener;

import javax.servlet.ServletContextEvent;

import com.opensymphony.xwork2.ObjectFactory;

public class Struts2PicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ObjectFactory.setObjectFactory(new PicoObjectFactory());
    }
}