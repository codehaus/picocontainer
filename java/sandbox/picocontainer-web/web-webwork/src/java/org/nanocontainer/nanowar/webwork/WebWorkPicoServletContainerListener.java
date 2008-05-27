package org.nanocontainer.nanowar.webwork;

import javax.servlet.ServletContextEvent;

import org.nanocontainer.nanowar.PicoServletContainerListener;

import webwork.action.factory.ActionFactory;

public class WebWorkPicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        ActionFactory.setActionFactory(new WebWorkActionFactory());
    }
}
