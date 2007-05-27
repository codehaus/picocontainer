package org.nanocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.MutablePicoContainer;


public class NanoBuilder {

    private Class ncClass = DefaultNanoContainer.class;
    private PicoBuilder picoBuilder = new PicoBuilder();
    private ClassLoader classLoader = DefaultNanoContainer.class.getClassLoader();

    public NanoContainer build() {
        DefaultPicoContainer temp = new DefaultPicoContainer();
        temp.addComponent(ClassLoader.class, classLoader);
        temp.addComponent("nc", ncClass);
        temp.addComponent(MutablePicoContainer.class, picoBuilder.build());
        return (NanoContainer) temp.getComponent("nc");
    }

    public NanoBuilder withConsoleMonitor() {
        picoBuilder.withConsoleMonitor();
        return this;
    }
}
