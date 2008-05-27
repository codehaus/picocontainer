package org.nanocontainer.nanowar.samples.jsf;

import javax.servlet.ServletContextEvent;

import org.nanocontainer.nanowar.PicoServletContainerListener;
import org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao;
import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService;

public class JsfPicoServletContainerListener extends PicoServletContainerListener {

    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        appContainer.addComponent(CheeseDao.class, MemoryCheeseDao.class);
        sessionContainer.addComponent(DefaultCheeseService.class);
        requestContainer.addComponent("cheeseBean", org.nanocontainer.nanowar.samples.jsf.ListCheeseController.class);
        requestContainer.addComponent("addCheeseBean", org.nanocontainer.nanowar.samples.jsf.AddCheeseController.class);
    }
}
