package org.nanocontainer.nanoweb.impl;

import java.beans.IntrospectionException;
import java.util.List;
import java.util.Map;

import ognl.Ognl;
import ognl.OgnlException;
import ognl.OgnlRuntime;

import org.nanocontainer.nanoweb.Converter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.AmbiguousComponentResolutionException;
import org.picocontainer.defaults.ObjectReference;

public class OgnlNullHandle implements ognl.NullHandler {

    private ObjectReference picoReference;

    public OgnlNullHandle(ObjectReference picoReference) {
        this.picoReference = picoReference;
    }

    public Object nullMethodResult(Map context, Object target, String methodName, List args) {
        return null;
    }

    public Object nullPropertyValue(Map context, Object target, Object property) {
        if ((target == null) || (property == null)) {
            return null;
        }

        String propName = property.toString();
        Class type;
        try {
            type = OgnlRuntime.getPropertyDescriptor(target.getClass(), propName).getPropertyType();
        } catch (IntrospectionException e) {
            // Nothing to do...
            return null;
        }

        Object value = tryToGet(type);

        if (value != null) {
            try {
                Ognl.setValue(propName, context, target, value);
            } catch (OgnlException e) {
                return null;
            }
        }

        return value;
    }

    private Object tryToGet(Class type) {
        Object value = usingConverter(type);
        if (value != null) {
            return value;
        }

        value = usingPico(type);
        if (value != null) {
            return value;
        }

        return usingClassNewInstance(type);
    }

    private Object usingConverter(Class type) {
        PicoContainer pico = (PicoContainer) picoReference.get();

        Converter converter = Helper.getConverterFor(type, pico);

        if (converter == null) {
            return null;
        }

        return converter.fromString("");
    }

    private Object usingPico(Class type) {
        PicoContainer pico = (PicoContainer) picoReference.get();
        try {
            return pico.getComponentInstanceOfType(type);
        } catch (AmbiguousComponentResolutionException e) {
            return null;
        }
    }

    private Object usingClassNewInstance(Class type) {
        try {
            return type.newInstance();
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        }
    }

}
