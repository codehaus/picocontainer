package org.picoextras.ant;

import junit.framework.TestCase;
import org.apache.tools.ant.Project;

/**
 * 
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
