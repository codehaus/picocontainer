/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.parameters;

import junit.framework.TestCase;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.DefaultPicoContainer;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.ParameterName;
import org.picocontainer.PicoCompositionException;
import org.picocontainer.testmodel.DependsOnTouchable;
import org.picocontainer.testmodel.SimpleTouchable;
import org.picocontainer.testmodel.Touchable;
import org.picocontainer.visitors.VerifyingVisitor;


/**
 * @author Jon Tirsen (tirsen@codehaus.org)
 */
public final class ParameterTestCase extends TestCase {

    public static class FooParameterName implements ParameterName {
        public String getName() {
            return "";
        }
    }

    final ParameterName pn = new FooParameterName();

    public void testComponentParameterFetches() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        ComponentAdapter adapter = pico.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class);
        assertNotNull(adapter);
        assertNotNull(pico.getComponent(Touchable.class));
        Touchable touchable = (Touchable) Scalar.byClass(Touchable.class).resolveInstance(pico);
        assertNotNull(touchable);
    }

    public void testComponentParameterExcludesSelf() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        ComponentAdapter adapter = pico.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class);

        assertNotNull(pico.getComponent(Touchable.class));
        Touchable touchable = (Touchable) Scalar.byClass(Touchable.class).resolveInstance(pico);
        assertNull(touchable);
    }

    public void testConstant() throws PicoCompositionException {
        Object value = new Object();
        Constant<Object> parameter = new Constant<Object>(value);
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        assertSame(value, parameter.resolveInstance(picoContainer));
    }

    public void testDependsOnTouchableWithTouchableSpecifiedAsConstant() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        SimpleTouchable touchable = new SimpleTouchable();
        pico.addComponent(DependsOnTouchable.class, DependsOnTouchable.class, new Constant(touchable));
        pico.getComponents();
        assertTrue(touchable.wasTouched);
    }

    public void testComponentParameterRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class);
        assertNull(Scalar.byClass(TestCase.class).resolveInstance(picoContainer));
    }
	
	public void testComponentParameterResolvesPrimitiveType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        ComponentAdapter adapter = picoContainer.addComponent("glarch", 239).getComponentAdapter("glarch");
        assertNotNull(adapter);
		Parameter parameter = Scalar.byKey("glarch");
		assertNotNull(parameter.resolveInstance(picoContainer));
		assertEquals(239, ((Integer)parameter.resolveInstance(picoContainer)).intValue());
	}

    public void testConstantRespectsExpectedType() {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Parameter parameter = new Constant(new SimpleTouchable());
        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class);
        assertFalse(parameter.isResolvable(picoContainer));
    }
// as there is no expected type anymore.... 
//    public void testParameterRespectsExpectedType() throws PicoCompositionException {
//        Parameter parameter = new Constant(Touchable.class);
//        MutablePicoContainer picoContainer = new DefaultPicoContainer();
//        assertFalse(parameter.isResolvable(picoContainer));
//
//        ComponentAdapter adapter = picoContainer.addComponent(Touchable.class, SimpleTouchable.class).getComponentAdapter(Touchable.class);
//
//        assertNull(ComponentParameter.DEFAULT.resolveInstance(picoContainer, adapter, TestCase.class, pn, false));
//    }

    public void testConstantWithPrimitives() throws PicoCompositionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = (byte)5;
        Constant parameter = new Constant(byteValue);
        assertSame(byteValue, parameter.resolveInstance(picoContainer));
        assertSame(byteValue, parameter.resolveInstance(picoContainer));
        Short shortValue = (short)5;
        parameter = new Constant(shortValue);
        assertSame(shortValue, parameter.resolveInstance(picoContainer));
        assertSame(shortValue, parameter.resolveInstance(picoContainer));
        Integer intValue = 5;
        parameter = new Constant(intValue);
        assertSame(intValue, parameter.resolveInstance(picoContainer));
        assertSame(intValue, parameter.resolveInstance(picoContainer));
        Long longValue = (long)5;
        parameter = new Constant(longValue);
        assertSame(longValue, parameter.resolveInstance(picoContainer));
        assertSame(longValue, parameter.resolveInstance(picoContainer));
        Float floatValue = new Float(5.5);
        parameter = new Constant(floatValue);
        assertSame(floatValue, parameter.resolveInstance(picoContainer));
        assertSame(floatValue, parameter.resolveInstance(picoContainer));
        Double doubleValue = 5.5;
        parameter = new Constant(doubleValue);
        assertSame(doubleValue, parameter.resolveInstance(picoContainer));
        assertSame(doubleValue, parameter.resolveInstance(picoContainer));
        Boolean booleanValue = true;
        parameter = new Constant(booleanValue);
        assertSame(booleanValue, parameter.resolveInstance(picoContainer));
        assertSame(booleanValue, parameter.resolveInstance(picoContainer));
        Character charValue = 'x';
        parameter = new Constant(charValue);
        assertSame(charValue, parameter.resolveInstance(picoContainer));
        assertSame(charValue, parameter.resolveInstance(picoContainer));
    }

    public void testConstantWithPrimitivesRejectsUnexpectedType() throws PicoCompositionException {
        MutablePicoContainer picoContainer = new DefaultPicoContainer();
        Byte byteValue = (byte)5;
        Constant parameter = new Constant(byteValue);
        assertFalse(parameter.isResolvable(picoContainer));
        Short shortValue = (short)5;
        parameter = new Constant(shortValue);
        assertFalse(parameter.isResolvable(picoContainer));
        Integer intValue = 5;
        parameter = new Constant(intValue);
        assertFalse(parameter.isResolvable(picoContainer));
        Long longValue = (long)5;
        parameter = new Constant(longValue);
        assertFalse(parameter.isResolvable(picoContainer));
        Float floatValue = new Float(5.5);
        parameter = new Constant(floatValue);
        assertFalse(parameter.isResolvable(picoContainer));
        Double doubleValue = 5.5;
        parameter = new Constant(doubleValue);
        assertFalse(parameter.isResolvable(picoContainer));
        Boolean booleanValue = true;
        parameter = new Constant(booleanValue);
        assertFalse(parameter.isResolvable(picoContainer));
        Character charValue = 'x';
        parameter = new Constant(charValue);
        assertFalse(parameter.isResolvable(picoContainer));
    }

    public void testKeyClashBug118() throws PicoCompositionException {
        DefaultPicoContainer pico = new DefaultPicoContainer();
        pico.addComponent("A", String.class, new Constant("A"));
        pico.addComponent("B", String.class, new Constant("A"));
        new VerifyingVisitor().traverse(pico);
    }

}
