package org.nanocontainer.ant;

import org.picocontainer.PicoContainer;
import org.picocontainer.PicoInitializationException;

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

        PicoContainer pico = task.getPicoContainer();

        Ping ping = (Ping) pico.getComponent(Ping.class);
        assertNotNull(ping);
        assertTrue(ping.wasExecuted);
    }

    public void testPicoContainerTaskWithParameters() throws PicoInitializationException {
        Component pangComp = new Component();
        pangComp.setClassname(Pang.class.getName());
        task.addConfiguredComponent(pangComp);

        Component pungComp = new Component();
        pungComp.setClassname(Pung.class.getName());

        // one component param
        Component.ComponentParam componentParam = pungComp.createComponent();
        componentParam.setType(Pang.class);

        // and one constant param
        Component.ConstantParam constantParam = pungComp.createConstant();
        constantParam.setValue("bajs");

        task.addConfiguredComponent(pungComp);
        task.execute();

        // Now see if the task did the job properly
        PicoContainer pico = task.getPicoContainer();

        Pung pung = (Pung) pico.getComponent(Pung.class);
        assertNotNull(pung);
        assertEquals("bajs", pung.text);

        Pang pang = (Pang) pico.getComponent(Pang.class);
        assertNotNull(pang);
        assertSame(pung.pang, pang);
    }

    protected PicoContainerTask createPicoContainerTask() {
        return new PicoContainerTask();
    }
}
