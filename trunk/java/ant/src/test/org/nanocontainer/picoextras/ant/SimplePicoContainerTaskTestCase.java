package org.picoextras.ant;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.extras.InvokingComponentAdapterFactory;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SimplePicoContainerTaskTestCase extends AbstractPicoContainerTaskTestCase {

    public void testPicoContainerTaskWithPropertyButWithoutParameters() throws PicoException {
        Component pongComp = new Component();
        pongComp.setClassname(Pong.class.getName());
        task.addComponent(pongComp);

        Component pingComp = new Component();
        pingComp.setClassname(Ping.class.getName());
        pingComp.setDynamicAttribute("someprop", "HELLO");
        task.addComponent(pingComp);

        task.execute();

        PicoContainer pico = task.getPicoContainer();

        Ping ping = (Ping) pico.getComponentInstance(Ping.class);
        assertNotNull(ping);
        assertTrue(ping.wasExecuted);
    }

    public void testPicoContainerTaskWithParameters() throws PicoException {
        Component pangComp = new Component();
        pangComp.setClassname(Pang.class.getName());
        task.addComponent(pangComp);

        Component pungComp = new Component();
        pungComp.setClassname(Pung.class.getName());

        // one component param
        Component.ComponentParam componentParam = pungComp.createComponent();
        componentParam.setType(Pang.class);

        // and one constant param
        Component.ConstantParam constantParam = pungComp.createConstant();
        constantParam.setValue("bajs");

        task.addComponent(pungComp);
        task.execute();

        // Now see if the task did the job properly
        PicoContainer pico = task.getPicoContainer();

        Pung pung = (Pung) pico.getComponentInstance(Pung.class);
        assertNotNull(pung);
        assertEquals("bajs", pung.text);

        Pang pang = (Pang) pico.getComponentInstance(Pang.class);
        assertNotNull(pang);
        assertSame(pung.pang, pang);
    }

    protected PicoContainerTask createPicoContainerTask() {
        return new PicoContainerTask() {
            protected MutablePicoContainer createPicoContainer(InvokingComponentAdapterFactory invokingFactory) {
                return new DefaultPicoContainer(invokingFactory);
            }
        };
    }
}
