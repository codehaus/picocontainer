package org.nanocontainer.ant;

import org.picocontainer.PicoContainer;
import org.picocontainer.internals.ConstantParameter;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SimplePicoContainerTaskTestCase extends AbstractPicoContainerTaskTestCase {

    public void testPicoContainerTaskWithoutParameters() {
        Component pingComp = new Component();
        pingComp.setClassname(Ping.class.getName());
        pingComp.setDynamicAttribute("someprop", "HELLO");
        task.addConfiguredComponent(pingComp);

        Component pongComp = new Component();
        pongComp.setClassname(Pong.class.getName());
        task.addConfiguredComponent(pongComp);

        task.execute();

        PicoContainer pico = task.getPicoContainer();

        Ping ping = (Ping) pico.getComponent(Ping.class);
        assertNotNull(ping);
        assertTrue(ping.wasExecuted);
    }

    public void testPicoContainerTaskWithParameters() {
        Component pingComp = new Component();
        pingComp.setClassname(Ping.class.getName());
        task.addConfiguredComponent(pingComp);

        Component pungComp = new Component();
        pungComp.setClassname(Pung.class.getName());
        pungComp.createComponent();
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
