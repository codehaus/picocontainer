package org.picocontainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface PicoVisitor {
    void visitContainer(PicoContainer pico);
    void visitComponentInstance(Object o);
}
