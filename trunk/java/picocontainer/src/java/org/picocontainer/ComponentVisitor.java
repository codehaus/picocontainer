package org.picocontainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface ComponentVisitor {
    void visitComponentInstance(Object o);
}
