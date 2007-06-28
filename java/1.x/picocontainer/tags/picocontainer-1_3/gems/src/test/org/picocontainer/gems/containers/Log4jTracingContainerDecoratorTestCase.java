package org.picocontainer.gems.containers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.jmock.Mock;
import org.jmock.MockObjectTestCase;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoVisitor;
import org.picocontainer.defaults.ConstantParameter;
import org.picocontainer.defaults.ConstructorInjectionComponentAdapter;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;

public class Log4jTracingContainerDecoratorTestCase extends MockObjectTestCase {
	
	private Logger log;
	
	private Mock picoMock= null;
	
	private MutablePicoContainer mockDelegate = null;
	
	private Log4jTracingContainerDecorator tracingDecorator;
	
	private ConsoleAppender consoleAppender;
	
	private StringWriter logOutput= null;

	protected void setUp() throws Exception {
		super.setUp();
		
		
		
		//Setup log4j for this test case.
		//All output will go to a string.
		log = Logger.getLogger(Log4jTracingContainerDecoratorTestCase.class);
		log.removeAllAppenders();
		log.setAdditivity(false);
		log.setLevel(Level.ALL);
		logOutput = new StringWriter();
		consoleAppender = new ConsoleAppender();
		consoleAppender.setName("StringWriter Appender");
		consoleAppender.setLayout(new SimpleLayout());
		consoleAppender.setWriter(logOutput);
		log.addAppender(consoleAppender);
		
		picoMock = mock(MutablePicoContainer.class);
		mockDelegate = (MutablePicoContainer) picoMock.proxy();
		tracingDecorator = new Log4jTracingContainerDecorator(mockDelegate, log);		
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		log = null;
		picoMock = null;
		mockDelegate = null;
		tracingDecorator = null;
	}

	public void testAccept() {
		
		//Dummy test object
		PicoVisitor visitor = new PicoVisitor() {
			public Object traverse(Object node) {
				throw new UnsupportedOperationException();
			}

			public void visitComponentAdapter(ComponentAdapter componentAdapter) {
				throw new UnsupportedOperationException();
			}

			public void visitContainer(PicoContainer pico) {
				throw new UnsupportedOperationException();
			}

			public void visitParameter(Parameter parameter) {
				throw new UnsupportedOperationException();
			}
			
		};
		//setup mock
		picoMock.expects(once()).method("accept").with(same(visitor));
		
		tracingDecorator.accept(visitor);
		String result = logOutput.toString();
		assertNotNull(result);
		assertTrue(result.contains("Visiting Container "));
	}

	public void testAddChildContainer() {
		MutablePicoContainer childPico = new DefaultPicoContainer();
		picoMock.expects(once()).method("addChildContainer")
			.with(same(childPico)).will(returnValue(true));
		
		assertTrue(tracingDecorator.addChildContainer(childPico));
		String result = logOutput.toString();
		assertNotNull(result);
		assertTrue(result.contains("Adding child container: "));
	}

	public void testDispose() {
		picoMock.expects(once()).method("dispose");
		tracingDecorator.dispose();
		String result = logOutput.toString();
		assertNotNull(result);
		assertTrue(result.contains("Disposing container "));
		
	}

	public void testGetComponentAdapter() {
		ConstructorInjectionComponentAdapter testAdapter = new ConstructorInjectionComponentAdapter(String.class, String.class);
		picoMock.expects(once()).method("getComponentAdapter").with(same(String.class)).will(returnValue(testAdapter));
		picoMock.expects(once()).method("getComponentAdapter").with(same(Map.class)).will(this.returnValue(null));
		
		ComponentAdapter ca = tracingDecorator.getComponentAdapter(String.class);
		assertNotNull(ca);

		verifyLog("Locating component adapter with key ");

		ca = tracingDecorator.getComponentAdapter(Map.class);
		assertNull(ca);

		verifyKeyNotFound();
	}
	
	private void verifyKeyNotFound() {
		verifyLog("Could not find component ");		
	}
	
	private void verifyLog(String valueToExpect) {
		String result = logOutput.toString();
		assertNotNull("Log output was null", result);
		assertTrue("Could not find '" + valueToExpect + "' in log output.  Instead got '"
				+ result + "'"
				,result.contains(valueToExpect));
	}

	public void testGetComponentAdapterOfType() {
		ConstructorInjectionComponentAdapter testAdapter = new ConstructorInjectionComponentAdapter(String.class, String.class);
		picoMock.expects(once()).method("getComponentAdapterOfType").with(same(String.class)).will(returnValue(testAdapter));
		picoMock.expects(once()).method("getComponentAdapterOfType").with(same(Map.class)).will(this.returnValue(null));
		
		ComponentAdapter ca = tracingDecorator.getComponentAdapterOfType(String.class);
		assertNotNull(ca);

		verifyLog("Locating component adapter with type ");

		ca = tracingDecorator.getComponentAdapterOfType(Map.class);
		assertNull(ca);

		verifyKeyNotFound();
	}

