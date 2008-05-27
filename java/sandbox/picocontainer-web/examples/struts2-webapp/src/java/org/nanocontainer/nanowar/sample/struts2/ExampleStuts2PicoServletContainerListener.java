package org.nanocontainer.nanowar.sample.struts2;

import org.nanocontainer.nanowar.PicoServletContainerListener;
import org.nanocontainer.nanowar.struts2.Struts2PicoServletContainerListener;
import org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao;
import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService;

import javax.servlet.ServletContextEvent;

public class ExampleStuts2PicoServletContainerListener extends Struts2PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);

        appContainer.addComponent(CheeseDao.class, MemoryCheeseDao.class);

        sessionContainer.addComponent(DefaultCheeseService.class);

    }
}
