package org.nanocontainer.webcontainer.groovy.adapters;

import org.nanocontainer.webcontainer.PicoContextHandler;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.ClassName;
import org.picocontainer.MutablePicoContainer;

import java.util.Map;

import groovy.util.NodeBuilder;

public class WaffleAdapter {
    private final PicoContextHandler context;
    private final MutablePicoContainer parentContainer;

    public WaffleAdapter(PicoContextHandler context, MutablePicoContainer parentContainer, Map attributes) {
        this.context = context;
        this.parentContainer = parentContainer;
    }
    public NodeBuilder getNodeBuilder() {
        String className = "com.thoughtworks.waffle.groovy.WaffleBuilder";
        NanoContainer factory = new DefaultNanoContainer();
        factory.addComponent(PicoContextHandler.class, context);
        factory.addComponent(MutablePicoContainer.class, parentContainer);
        factory.addComponent("wb", new ClassName(className));
        return (NodeBuilder) factory.getComponent("wb");

    }

}
