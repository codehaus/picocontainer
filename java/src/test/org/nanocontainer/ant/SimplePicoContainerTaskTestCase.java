package org.nanocontainer.ant;

import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.internals.ConstantParameter;
import org.picocontainer.internals.ComponentAdapter;

import java.util.Collection;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SimplePicoContainerTaskTestCase extends AbstractPicoContainerTaskTestCase {

    public void testPicoContainerTaskWithoutParameters() {
        Component pongComp = new Component();
        pongComp.setClassname(Pong.class.getName());
        task.addConfiguredComponent(pongComp);

        Component pingComp = new Component();
        pingComp.setClassname(Ping.class.getName());
        pingComp.setDynamicAttribute("someprop", "HELLO");
        task.addConfiguredComponent(pingComp);

        task.execute();

        DefaultPicoContainer pico = task.getPicoContainer();

        assertEquals("order of components not same as in build.xml (important for Nanning)",
                Pong.class, componentAdapter(pico, 0).getComponentImplementation());
        assertEquals("order of components not same as in build.xml (important for Nanning)",
                Ping.class, componentAdapter(pico, 1).getComponentImplementation());

        Ping ping = (Ping) pico.getComponent(Ping.class);
        assertNotNull(ping);
        assertTrue(ping.wasExecuted);
    }

    private ComponentAdapter componentAdapter(DefaultPicoContainer pico, int index) {
        return ((ComponentAdapter) pico.getComponentRegistry().getComponentAdapters().get(index));
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
