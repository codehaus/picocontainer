package org.nanocontainer.nanowar.sample.webwork2;

import org.nanocontainer.nanowar.PicoServletContainerListener;
import org.nanocontainer.nanowar.webwork2.WebWork2PicoServletContainerListener;
import org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao;
import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService;

import javax.servlet.ServletContextEvent;

public class ExampleWebWork2PicoServletContainerListener extends WebWork2PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);

        appContainer.addComponent(CheeseDao.class, MemoryCheeseDao.class);

        sessionContainer.addComponent(DefaultCheeseService.class);

    }
}
