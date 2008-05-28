package org.nanocontainer.nanowar.sample;

import org.nanocontainer.nanowar.WebappComposer;
import org.nanocontainer.nanowar.sample.dao.CheeseDao;
import org.nanocontainer.nanowar.sample.dao.simple.MemoryCheeseDao;
import org.nanocontainer.nanowar.sample.service.defaults.DefaultCheeseService;
import org.picocontainer.MutablePicoContainer;

public class ExampleWebappComposer implements WebappComposer {

    public void application(MutablePicoContainer applicationContainer) {
        applicationContainer.addComponent(CheeseDao.class, MemoryCheeseDao.class);
    }

    public void session(MutablePicoContainer sessionContainer) {
        sessionContainer.addComponent(DefaultCheeseService.class);
    }

    public void request(MutablePicoContainer requestContainer) {
    }

}
