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

import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoIntrospectionException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.ParameterName;
import org.picocontainer.parameters.ConstantParameter;
import org.picocontainer.parameters.ComponentParameter;
import org.picocontainer.visitors.VerifyingVisitor;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision: 1.7 $
 */
public final class ParameterTestCase extends TestCase {

    public static class FooParameterName implements ParameterName {
        public String getParameterName() {
            return "";
        }
    }

    final ParameterName pn = new FooParameterName();

    public void testComponentParameterFetches() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        ComponentAdapter adapter = pico.addComponent(Touchable.class, SimpleTouchable.class).lastCA();
        assertNotNull(adapter);
        assertNotNull(pico.getComponent(Touchable.class));
        Touchable touchable = (Touchable) ComponentParameter.DEFAULT.resolveInstance(pico, null, Touchable.class, pn);
        assertNotNull(touchable);
    }

    public void testComponentParameterExcludesSelf() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        ComponentAdapter adapter = pico.addComponent(Touchable.class, SimpleTouchable.class).lastCA();

        assertNotNull(pico.getComponent(Touchable.class));
        Touchable touchable = (Touchable) ComponentParameter.DEFAULT.resolveInstance(pico, adapter, Touchable.class, pn);
        assertNull(touchable);
    }

    public void testConstantParameter() throws PicoInitializationException, NotConcreteRegistrationException, PicoIntrospectionException {
        Object value = new Object();
        ConstantParameter parameter = new ConstantParameter(value);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertSame(value, parameter.resolveInstance(picoContainer, null, Object.class, pn));
    }

    public void testDependsOnTouchableWithTouchableSpecifiedAsConstant() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        SimpleTouchable touchable = new SimpleTouchable();
        pico.addComponent(DependsOnTouchable.class, DependsOnTouchable.class, new Parameter[]{
            new ConstantParameter(touchable)
        });
        pico.getComponents();
        assertTrue(touchable.wasTouched);
    }

    public void testComponentParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).lastCA();
        assertNull(ComponentParameter.DEFAULT.resolveInstance(picoContainer, adapter, TestCase.class, pn));
    }
	
	public void testComponentParameterResolvesPrimitiveType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        ComponentAdapter adapter = picoContainer.addComponent("glarch", new Integer(239)).lastCA();
        assertNotNull(adapter);
		Parameter parameter = new ComponentParameter("glarch");
		assertNotNull(parameter.resolveInstance(picoContainer,null,Integer.TYPE, pn));
		assertEquals(239, ((Integer)parameter.resolveInstance(picoContainer,null,Integer.TYPE, pn)).intValue());
	}

    public void testConstantParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Parameter parameter = new ConstantParameter(new SimpleTouchable());
        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).lastCA();
        assertFalse(parameter.isResolvable(picoContainer, adapter, TestCase.class, pn));
    }

    public void testParameterRespectsExpectedType() throws PicoInitializationException, NotConcreteRegistrationException, PicoIntrospectionException {
        Parameter parameter = new ConstantParameter(Touchable.class);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertFalse(parameter.isResolvable(picoContainer, null, TestCase.class, pn));

        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).lastCA();

        assertNull(ComponentParameter.DEFAULT.resolveInstance(picoContainer, adapter, TestCase.class, pn));
    }

    public void testConstantParameterWithPrimitives() throws PicoInitializationException, NotConcreteRegistrationException, PicoIntrospectionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = new Byte((byte) 5);
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertSame(byteValue, parameter.resolveInstance(picoContainer, null, Byte.TYPE, pn));
        assertSame(byteValue, parameter.resolveInstance(picoContainer, null, Byte.class, pn));
        Short shortValue = new Short((short) 5);
        parameter = new ConstantParameter(shortValue);
        assertSame(shortValue, parameter.resolveInstance(picoContainer, null, Short.TYPE, pn));
        assertSame(shortValue, parameter.resolveInstance(picoContainer, null, Short.class, pn));
        Integer intValue = new Integer(5);
        parameter = new ConstantParameter(intValue);
        assertSame(intValue, parameter.resolveInstance(picoContainer, null, Integer.TYPE, pn));
        assertSame(intValue, parameter.resolveInstance(picoContainer, null, Integer.class, pn));
        Long longValue = new Long(5);
        parameter = new ConstantParameter(longValue);
        assertSame(longValue, parameter.resolveInstance(picoContainer, null, Long.TYPE, pn));
        assertSame(longValue, parameter.resolveInstance(picoContainer, null, Long.class, pn));
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertSame(floatValue, parameter.resolveInstance(picoContainer, null, Float.TYPE, pn));
        assertSame(floatValue, parameter.resolveInstance(picoContainer, null, Float.class, pn));
        Double doubleValue = new Double(5.5);
        parameter = new ConstantParameter(doubleValue);
        assertSame(doubleValue, parameter.resolveInstance(picoContainer, null, Double.TYPE, pn));
        assertSame(doubleValue, parameter.resolveInstance(picoContainer, null, Double.class, pn));
        Boolean booleanValue = new Boolean(true);
        parameter = new ConstantParameter(booleanValue);
        assertSame(booleanValue, parameter.resolveInstance(picoContainer, null, Boolean.TYPE, pn));
        assertSame(booleanValue, parameter.resolveInstance(picoContainer, null, Boolean.class, pn));
        Character charValue = new Character('x');
        parameter = new ConstantParameter(charValue);
        assertSame(charValue, parameter.resolveInstance(picoContainer, null, Character.TYPE, pn));
        assertSame(charValue, parameter.resolveInstance(picoContainer, null, Character.class, pn));
    }

    public void testConstantParameterWithPrimitivesRejectsUnexpectedType() throws PicoInitializationException, NotConcreteRegistrationException, PicoIntrospectionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = (byte)5;
        ConstantParameter parameter = new ConstantParameter(byteValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Integer.TYPE, pn));
        Short shortValue = (short)5;
        parameter = new ConstantParameter(shortValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn));
        Integer intValue = 5;
        parameter = new ConstantParameter(intValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn));
        Long longValue = (long)5;
        parameter = new ConstantParameter(longValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn));
        Float floatValue = new Float(5.5);
        parameter = new ConstantParameter(floatValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn));
        Double doubleValue = 5.5;
        parameter = new ConstantParameter(doubleValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn));
        Boolean booleanValue = true;
        parameter = new ConstantParameter(booleanValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn));
        Character charValue = 'x';
        parameter = new ConstantParameter(charValue);
        assertFalse(parameter.isResolvable(picoContainer, null, Byte.TYPE, pn));
    }

    public void testKeyClashBug118() throws PicoRegistrationException, PicoInitializationException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent("A", String.class, new ConstantParameter("A"));
        pico.addComponent("B", String.class, new ConstantParameter("A"));
        new VerifyingVisitor().traverse(pico);
    }

}
