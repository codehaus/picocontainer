package org.nanocontainer.nanowar.sample.struts;

import javax.servlet.ServletContextEvent;

import org.nanocontainer.nanowar.PicoServletContainerListener;
import org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao;
import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService;

public class StrutsPicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        appContainer.addComponent(CheeseDao.class, MemoryCheeseDao.class);

        sessionContainer.addComponent(DefaultCheeseService.class);
    }
}
