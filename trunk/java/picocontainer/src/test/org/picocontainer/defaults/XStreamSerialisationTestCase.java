package org.picocontainer.defaults;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.DependsOnTouchable;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import junit.framework.TestCase;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class XStreamSerialisationTestCase extends TestCase {
    private XStream xStream = new XStream(new DomDriver());

    public void testShouldBeAbleToSerialiseEmptyPico() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        String picoXml = xStream.toXML(pico);
        PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

        assertEquals(0, serializedPico.getComponentInstances().size());
    }

    public void testShouldBeAbleToSerialisePicoWithUninstantiatedComponents() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(SimpleTouchable.class);
        pico.registerComponentImplementation(DependsOnTouchable.class);
        String picoXml = xStream.toXML(pico);
        PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

        assertEquals(2, serializedPico.getComponentInstances().size());
    }

    public void testShouldBeAbleToSerialisePicoWithInstantiatedComponents() {
        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(SimpleTouchable.class);
        pico.registerComponentImplementation(DependsOnTouchable.class);
        pico.getComponentInstances();
        String picoXml = xStream.toXML(pico);
        PicoContainer serializedPico = (PicoContainer) xStream.fromXML(picoXml);

        assertEquals(2, serializedPico.getComponentInstances().size());
    }
}