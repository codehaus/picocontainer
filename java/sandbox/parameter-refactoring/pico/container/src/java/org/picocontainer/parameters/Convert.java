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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.picocontainer.PicoCompositionException;
import org.picocontainer.PicoContainer;

/**
 * perform necessary data conversion
 * @author Konstantin Pribluda
 * @author Paul Hammant
 *
 */
public class Convert<T> implements Extract<T> {
	
	private static interface Converter {
        Object convert(String paramValue);
    }
	private static class NewInstanceConverter implements Converter> {
	    private Constructor c;
	
	    private NewInstanceConverter(Class clazz) {
	        try {
	            c = clazz.getConstructor(String.class);
	        } catch (NoSuchMethodException e) {
	        }
	    }
	
	    public Object  convert(String paramValue) {
	        try {
	            return  c.newInstance(paramValue);
	        } catch (IllegalAccessException e) {
	        } catch (InvocationTargetException e) {
	        } catch (InstantiationException e) {
	        }
	        return null;
	    }
	}
	
	
	private static class ValueOfConverter implements Converter {
	    private Method m;
	    private ValueOfConverter(Class clazz) {
	        try {
	            m = clazz.getMethod("valueOf", String.class);
	        } catch (NoSuchMethodException e) {
	        }
	    }
	
	    public Object convert(String paramValue) {
	        try {
	            return m.invoke(null, paramValue);
	        } catch (IllegalAccessException e) {
	        } catch (InvocationTargetException e) {
	        }
	        return null;
	
	    }
	}


    private static final Map<Class, Converter> stringConverters = new HashMap<Class, Converter>();

	static {
        stringConverters.put(Integer.class, new ValueOfConverter(Integer.class));
        stringConverters.put(Double.class, new ValueOfConverter(Double.class));
        stringConverters.put(Boolean.class, new ValueOfConverter(Boolean.class));
        stringConverters.put(Long.class, new ValueOfConverter(Long.class));
        stringConverters.put(Float.class, new ValueOfConverter(Float.class));
        stringConverters.put(Character.class, new ValueOfConverter(Character.class));
        stringConverters.put(Byte.class, new ValueOfConverter(Byte.class));
        stringConverters.put(Byte.class, new ValueOfConverter(Short.class));
        stringConverters.put(File.class, new NewInstanceConverter(File.class));

    }
	
    
    private static Converter obtainConverter(Class clazz) {
    	synchronized (stringConverters) {
    		Converter converter = stringConverters.get(clazz);
    		if(converter == null) {
    			if(clazz.getConstructor(String.class)!= null) {
    				converter = new NewInstanceConverter(clazz);
    				stringConverters.put(clazz,converter);
    			} else if(clazz.getMethod("valueOf", String.class) != null) {
    				converter = new ValueOfConverter(clazz);
    				stringConverters.put(clazz,converter);
    			}
    		}
    		return converter;
		}
    }
	Class<T> expectedClass;
	Extract extract;

	/**
	 * create type converter
	 * @param expectedClass
	 * @param extract
	 */
	public Convert(Class<T> expectedClass, Extract extract) {
		this.expectedClass = expectedClass;
		this.extract = extract;
	}

	@SuppressWarnings("unchecked")
	public T resolveInstance(PicoContainer container) {
		
		Object value = extract.resolveInstance(container);
		if(value instanceof String && !String.class.equals(expectedClass)) {
			// convert it
			Converter converter = obtainConverter(expectedClass);
			if(converter != null) {
				return (T) converter.convert((String)value);
			} else {
				throw new PicoCompositionException("No converter available for coversion of " + value.getClass() + " to " + expectedClass);
			}
		}
		return (T) value;
	}
	
	
	public String toString() {
		return "Convert to " + expectedClass  + " from (" + extract + ")";
	}

}
