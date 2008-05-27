package org.nanocontainer.nanowar.sample.webwork1;

import org.nanocontainer.nanowar.PicoServletContainerListener;
import org.nanocontainer.nanowar.webwork.WebWorkPicoServletContainerListener;
import org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao;
import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService;

import javax.servlet.ServletContextEvent;

public class ExampleWebWork1PicoServletContainerListener extends WebWorkPicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);

        appContainer.addComponent(CheeseDao.class, MemoryCheeseDao.class);

        sessionContainer.addComponent(DefaultCheeseService.class);

    }
}
