package org.nanocontainer.reflection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertNotSame;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;

import org.junit.Test;
import org.nanocontainer.DefaultNanoContainer;
import org.nanocontainer.integrationkit.ContainerPopulator;
import org.nanocontainer.integrationkit.ContainerRecorder;
import org.nanocontainer.script.xml.XMLContainerBuilder;
import org.nanocontainer.testmodel.FredImpl;
import org.nanocontainer.testmodel.ThingThatTakesParamsInConstructor;
import org.nanocontainer.testmodel.Wilma;
import org.nanocontainer.testmodel.WilmaImpl;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.behaviors.Caching;
import org.picocontainer.parameters.ComponentParameter;

/**
 * @author Konstantin Pribluda ( konstantin.pribluda(at)infodesire.com )
 * @author Aslak Helles&oslash;y
 */
public class DefaultContainerRecorderTestCase {
    @Test public void testInvocationsCanBeRecordedAndReplayedOnADifferentContainerInstance() throws Exception {
        ContainerRecorder recorder = new DefaultContainerRecorder(new DefaultNanoContainer());
        MutablePicoContainer recorded = recorder.getContainerProxy();

        recorded.addComponent("fruit", "apple");
        recorded.addComponent("int", 239);
        recorded.addComponent("thing",
                ThingThatTakesParamsInConstructor.class,
                ComponentParameter.DEFAULT,
                ComponentParameter.DEFAULT);

        MutablePicoContainer slave = new DefaultPicoContainer();
        recorder.replay(slave);
        assertEquals("apple", slave.getComponent("fruit"));
        assertEquals("apple239", ((ThingThatTakesParamsInConstructor) slave.getComponent("thing")).getValue());

        // test that we can replay once more
        MutablePicoContainer anotherSlave = new DefaultPicoContainer();
        recorder.replay(anotherSlave);
        assertEquals("apple", anotherSlave.getComponent("fruit"));
        assertEquals("apple239", ((ThingThatTakesParamsInConstructor) anotherSlave.getComponent("thing")).getValue());
    }

    @Test public void testRecorderWorksAfterSerialization() throws IOException, ClassNotFoundException, IllegalAccessException, InvocationTargetException {
        ContainerRecorder recorder = new DefaultContainerRecorder(new DefaultPicoContainer());
        MutablePicoContainer recorded = recorder.getContainerProxy();
        recorded.addComponent("fruit", "apple");

        ContainerRecorder serializedRecorder = (ContainerRecorder) serializeAndDeserialize(recorder);
        MutablePicoContainer slave = new DefaultPicoContainer();
        serializedRecorder.replay(slave);
        assertEquals("apple", slave.getComponent("fruit"));
    }

    private Object serializeAndDeserialize(Object o) throws IOException, ClassNotFoundException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(baos);

        oos.writeObject(o);
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));

        return ois.readObject();
    }


    @Test public void testXMLRecorderHierarchy() {
        
        MutablePicoContainer parent = new DefaultPicoContainer(new Caching());

        DefaultContainerRecorder parentRecorder = new DefaultContainerRecorder(parent);

        new XMLContainerBuilder(new StringReader(""
                + "<container>"
                + "  <component-implementation key='wilma' class='"+WilmaImpl.class.getName()+"'/>"
                + "</container>"
                ), Thread.currentThread().getContextClassLoader()).populateContainer(parentRecorder.getContainerProxy());

        MutablePicoContainer recordingParent = parentRecorder.getContainerProxy();

        assertNotSame("one should be a proxy of the other", recordingParent, parent);
        assertEquals("as one is a proxy of the other, equals() should be true", recordingParent, parent);

        assertNull(recordingParent.getComponent("fred"));
        assertNotNull(recordingParent.getComponent("wilma"));

        assertNull(parent.getComponent("fred"));
        assertNotNull(parent.getComponent("wilma"));

        MutablePicoContainer child = new DefaultPicoContainer(parent);
        DefaultContainerRecorder grandchild = new DefaultContainerRecorder(child);

        new XMLContainerBuilder(new StringReader(
                  "<container>"
                + "  <component-implementation key='fred' class='"+FredImpl.class.getName()+"'>"
                + "     <parameter key='wilma'/>"
               + "  </component-implementation>"
                + "</container>"
                ), Thread.currentThread().getContextClassLoader()).populateContainer(grandchild.getContainerProxy());

        MutablePicoContainer recordingChild = grandchild.getContainerProxy();

        assertNotNull(recordingChild.getComponent("fred"));
        assertNotNull(recordingChild.getComponent("wilma"));
        
        FredImpl fred = (FredImpl)recordingChild.getComponent("fred");
        Wilma wilma = (Wilma)recordingChild.getComponent("wilma");
        assertSame(wilma, fred.wilma());
    }

}
