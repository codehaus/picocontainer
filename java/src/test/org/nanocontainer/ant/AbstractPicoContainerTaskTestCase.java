package org.nanocontainer.ant;

import junit.framework.TestCase;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractPicoContainerTaskTestCase extends TestCase {
    protected PicoContainerTask task;

    public void setUp() {
        task = createPicoContainerTask();
    }

    protected abstract PicoContainerTask createPicoContainerTask();

    protected AntComponent createAntElement(Class componentClass) {
        AntComponent antComponent = (AntComponent) task.createDynamicElement("component");
        antComponent.setClass(componentClass.getName());
        return antComponent;
    }
}
