package org.nanocontainer.ant;

import org.picocontainer.PicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SimplePicoContainerTaskTestCase extends AbstractPicoContainerTaskTestCase {

    public static class Ping {}
    public static class Pong {
        private boolean wasExecuted = false;

        public Pong(Ping ping) {}

        public void execute() {
            wasExecuted = true;
        }
    }

    public void testPicoContainerTask() {

        createAntElement(Ping.class);
        createAntElement(Pong.class);

        task.execute();

        PicoContainer pico = task.getPicoContainer();

        Pong pong = (Pong) pico.getComponent(Pong.class);
        assertNotNull(pong);
        assertTrue(pong.wasExecuted);
    }

    protected PicoContainerTask createPicoContainerTask() {
        return new PicoContainerTask();
    }

}
