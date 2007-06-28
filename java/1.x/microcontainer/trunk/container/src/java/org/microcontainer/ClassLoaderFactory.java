package org.microcontainer;

/**
 * @author Michael Ward
 * @version $Revision$
 */
public interface ClassLoaderFactory {

	ClassLoader build(String contextName);
}
