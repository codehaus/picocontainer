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
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision: 1.7 $
 */
public class ParameterTestCase extends TestCase {

    public void testComponentParameterFetches() throws PicoInstantiationException, PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        ComponentParameter parameter = new ComponentParameter();

        assertNotNull(pico.getComponentInstance(Touchable.class));
        Touchable touchable = (Touchable) parameter.resolveAdapter(pico, Touchable.class).getComponentInstance();
        assertNotNull(touchable);
    }

    public void testConstantParameter() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertSame(value, parameter.resolveAdapter(picoContainer, Object.class).getComponentInstance());
    }

    public void testDependsOnTouchableWithTouchableSpecifiedAsConstant() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        SimpleTouchable touchable = new SimpleTouchable();
        pico.registerComponentImplementation(DependsOnTouchable.class, DependsOnTouchable.class, new Parameter[]{
            new ConstantParameter(touchable)
        });
        pico.getComponentInstances();
        assertTrue(touchable.wasTouched);
    }

    public void testComponentParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Parameter parameter = new ComponentParameter();
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        assertNull(parameter.resolveAdapter(picoContainer, TestCase.class));
    }

    public void testConstantParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Parameter parameter = new ConstantParameter(new SimpleTouchable());
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        assertNull(parameter.resolveAdapter(picoContainer, TestCase.class));
    }

    public void testParameterRespectsExpectedType() throws PicoInitializationException, NotConcreteRegistrationException, PicoIntrospectionException {
        Parameter parameter = new ConstantParameter(Touchable.class);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertNull(parameter.resolveAdapter(picoContainer, TestCase.class));
        parameter = new ComponentParameter();
        picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        assertNull(parameter.resolveAdapter(picoContainer, TestCase.class));
    }

    public void testConstantParameterWithPrimitives() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = new Byte((byte) 5);
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertSame(byteValue, parameter.resolveAdapter(picoContainer, Byte.TYPE).getComponentInstance());
        assertSame(byteValue, parameter.resolveAdapter(picoContainer, Byte.class).getComponentInstance());
        Short shortValue = new Short((short) 5);
        parameter = new ConstantParameter(shortValue);
        assertSame(shortValue, parameter.resolveAdapter(picoContainer, Short.TYPE).getComponentInstance());
        assertSame(shortValue, parameter.resolveAdapter(picoContainer, Short.class).getComponentInstance());
        Integer intValue = new Integer(5);
        parameter = new ConstantParameter(intValue);
        assertSame(intValue, parameter.resolveAdapter(picoContainer, Integer.TYPE).getComponentInstance());
        assertSame(intValue, parameter.resolveAdapter(picoContainer, Integer.class).getComponentInstance());
        Long longValue = new Long(5);
        parameter = new ConstantParameter(longValue);
        assertSame(longValue, parameter.resolveAdapter(picoContainer, Long.TYPE).getComponentInstance());
        assertSame(longValue, parameter.resolveAdapter(picoContainer, Long.class).getComponentInstance());
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertSame(floatValue, parameter.resolveAdapter(picoContainer, Float.TYPE).getComponentInstance());
        assertSame(floatValue, parameter.resolveAdapter(picoContainer, Float.class).getComponentInstance());
        Double doubleValue = new Double(5.5);
        parameter = new ConstantParameter(doubleValue);
        assertSame(doubleValue, parameter.resolveAdapter(picoContainer, Double.TYPE).getComponentInstance());
        assertSame(doubleValue, parameter.resolveAdapter(picoContainer, Double.class).getComponentInstance());
        Boolean booleanValue = new Boolean(true);
        parameter = new ConstantParameter(booleanValue);
        assertSame(booleanValue, parameter.resolveAdapter(picoContainer, Boolean.TYPE).getComponentInstance());
        assertSame(booleanValue, parameter.resolveAdapter(picoContainer, Boolean.class).getComponentInstance());
        Character charValue = new Character('x');
        parameter = new ConstantParameter(charValue);
        assertSame(charValue, parameter.resolveAdapter(picoContainer, Character.TYPE).getComponentInstance());
        assertSame(charValue, parameter.resolveAdapter(picoContainer, Character.class).getComponentInstance());
    }

    public void testConstantParameterWithPrimitivesRejectsUnexpectedType() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = new Byte((byte) 5);
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertNull(parameter.resolveAdapter(picoContainer, Integer.TYPE));
        Short shortValue = new Short((short) 5);
        parameter = new ConstantParameter(shortValue);
        assertNull(parameter.resolveAdapter(picoContainer, Byte.TYPE));
        Integer intValue = new Integer(5);
        parameter = new ConstantParameter(intValue);
        assertNull(parameter.resolveAdapter(picoContainer, Byte.TYPE));
        Long longValue = new Long(5);
        parameter = new ConstantParameter(longValue);
        assertNull(parameter.resolveAdapter(picoContainer, Byte.TYPE));
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertNull(parameter.resolveAdapter(picoContainer, Byte.TYPE));
        Double doubleValue = new Double(5.5);
        parameter = new ConstantParameter(doubleValue);
        assertNull(parameter.resolveAdapter(picoContainer, Byte.TYPE));
        Boolean booleanValue = new Boolean(true);
        parameter = new ConstantParameter(booleanValue);
        assertNull(parameter.resolveAdapter(picoContainer, Byte.TYPE));
        Character charValue = new Character('x');
        parameter = new ConstantParameter(charValue);
        assertNull(parameter.resolveAdapter(picoContainer, Byte.TYPE));
    }

    public void testKeyClashBug118() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("A", String.class, new Parameter[]{
            new ConstantParameter("A")
        });
        pico.registerComponentImplementation("B", String.class, new Parameter[]{
            new ConstantParameter("A")
        });
        pico.verify();
    }

}
