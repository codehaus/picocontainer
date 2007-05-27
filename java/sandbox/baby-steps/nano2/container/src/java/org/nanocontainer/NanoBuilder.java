package org.nanocontainer;

import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.PicoBuilder;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.monitors.ConsoleComponentMonitor;
import com.thoughtworks.xstream.io.xml.xppdom.Xpp3DomBuilder;


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

    public NanoBuilder withLifecycle() {
        picoBuilder.withLifecycle();
        return this;
    }

    public NanoBuilder withReflectionLifecycle() {
        picoBuilder.withReflectionLifecycle();
        return this;
    }

    public NanoBuilder withMonitor(Class clazz) {
        picoBuilder.withMonitor(clazz);
        return this;
    }
}
