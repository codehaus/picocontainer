package org.picocontainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface ContainerVisitor {
    void visit(PicoContainer pico);
}
