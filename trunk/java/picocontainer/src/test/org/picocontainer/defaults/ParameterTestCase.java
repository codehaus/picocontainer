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
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;

import junit.framework.TestCase;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision: 1.7 $
 */
public class ParameterTestCase extends TestCase {

    public void testComponentParameterFetches() throws PicoInstantiationException, PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        ComponentAdapter adapter = pico.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        ComponentParameter parameter = new ComponentParameter();

        assertNotNull(pico.getComponentInstance(Touchable.class));
        Touchable touchable = (Touchable) parameter.resolveInstance(pico, adapter, Touchable.class);
        assertNotNull(touchable);
    }

    public void testConstantParameter() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertSame(value, parameter.resolveInstance(picoContainer, null, Object.class));
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
        ComponentAdapter adapter = picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        assertNull(parameter.resolveInstance(picoContainer, adapter, TestCase.class));
    }
	
	
	public void testComponentParameterResolvesPrimitiveType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        ComponentAdapter adapter = picoContainer.registerComponentInstance("glarch", new Integer(239));
		Parameter parameter = new ComponentParameter("glarch");
		assertNotNull(parameter.resolveInstance(picoContainer,adapter,Integer.TYPE));
		assertEquals(239, ((Integer)parameter.resolveInstance(picoContainer,adapter,Integer.TYPE)).intValue());
	}

    public void testConstantParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Parameter parameter = new ConstantParameter(new SimpleTouchable());
        ComponentAdapter adapter = picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);
        assertFalse(parameter.isResolvable(picoContainer, adapter, TestCase.class));
    }

    public void testParameterRespectsExpectedType() throws PicoInitializationException, NotConcreteRegistrationException, PicoIntrospectionException {
        Parameter parameter = new ConstantParameter(Touchable.class);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertFalse(parameter.isResolvable(picoContainer, null, TestCase.class));

        parameter = new ComponentParameter();
        ComponentAdapter adapter = picoContainer.registerComponentImplementation(Touchable.class, SimpleTouchable.class);

        assertNull(parameter.resolveInstance(picoContainer, adapter, TestCase.class));
    }

    public void testConstantParameterWithPrimitives() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = new Byte((byte) 5);
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertSame(byteValue, parameter.resolveInstance(picoContainer, null, Byte.TYPE));
        assertSame(byteValue, parameter.resolveInstance(picoContainer, null, Byte.class));
        Short shortValue = new Short((short) 5);
        parameter = new ConstantParameter(shortValue);
        assertSame(shortValue, parameter.resolveInstance(picoContainer, null, Short.TYPE));
        assertSame(shortValue, parameter.resolveInstance(picoContainer, null, Short.class));
        Integer intValue = new Integer(5);
        parameter = new ConstantParameter(intValue);
        assertSame(intValue, parameter.resolveInstance(picoContainer, null, Integer.TYPE));
        assertSame(intValue, parameter.resolveInstance(picoContainer, null, Integer.class));
        Long longValue = new Long(5);
        parameter = new ConstantParameter(longValue);
        assertSame(longValue, parameter.resolveInstance(picoContainer, null, Long.TYPE));
        assertSame(longValue, parameter.resolveInstance(picoContainer, null, Long.class));
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertSame(floatValue, parameter.resolveInstance(picoContainer, null, Float.TYPE));
        assertSame(floatValue, parameter.resolveInstance(picoContainer, null, Float.class));
        Double doubleValue = new Double(5.5);
        parameter = new ConstantParameter(doubleValue);
        assertSame(doubleValue, parameter.resolveInstance(picoContainer, null, Double.TYPE));
        assertSame(doubleValue, parameter.resolveInstance(picoContainer, null, Double.class));
        Boolean booleanValue = new Boolean(true);
        parameter = new ConstantParameter(booleanValue);
        assertSame(booleanValue, parameter.resolveInstance(picoContainer, null, Boolean.TYPE));
        assertSame(booleanValue, parameter.resolveInstance(picoContainer, null, Boolean.class));
        Character charValue = new Character('x');
        parameter = new ConstantParameter(charValue);
        assertSame(charValue, parameter.resolveInstance(picoContainer, null, Character.TYPE));
        assertSame(charValue, parameter.resolveInstance(picoContainer, null, Character.class));
    }

    public void testConstantParameterWithPrimitivesRejectsUnexpectedType() throws PicoInitializationException, AssignabilityRegistrationException, NotConcreteRegistrationException, PicoIntrospectionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = new Byte((byte) 5);
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Integer.TYPE));
        Short shortValue = new Short((short) 5);
        parameter = new ConstantParameter(shortValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE));
        Integer intValue = new Integer(5);
        parameter = new ConstantParameter(intValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE));
        Long longValue = new Long(5);
        parameter = new ConstantParameter(longValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE));
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE));
        Double doubleValue = new Double(5.5);
        parameter = new ConstantParameter(doubleValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE));
        Boolean booleanValue = new Boolean(true);
        parameter = new ConstantParameter(booleanValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE));
        Character charValue = new Character('x');
        parameter = new ConstantParameter(charValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE));
    }

    public void testKeyClashBug118() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.registerComponentImplementation("A", String.class, new Parameter[]{
            new ConstantParameter("A")
        });
        pico.registerComponentImplementation("B", String.class, new Parameter[]{
            new ConstantParameter("A")
        });
        pico.accept(new VerifyingVisitor());
    }

}
