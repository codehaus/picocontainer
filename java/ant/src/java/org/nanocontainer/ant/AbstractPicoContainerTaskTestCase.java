package org.nanocontainer.ant;

import junit.framework.TestCase;
import org.apache.tools.ant.Project;
import org.picocontainer.defaults.ObjectReference;
import org.picocontainer.defaults.SimpleReference;
import org.picocontainer.PicoContainer;

/**
 * Base class for testing of PicoContainerTask implementations.
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public abstract class AbstractPicoContainerTaskTestCase extends TestCase {
    protected PicoContainerTask task;

    public void setUp() {
        Project project = new Project();

        task = createPicoContainerTask();
        task.setProject(project);
        task.setTaskName(task.getClass().getName());
    }

    protected abstract PicoContainerTask createPicoContainerTask();
}
