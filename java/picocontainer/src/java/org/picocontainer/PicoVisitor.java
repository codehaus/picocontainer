package org.picocontainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface PicoVisitor {
    void visitContainer(PicoContainer pico);
    void visitComponentAdapter(ComponentAdapter componentAdapter);
    void visitComponentInstance(Object o);
}
