package org.picoextras.typ3msg;

import junit.framework.TestCase;
import org.picoextras.type3msg.MulticastingPicoException;
import org.picoextras.type3msg.MulticastingProxy;
import org.picoextras.type3msg.RoundRobinMulticastingProxy;
import org.picoextras.type3msg.Type3MsgPicoContainer;
import org.picoextras.type3msg.sample.Controller;
import org.picoextras.type3msg.sample.Database;
import org.picoextras.type3msg.sample.PretendDatabase;
import org.picoextras.type3msg.sample.Worker;
import org.picoextras.type3msg.sample.WorkerDoer;
import org.picoextras.type3msg.sample.WorkerLogger;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Chris Stevenson, Stacy Curl
 */
public class Type3MsgPicoContainerTest extends TestCase {
    private TestGirl testGirl1;
    private TestGirl testGirl2;
    private TestGirl testGirl3;

    protected void setUp() throws Exception {
        testGirl1 = new TestGirl();
        testGirl2 = new TestGirl();
        testGirl3 = new TestGirl();
    }

    public void testCannotAddAComponentWithWrongInterface() {
        MulticastingProxy multicastingProxy = new MulticastingProxy(Kissable.class);
        try {
            multicastingProxy.add("foo");
            fail("Should throw an Exception");
        } catch (MulticastingPicoException e) {
            //expected
        }
    }

    public void testMulticastingProxySendsKissToAllComponents() {
        MulticastingProxy multicastingProxy = new MulticastingProxy(Kissable.class);

        multicastingProxy.add(testGirl1);
        multicastingProxy.add(testGirl2);
        multicastingProxy.add(testGirl3);

        Kissable kissable = (Kissable) multicastingProxy.getProxy();

        kissable.kiss();

        assertTrue(testGirl1.wasKissed());
        assertTrue(testGirl2.wasKissed());
        assertTrue(testGirl3.wasKissed());

    }

    public void testRoundRobinProxySendsKissToOneOfTheComponents() {
        RoundRobinMulticastingProxy multicastingProxy = new RoundRobinMulticastingProxy(Kissable.class);

        multicastingProxy.add(testGirl1);
        multicastingProxy.add(testGirl2);

        Kissable kissable = (Kissable) multicastingProxy.getProxy();

        kissable.kiss();
        assertTrue(testGirl1.wasKissed());
        assertFalse(testGirl2.wasKissed());

        testGirl1.wipeFace();
        testGirl2.wipeFace();

        kissable.kiss();
        assertFalse(testGirl1.wasKissed());
        assertTrue(testGirl2.wasKissed());
    }

    public void testPicoContainerWithKnowledgeOfCasting() {
        Type3MsgPicoContainer c = new Type3MsgPicoContainer();

        c.registerComponentImplementation(TestBoy.class);

        c.registerMulticasted(Kissable.class, testGirl1);
        c.registerMulticasted(Kissable.class, testGirl2);

        TestBoy testBoy = (TestBoy) c.getComponentInstance(TestBoy.class);
        testBoy.doKiss();

        assertTrue(testGirl1.wasKissed());
        assertTrue(testGirl2.wasKissed());

    }

    public void testWhyWouldYouUseMulticasting() {
        Type3MsgPicoContainer c = new Type3MsgPicoContainer();

        c.registerComponentImplementation(Controller.class);
//        c.registerRoundRobinMulticaster(Database.class, db2);

        //smelly - how do we do this
        //c.registerMulticasted(Worker.class, new RoundRobin(UnitOfWorkDoer.class, UnitOfWorkDoer.class));
        WorkerLogger workerLogger = new WorkerLogger();
        WorkerDoer workerDoer = new WorkerDoer();

        c.registerMulticasted(Worker.class, workerLogger);
        c.registerMulticasted(Worker.class, workerDoer);

        List objects = new ArrayList();

        c.registerRoundRobin(Database.class, new PretendDatabase(objects));
        c.registerRoundRobin(Database.class, new PretendDatabase(objects));

        Controller controller = (Controller) c.getComponentInstance(Controller.class);

        controller.fireOffAUnitOfWork("foo");

        assertEquals("foo", workerLogger.getLogged());
        assertTrue(workerDoer.getDoneTasks().contains("foo"));

        controller.storeObject("bar");
        assertTrue(objects.contains("bar"));
    }

}
