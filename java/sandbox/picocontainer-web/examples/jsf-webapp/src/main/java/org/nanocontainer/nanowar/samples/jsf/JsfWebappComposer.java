package org.nanocontainer.nanowar.samples.jsf;

import org.nanocontainer.nanowar.sample.ExampleWebappComposer;
import org.picocontainer.MutablePicoContainer;

public class JsfWebappComposer extends ExampleWebappComposer {

    public void request(MutablePicoContainer requestContainer) {
        requestContainer.addComponent("cheeseBean", org.nanocontainer.nanowar.samples.jsf.ListCheeseController.class);
        requestContainer.addComponent("addCheeseBean", org.nanocontainer.nanowar.samples.jsf.AddCheeseController.class);
    }

}