	public void testGetComponentAdapters() {
		List adapters = Collections.EMPTY_LIST;
		picoMock.expects(once()).method("getComponentAdapters").will(returnValue(adapters));
		
		Collection returnedAdapters = tracingDecorator.getComponentAdapters();
		assertEquals(adapters,returnedAdapters);
		
		verifyLog("Grabbing all component adapters for container: ");
	}

	public void testGetComponentAdaptersOfType() {
		List adapters = Collections.EMPTY_LIST;
		picoMock.expects(once()).method("getComponentAdaptersOfType").with(same(String.class)).will(returnValue(adapters));
		
		List returnedAdapters = tracingDecorator.getComponentAdaptersOfType(String.class);
		assertEquals(adapters,returnedAdapters);
		
		verifyLog("Grabbing all component adapters for container: ");
	}

	public void testGetComponentInstance() {
		String test = "This is a test";
		picoMock.expects(once()).method("getComponentInstance").with(same(String.class)).will(returnValue(test));
		picoMock.expects(once()).method("getComponentInstance").with(same(Map.class)).will(returnValue(null));
		
		Object result = tracingDecorator.getComponentInstance(String.class);
		assertEquals(test, result);
		verifyLog("Attempting to load component instance with key: ");
		
		assertNull(tracingDecorator.getComponentInstance(Map.class));
		this.verifyKeyNotFound();
	}

	public void testGetComponentInstanceOfType() {
		String test = "This is a test";
		picoMock.expects(once()).method("getComponentInstanceOfType").with(same(String.class)).will(returnValue(test));
		picoMock.expects(once()).method("getComponentInstanceOfType").with(same(Map.class)).will(returnValue(null));
		
		Object result = tracingDecorator.getComponentInstanceOfType(String.class);
		assertEquals(test, result);
		verifyLog("Attempting to load component instance with type: ");
		
		assertNull(tracingDecorator.getComponentInstanceOfType(Map.class));
		verifyLog("No component of type " + Map.class.getName());
	}

	public void testGetComponentInstances() {
		List test = Collections.EMPTY_LIST;
		picoMock.expects(once()).method("getComponentInstances").will(returnValue(test));
		
		Object result = tracingDecorator.getComponentInstances();
		assertEquals(test, result);
		verifyLog("Retrieving all component instances for container ");
	}

	public void testGetComponentInstancesOfType() {
		List test = Collections.EMPTY_LIST;
		picoMock.expects(once()).method("getComponentInstancesOfType").with(same(Map.class)).will(returnValue(test));
		List stringTest = new ArrayList();
		stringTest.add("doe");
		stringTest.add("ray");
		stringTest.add("me");
		picoMock.expects(once()).method("getComponentInstancesOfType").with(same(String.class)).will(returnValue(stringTest));		
		
		Object result = tracingDecorator.getComponentInstancesOfType(String.class);
		assertEquals(stringTest, result);
		verifyLog("Loading all component instances of type ");
		
		result = tracingDecorator.getComponentInstancesOfType(Map.class);
		assertEquals(test, result);
		verifyLog("Could not find any components  ");
		
	}

	public void testGetParent() {
		picoMock.expects(once()).method("getParent").will(returnValue(new DefaultPicoContainer()));
		Object result = tracingDecorator.getParent();
		assertNotNull(result);
		
		verifyLog("Retrieving the parent for container");
		
	}

	public void testMakeChildContainer() {
		picoMock.expects(once()).method("makeChildContainer").will(returnValue(new DefaultPicoContainer()));
		MutablePicoContainer result = tracingDecorator.makeChildContainer();
		assertTrue(result instanceof Log4jTracingContainerDecorator);
		verifyLog("Making child container for container ");
	}

	public void testRegisterComponent() {
		ConstructorInjectionComponentAdapter testAdapter = new ConstructorInjectionComponentAdapter(String.class, String.class);
		picoMock.expects(once()).method("registerComponent").with(same(testAdapter)).will(returnValue(testAdapter));
		
		ComponentAdapter result = tracingDecorator.registerComponent(testAdapter);
		assertEquals(testAdapter, result);
		verifyLog("Registering component adapter ");
	}

