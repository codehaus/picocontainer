package org.picocontainer.gui;


/**
 * Interface to be implemented by classes that hold arbitrary values.
 *
 * @author <a href="mailto:aslak.hellesoy at bekk.no">Aslak Helles&oslash;y</a>
 * @version $Revision$
 */
public interface ValueHolder {
    Object getValue();

    void setValue(Object o);
}
