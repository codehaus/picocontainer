package org.microcontainer.impl;

import junit.framework.TestCase;

/**
 * @author Michael Ward
 */
public class ClassLoaderDelegateTestCase extends TestCase {

	private DelegatingClassLoader delegating;
	private MockClassLoaderOne mockClassLoaderOne;
	private MockClassLoaderTwo mockClassLoaderTwo;

	protected void setUp() throws Exception {
		delegating = new DelegatingClassLoader(this.getClass().getClassLoader());

		// build mock class loaders
		mockClassLoaderOne = new MockClassLoaderOne();
		mockClassLoaderTwo = new MockClassLoaderTwo();

		// register to delegating
		delegating.addClassLoader(mockClassLoaderOne);
		delegating.addClassLoader(mockClassLoaderTwo);
	}

	public void testLoadClassUsesParentClassLoader() throws Exception {
		Class clazz = delegating.loadClass("java.lang.String");

		// uses standard
		assertEquals(clazz.getName(), "java.lang.String");

		// Neither should have been called
		assertFalse(mockClassLoaderOne.called);
		assertFalse(mockClassLoaderTwo.called);
	}

	public void testLoadClassFromDelegate() throws Exception {
		Class clazz = delegating.loadClass("xxx");
		assertNotNull(clazz);
		assertTrue(mockClassLoaderOne.called);

		clazz = delegating.loadClass("yyy");
		assertNotNull(clazz);
		assertTrue(mockClassLoaderTwo.called);
	}

	public void testLoadClassThrowsClassNotFound() {
		try {
			delegating.loadClass("does.not.exist.Junk");
			fail("ClassNotFoundException should have been thrown");
		} catch (ClassNotFoundException ignore) {
			// ignore
		}
	}

	/**
	 * Mock one
 	 */
	public static class MockClassLoaderOne extends ClassLoader {
		boolean called = false;

		public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
			called = true;

			if(name.equals("xxx")) {
				return Float.class;
			}

			throw new ClassNotFoundException();
		}
	}

	/**
	 * Mock two
 	 */
	public static class MockClassLoaderTwo extends ClassLoader {
		boolean called = false;

		public synchronized Class loadClass(String name, boolean resolve) throws ClassNotFoundException {
			called = true;

			if(name.equals("yyy")) {
				return Integer.class;
			}

			throw new ClassNotFoundException();
		}
	}
}
