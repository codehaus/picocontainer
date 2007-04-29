package org.nanocontainer.webcontainer.groovy.adapters;

import org.nanocontainer.webcontainer.PicoContextHandler;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.OldDefaultNanoContainer;
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
        NanoContainer factory = new OldDefaultNanoContainer();
        factory.getPico().registerComponent(PicoContextHandler.class, context);
        factory.getPico().registerComponent(MutablePicoContainer.class, parentContainer);
        factory.registerComponent("wb", new ClassName(className));
        return (NodeBuilder) factory.getPico().getComponent("wb");

    }

}
