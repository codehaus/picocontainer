package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.net.URL;
import java.net.MalformedURLException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class BeanPropertyComponentAdapterFactoryTestCase extends AbstractComponentAdapterFactoryTestCase {

    public static class Foo {
        public String message;

        public void setMessage(String message) {
            this.message = message;
        }
    }

    public static class Failing {
        public void setMessage(String message) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }

    /**
     * Class that contains all types of Java primitives, to test if they are
     * set correctly.
     *
     * @author Mirko Novakovic
     */
    public static class Primitives {
        public byte byte_;
        public short short_;
        public int int_;
        public long long_;
        public float float_;
        public double double_;
        public boolean boolean_;
        public char char_;
        public File file_;
        public URL url_;

        public void setBoolean_(boolean boolean_) {
            this.boolean_ = boolean_;
        }

        public void setByte_(byte byte_) {
            this.byte_ = byte_;
        }

        public void setChar_(char char_) {
            this.char_ = char_;
        }

        public void setDouble_(double double_) {
            this.double_ = double_;
        }

        public void setFloat_(float float_) {
            this.float_ = float_;
        }

        public void setInt_(int int_) {
            this.int_ = int_;
        }

        public void setLong_(long long_) {
            this.long_ = long_;
        }

        public void setShort_(short short_) {
            this.short_ = short_;
        }

        public void setFile_(File file_) {
            this.file_ = file_;
        }

        public void setUrl_(URL url_) {
            this.url_ = url_;
        }
    }

    /**
     * A component that depends on another component.
     *
     * @author Mirko Novakovic
     */
    public static class A {
        private B b;

        /**
         * @param b The b to set.
         */
        public void setB(B b) {
            this.b = b;
        }
    }

    /**
     * Yet another component.
     *
     * @author Mirko Novakovic
     */
    public static class B {
    }

    public void testSetProperties() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Foo.class);
        Foo foo = (Foo) adapter.getComponentInstance();
        assertNotNull(foo);
        assertEquals("hello", foo.message);
    }

    public void testFailingSetter() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Failing.class);
        try {
            adapter.getComponentInstance();
            fail();
        } catch (PicoInitializationException e) {
        }
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

    public void testDelegateIsAccessible() {
        DecoratingComponentAdapter componentAdapter =
                (DecoratingComponentAdapter) createComponentAdapterFactory().createComponentAdapter(Touchable.class, SimpleTouchable.class, null);

        assertNotNull(componentAdapter.getDelegate());
    }

    private ComponentAdapter createAdapterCallingSetMessage(Class impl) {
        BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) createComponentAdapterFactory();

        Map properties = new HashMap();
        properties.put("message", "hello");
        factory.setProperties(impl, properties);

        ComponentAdapter adapter = factory.createComponentAdapter(impl, impl, null);
        return adapter;
    }

    /**
     * Tests if all attributes that are Java primitive types will be set by the adpater.
     */
    public void testSetPrimitiveProperties() throws MalformedURLException {
        BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) createComponentAdapterFactory();
        Map properties = new HashMap();
        properties.put("byte_", "1");
        properties.put("short_", "2");
        properties.put("int_", "3");
        properties.put("long_", "4");
        properties.put("float_", "5.0");
        properties.put("double_", "6.0");
        properties.put("char_", "a");
        properties.put("boolean_", "true");
        properties.put("file_", "/foo/bar");
        properties.put("url_", "http://www.picocontainer.org/");
        factory.setProperties(Primitives.class, properties);
        Primitives primitives = (Primitives) factory.createComponentAdapter(Primitives.class, Primitives.class, null).getComponentInstance();

        assertNotNull(primitives);
        assertEquals(1, primitives.byte_);
        assertEquals(2, primitives.short_);
        assertEquals(3, primitives.int_);
        assertEquals(4, primitives.long_);
        assertEquals(5.0, primitives.float_, 0.1);
        assertEquals(6.0, primitives.double_, 0.1);
        assertEquals('a', primitives.char_);
        assertEquals(true, primitives.boolean_);
        assertEquals(new File("/foo/bar"), primitives.file_);
        assertEquals(new URL("http://www.picocontainer.org/"), primitives.url_);
    }

    /**
     * Tests if a dependend component will be set by the adpater.
     */
    public void testSetDependenComponent() {
        picoContainer.registerComponentImplementation("b", B.class);
        BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) createComponentAdapterFactory();
        Map properties = new HashMap();

        // the second b is the key of the B implementation
        properties.put("b", "b");
        factory.setProperties(A.class, properties);
        picoContainer.registerComponent(factory.createComponentAdapter(A.class, A.class, null));
        A a = (A) picoContainer.getComponentInstance(A.class);

        assertNotNull(a);
        assertNotNull(a.b);
    }
}
