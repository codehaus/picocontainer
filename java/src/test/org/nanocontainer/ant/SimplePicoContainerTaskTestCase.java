package org.nanocontainer.ant;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SimplePicoContainerTaskTestCase extends AbstractPicoContainerTaskTestCase {

    public void testPicoContainerTaskWithAttributeButWithoutParameters() throws PicoInitializationException {
        Component pongComp = new Component();
        pongComp.setClassname(Pong.class.getName());
        task.addConfiguredComponent(pongComp);

        Component pingComp = new Component();
        pingComp.setClassname(Ping.class.getName());
        pingComp.setDynamicAttribute("someprop", "HELLO");
        task.addConfiguredComponent(pingComp);

        task.execute();

        DefaultPicoContainer pico = task.getPicoContainer();

        Ping ping = (Ping) pico.getComponent(Ping.class);
        assertNotNull(ping);
        assertTrue(ping.wasExecuted);
    }

    public void testPicoContainerTaskWithParameters() throws PicoInitializationException {
        Component pingComp = new Component();
        pingComp.setClassname(Ping.class.getName());
        task.addConfiguredComponent(pingComp);

        Component pungComp = new Component();
        pungComp.setClassname(Pung.class.getName());

        // one component param
        Component.ComponentParam componentParam = pungComp.createComponent();
        componentParam.setType(Ping.class);

        // and one constant param
        Component.ConstantParam constantParam = pungComp.createConstant();
        constantParam.setValue("bajs");

        task.addConfiguredComponent(pungComp);
        task.execute();

        PicoContainer pico = task.getPicoContainer();

        Ping ping = (Ping) pico.getComponent(Ping.class);
        assertNotNull(ping);
        assertTrue(ping.wasExecuted);

        Pung pung = (Pung) pico.getComponent(Pung.class);
        assertNotNull(pung);
        assertEquals("bajs", pung.text);
    }

    protected PicoContainerTask createPicoContainerTask() {
        return new PicoContainerTask();
    }
}