	public void testRegisterComponentImplementationClass() {
		ConstructorInjectionComponentAdapter testAdapter = new ConstructorInjectionComponentAdapter(String.class, String.class);
		picoMock.expects(once()).method("registerComponentImplementation").with(same(String.class)).will(returnValue(testAdapter));
		
		ComponentAdapter result = tracingDecorator.registerComponentImplementation(String.class);
		assertEquals(testAdapter, result);
		verifyLog("Registering component implementation ");
	}

	public void testRegisterComponentImplementationWithKeyAndClass() {
		ConstructorInjectionComponentAdapter testAdapter = new ConstructorInjectionComponentAdapter(String.class, String.class);
		picoMock.expects(once()).method("registerComponentImplementation").with(same(String.class), same(String.class)).will(returnValue(testAdapter));
		
		ComponentAdapter result = tracingDecorator.registerComponentImplementation(String.class, String.class);
		assertEquals(testAdapter, result);
		verifyLog("Registering component implementation ");
	}

	public void testRegisterComponentInstanceWithKey() {
		String testString = "This is a test.";
		ComponentAdapter testAdapter = new InstanceComponentAdapter(String.class, testString);
		picoMock.expects(once()).method("registerComponentInstance").with(same(String.class), same(testString)).will(returnValue(testAdapter));

		ComponentAdapter result = tracingDecorator.registerComponentInstance(String.class, testString);

		assertTrue(result instanceof InstanceComponentAdapter);
		verifyLog("Registering component instance with key ");
	}


	public void testRegisterComponentImplementationObjectClassParameterArray() {
		Parameter params[] = new Parameter []{new ConstantParameter("test")};
		ConstructorInjectionComponentAdapter testAdapter = new ConstructorInjectionComponentAdapter(String.class, String.class, params);
		picoMock.expects(once()).method("registerComponentImplementation").with(same(String.class), same(String.class), same(params)).will(returnValue(testAdapter));
	
		ComponentAdapter result = tracingDecorator.registerComponentImplementation(String.class, String.class, params);
		assertEquals(testAdapter, result);
		
		verifyLog("Registering component implementation with key ");
		verifyLog(" using parameters ");
		
	}

	public void testRemoveChildContainer() {
		MutablePicoContainer testPico = new DefaultPicoContainer();
		picoMock.expects(once()).method("removeChildContainer").with(same(testPico)).will(returnValue(true));
		
		boolean result = tracingDecorator.removeChildContainer(testPico);

		assertTrue(result);
		verifyLog("Removing child container: ");
	}

	public void testStart() {
		picoMock.expects(once()).method("start");
		tracingDecorator.start();
		String result = logOutput.toString();
		assertNotNull(result);
		assertTrue(result.contains("Starting Container "));
	}

	public void testStop() {
		picoMock.expects(once()).method("stop");
		tracingDecorator.stop();
		String result = logOutput.toString();
		assertNotNull(result);
		assertTrue(result.contains("Stopping Container "));
	}

	public void testUnregisterComponent() {
		ConstructorInjectionComponentAdapter testAdapter = new ConstructorInjectionComponentAdapter(String.class, String.class);
		picoMock.expects(once()).method("unregisterComponent").with(same(String.class)).will(returnValue(testAdapter));
		
		
		ComponentAdapter result = tracingDecorator.unregisterComponent(String.class);
		assertEquals(testAdapter, result);
		verifyLog("Unregistering component ");
	}

	public void testUnregisterComponentByInstance() {
		String testString = "This is a test.";
		ComponentAdapter testAdapter = new InstanceComponentAdapter(String.class, testString);
		picoMock.expects(once()).method("unregisterComponentByInstance").with(same(testString)).will(returnValue(testAdapter));
		
		ComponentAdapter result = tracingDecorator.unregisterComponentByInstance(testString);
		
		assertEquals(testAdapter, result);
		verifyLog("Unregistering component by instance (");
		
	}

	public void testVerify() {
		picoMock.expects(once()).method("verify");
		tracingDecorator.verify();
		verifyLog("Verifying container");
		verifyLog("Using deprecated function PicoContainer.verify().");
	}
	
	public void testDecoratorIsSerializable() throws IOException, ClassNotFoundException {
		String logCategory = "this.is.a.test";
		Log4jTracingContainerDecorator decorator = new Log4jTracingContainerDecorator(new DefaultPicoContainer(), Logger.getLogger(logCategory));
		
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(os);
		oos.writeObject(decorator);
		oos.close();
		
		byte[] savedStream = os.toByteArray();
		
		ByteArrayInputStream bis = new ByteArrayInputStream(savedStream);
		ObjectInputStream ois = new ObjectInputStream(bis);
		
		Log4jTracingContainerDecorator result = (Log4jTracingContainerDecorator) ois.readObject();
		assertNotNull(result);
		assertEquals(logCategory, result.getLoggerUsed().getName());
		
		
	}

}
