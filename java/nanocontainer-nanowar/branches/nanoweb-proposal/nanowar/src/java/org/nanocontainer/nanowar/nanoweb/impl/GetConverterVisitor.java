package org.nanocontainer.nanoweb.impl;

import org.nanocontainer.nanoweb.ConverterComponentAdapter;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AbstractPicoVisitor;

public class GetConverterVisitor extends AbstractPicoVisitor {

    private Class type;
    private ComponentAdapter converterCA;

    public GetConverterVisitor(Class type) {
        this.type = type;
    }

    public void visitContainer(PicoContainer pico) {}

    public void visitComponentAdapter(ComponentAdapter ca) {
        if (ca instanceof ConverterComponentAdapter) {
            if (((ConverterComponentAdapter) ca).getType().isAssignableFrom(type)) {
                converterCA = ca;
            }
        }
    }

    public void visitParameter(Parameter param) {}

    public ComponentAdapter getConverterCA() {
        return this.converterCA;
    }

}
