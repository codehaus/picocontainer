/*
 * Copyright (C) 2004 Joerg Schaible
 * Created on 29.06.2004 by joehni
 */
package org.picocontainer.defaults.issues;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.DependsOnTwoComponents;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;

/**
 * Play with ComponentParameter inspired by Rickard &Ouml;berg
 * @author J&ouml;rg Schaible
 */
public class SiblingPicoAndInternalComponentsTestCase
        extends TestCase {

    final static public class InternalComponentParameter implements Parameter {

        private final PicoContainer m_internalPicoContainer;
        public InternalComponentParameter(PicoContainer internal) {
            m_internalPicoContainer = internal;
        }
        /**
         * {@inheritDoc}
         * @see org.picocontainer.Parameter#resolveAdapter(org.picocontainer.PicoContainer, java.lang.Class)
         */
        public ComponentAdapter resolveAdapter(PicoContainer picoContainer, Class expectedType) {
            return m_internalPicoContainer.getComponentAdapterOfType(expectedType);
        }
        
    }

    final static public class SiblingContainerComponentParameter implements Parameter {

        private final List m_listOfPicoContainers;
        public SiblingContainerComponentParameter(List list) {
            m_listOfPicoContainers = list;
        }
        /**
         * {@inheritDoc}
         * @see org.picocontainer.Parameter#resolveAdapter(org.picocontainer.PicoContainer, java.lang.Class)
         */
        public ComponentAdapter resolveAdapter(PicoContainer picoContainer, Class expectedType) {
            ComponentAdapter result = picoContainer.getComponentAdapterOfType(expectedType);
            if (result == null) {
                for (Iterator it = m_listOfPicoContainers.iterator(); result == null && it.hasNext();) {
                    PicoContainer pico = (PicoContainer)it.next();
                    if (pico != picoContainer) {
                        result = pico.getComponentAdapterOfType(expectedType);
                    }
                }
            }
            return result;
        }
        
    }
    
    private MutablePicoContainer systemPico;
    
    /**
     * {@inheritDoc}
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();
        systemPico = new DefaultPicoContainer();
        DefaultPicoContainer systemPicoInternal = new DefaultPicoContainer(systemPico);
        systemPico.registerComponentImplementation(DependsOnTouchable.class, DependsOnTouchable.class, new Parameter[] {
                new InternalComponentParameter(systemPicoInternal)
        });
        systemPicoInternal.registerComponentImplementation(SimpleTouchable.class);
    }
    
    final public void testInternalComponentParameter() {
        DependsOnTouchable dot = (DependsOnTouchable)systemPico.getComponentInstanceOfType(DependsOnTouchable.class);
        assertNotNull(dot);
    }
    
    final public void testSiblingComponentParameter() {
        DefaultPicoContainer picoA = new DefaultPicoContainer();
        DefaultPicoContainer picoB = new DefaultPicoContainer();
        List picoList = new ArrayList();
        picoList.add(picoA);
        picoList.add(picoB);
        picoList.add(systemPico);
        picoA.registerComponentImplementation(DependsOnTwoComponents.class, DependsOnTwoComponents.class, new Parameter[] {
                new SiblingContainerComponentParameter(picoList),
                new SiblingContainerComponentParameter(picoList),
        });
        
        try {
            picoA.getComponentInstancesOfType(DependsOnTwoComponents.class);
            fail("Should not have found internal Touchable");
        } catch (PicoIntrospectionException e) {
        }
        
        picoB.registerComponentImplementation(SimpleTouchable.class);
        DependsOnTwoComponents dotc = (DependsOnTwoComponents)picoA.getComponentInstanceOfType(DependsOnTwoComponents.class);
        assertNotNull(dotc);
    }
}
