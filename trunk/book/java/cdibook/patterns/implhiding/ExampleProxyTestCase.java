package cdibook.patterns.implhiding;

import junit.framework.TestCase;

import java.lang.reflect.Proxy;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Paul Hammant
 * @version $Revision$
 */
public class ExampleProxyTestCase extends TestCase {

    private Foo foo;
    private Foo fooProxy;

// START SNIPPET: intro
    public interface Foo {
        void doFoo();
    }

    public class DefaultFoo implements Foo {
        // As from interface
        public void doFoo() {
        }
        // Not on interface
        public void doBar() {
        }
    }

// END SNIPPET: intro

    protected void setUp() throws Exception {
        super.setUp();
        makeFooProxy();
    }

// START SNIPPET: setup
    public void makeFooProxy() {

        foo = new DefaultFoo();

        fooProxy = (Foo) Proxy.newProxyInstance(getClass().getClassLoader(),
                new Class[] {Foo.class}, new FooInvocationHandler());
    }

    private class FooInvocationHandler implements InvocationHandler {
        public Object invoke(final Object proxy, final Method method,
                             final Object[] args)
                throws Throwable {
            try {
                return method.invoke(proxy, args);
            } catch (final InvocationTargetException ite) {
                throw ite.getTargetException();
            }
        }
    }

// END SNIPPET: setup

// START SNIPPET: 1
    public void testWithoutDynamicProxy() {

        DefaultFoo defaultFoo = (DefaultFoo) foo;
        // this is perfectly legal.
        defaultFoo.doBar();

    }
// END SNIPPET: 1

// START SNIPPET: 2
    public void testThatDynamicProxyNotCastableBackToImplementation() {

        try {
            DefaultFoo defaultFoo = (DefaultFoo) fooProxy;
            defaultFoo.doBar();
            fail("fooProxy should not be castable back to implementation");
        } catch (ClassCastException e) {
            // expected. You can't cast fooProxy back to DefaultFoo.
        }

    }
// END SNIPPET: 2

// START SNIPPET: 3
    public void testThatDynamicProxyNotSubvertableUsingReflection()
            throws NoSuchMethodException, IllegalAccessException,
                   InvocationTargetException {

        try {
            Method doBar = DefaultFoo.class.getMethod("doBar", new Class[0]);

            // works fine.
            doBar.invoke(foo, new Object[0]);

            // should barf.
            doBar.invoke(fooProxy, new Object[0]);

            fail("Shlould have barfed with 'IllegalArgumentException: object is not an instance of declaring class'");

        } catch (IllegalArgumentException e) {
            // expected
            assertTrue(e.getMessage().indexOf("object is not an instance of declaring class") != -1);
        }
    }
// END SNIPPET: 3

// START SNIPPET: coded    
    public class CodedFooProxy implements Foo {
        Foo delegate;

        public CodedFooProxy(Foo delegate) {
            this.delegate = delegate;
        }

        public void doFoo() {
            delegate.doFoo();
        }
    }
// END SNIPPET: coded


}
