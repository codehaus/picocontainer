/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.defaults;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.tck.AbstractComponentAdapterFactoryTestCase;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Aslak Helles&oslash;y
 * @author Mirko Novakovic
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

    public static class A {
        private B b;

        public void setB(B b) {
            this.b = b;
        }
    }

    public static class B {
    }

    public void testSetProperties() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Foo.class);
        Foo foo = (Foo) adapter.getComponentInstance(null);
        assertNotNull(foo);
        assertEquals("hello", foo.message);
    }

    public void testFailingSetter() {
        ComponentAdapter adapter = createAdapterCallingSetMessage(Failing.class);
        try {
            adapter.getComponentInstance(null);
            fail();
        } catch (PicoInitializationException e) {
        }
    }

    protected ComponentAdapterFactory createComponentAdapterFactory() {
        return new BeanPropertyComponentAdapterFactory(new DefaultComponentAdapterFactory());
    }

    public void testPropertiesSetAfterAdapterCreationShouldBeTakenIntoAccount() {
        BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) createComponentAdapterFactory();

        BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.createComponentAdapter("foo", Foo.class, null);

        Map properties = new HashMap();
        properties.put("message", "hello");
        adapter.setProperties(properties);

        Foo foo = (Foo) adapter.getComponentInstance(null);

        assertEquals("hello", foo.message);
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

        BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.createComponentAdapter(impl, impl, null);
        adapter.setProperties(properties);
        return adapter;
    }

    public void testAllJavaPrimitiveAttributesShouldBeSetByTheAdapter() throws MalformedURLException {
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
        BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.createComponentAdapter(Primitives.class, Primitives.class, null);
        adapter.setProperties(properties);
        Primitives primitives = (Primitives) adapter.getComponentInstance(null);

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

    public void testSetDependenComponentWillBeSetByTheAdapter() {
        picoContainer.registerComponentImplementation("b", B.class);
        BeanPropertyComponentAdapterFactory factory = (BeanPropertyComponentAdapterFactory) createComponentAdapterFactory();
        Map properties = new HashMap();

        // the second b is the key of the B implementation
        properties.put("b", "b");
        BeanPropertyComponentAdapter adapter = (BeanPropertyComponentAdapter) factory.createComponentAdapter(A.class, A.class, null);
        adapter.setProperties(properties);
        picoContainer.registerComponent(adapter);
        A a = (A) picoContainer.getComponentInstance(A.class);

        assertNotNull(a);
        assertNotNull(a.b);
    }
}
