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
import org.picocontainer.Parameter;

import java.util.ArrayList;
import java.util.List;

public class SetterInjectionComponentAdapterTestCase extends TestCase {

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
        SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class, null));
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, null));

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
        SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class, null));
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, null));

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

    public void testShouldBeAbleToTakeParameters() {
        ArrayList list = new ArrayList();
        Parameter[] aParameters = new Parameter[]{
            new ComponentParameter(),
            new ConstantParameter("YO"),
            new ConstantParameter(list)
        };
        SetterInjectionComponentAdapter aAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("a", A.class), aParameters);
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(aAdapter);

        A a = (A) aAdapter.getComponentInstance();
        assertNotNull(a.getB());
        assertEquals("YO", a.getString());
        assertSame(list, a.getList());
    }

    public static class C {
        private B b;
        private List l;
        private final boolean asBean;

        public C() {
            asBean = true;
        }

        public C(B b) {
            this.l = new ArrayList();
            this.b = b;
            asBean = false;
        }

        public void setB(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public void setList(List l) {
            this.l = l;
        }

        public List getList() {
            return l;
        }

        public boolean instantiatedAsBean() {
            return asBean;
        }
    }

    public void notSureWhatThisIsTestingOrWhyItIsNeeded___testHybrids() {
        SetterInjectionComponentAdapter bAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("b", B.class, new Parameter[]{}));
        SetterInjectionComponentAdapter cAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("c", C.class, new Parameter[]{}));
        SetterInjectionComponentAdapter cNullAdapter = new SetterInjectionComponentAdapter(new ConstructorInjectionComponentAdapter("c0", C.class, null));

        MutablePicoContainer pico = new DefaultPicoContainer();
        pico.registerComponent(bAdapter);
        pico.registerComponent(cAdapter);
        pico.registerComponent(cNullAdapter);
        pico.registerComponentImplementation(ArrayList.class);

        C c = (C) cAdapter.getComponentInstance();
        assertTrue(c.instantiatedAsBean());
        C c0 = (C) cNullAdapter.getComponentInstance();
        assertTrue(c0.instantiatedAsBean());
    }
}
