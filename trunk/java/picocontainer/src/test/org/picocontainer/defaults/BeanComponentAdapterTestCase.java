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

import junit.framework.TestCase;
import org.picocontainer.MutablePicoContainer;

import java.util.ArrayList;
import java.util.List;

public class BeanComponentAdapterTestCase extends TestCase {

    public static class A {
        private B b;
        private String string;
        private List list;

        public void setB(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public String getString() {
            return string;
        }

        public void setString(String string) {
            this.string = string;
        }

        public List getList() {
            return list;
        }

        public void setList(List list) {
            this.list = list;
        }
    }

    public static class B {
    }

    public void testDependenciesAreResolved() {
        BeanComponentAdapter aAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class, null));
        BeanComponentAdapter bAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, null));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(aAdapter);
        pico.registerComponentInstance("YO");
        pico.registerComponentImplementation(ArrayList.class);

        A a = (A) aAdapter.getComponentInstance();
        assertNotNull(a.getB());
        assertNotNull(a.getString());
        assertNotNull(a.getList());
    }

    public void testAllUnsatisfiableDependenciesAreSignalled() {
        BeanComponentAdapter aAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class, null));
        BeanComponentAdapter bAdapter = new BeanComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, null));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(aAdapter);

        try {
            aAdapter.getComponentInstance();
        } catch (UnsatisfiableDependenciesException e) {
            e.getUnsatisfiableDependencies().contains(List.class);
            e.getUnsatisfiableDependencies().contains(String.class);
        }
    }
}
