package org.nanocontainer.nanoweb.impl;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import ognl.DefaultTypeConverter;
import ognl.Ognl;
import ognl.OgnlException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nanocontainer.nanoweb.Converter;
import org.nanocontainer.nanoweb.defaults.OgnlExpressionEvaluator;
import org.picocontainer.PicoContainer;

/**
 * @version $Id: OgnlTypeConverterAdapter.java 8 2005-05-27 07:45:55Z juze $
 */
public class OgnlTypeConverterAdapter extends DefaultTypeConverter {

    private final transient Log log = LogFactory.getLog(Converter.class); // Yes, Its converter.

    private final PicoContainer pico;

    public OgnlTypeConverterAdapter(final PicoContainer pico) {
        this.pico = pico;
    }

    public Object convertValue(Map ctx, Object target, Member member, String propertyName, Object value, Class toType) {

        if (Collection.class.isAssignableFrom(toType) && (member instanceof Method)) {
            return doCollection(ctx, target, member, propertyName, (String[]) value, toType);
        }

        return super.convertValue(ctx, target, member, propertyName, value, toType);
    }

    private Object doCollection(Map ctx, Object target, Member member, String propertyName, String[] value, Class toType) {
        Class collectionType = getGenericType((Method) member);
        if (collectionType == null) {
            log.warn("Impossible to determine collection member type for " + member.getName() + ".");
            return null;
        }

        Collection collection;
        if (OgnlExpressionEvaluator.COLLECTION_OPERATION_REMOVE.equals(ctx.get(OgnlExpressionEvaluator.CTX_KEY_COLLECTION_OPERATION))) {
            try {
                collection = (Collection) Ognl.getValue(propertyName, target);
            } catch (OgnlException e) {
                throw new RuntimeException(e); // TODO No runtime exception
            }
            for (int i = 0; i < value.length; i++) {
                removeValueIfNotNull(collection, ctx, value[i], collectionType);
            }
        } else {
            if (OgnlExpressionEvaluator.COLLECTION_OPERATION_ADD.equals(ctx.get(OgnlExpressionEvaluator.CTX_KEY_COLLECTION_OPERATION))) {
                try {
                    collection = (Collection) Ognl.getValue(propertyName, target);
                } catch (OgnlException e) {
                    throw new RuntimeException(e); // TODO No runtime exception
                }
            } else {
                collection = newCollectionInstance(toType);
            }

            for (int i = 0; i < value.length; i++) {
                addValueIfNotNull(collection, ctx, value[i], collectionType);
            }
        }

        return collection;
    }

    public Object convertValue(Map ctx, Object value, Class toType) {

        log.debug("Begining to perform a conversion from \"" + value + "\" to \"" + toType + "\".");

        if ((value instanceof String[]) && (((String[]) value).length != 1)) {
            super.convertValue(ctx, value, toType);
        }

        if ((value instanceof String[]) && (((String[]) value).length == 1)) {
            value = ((String[]) value)[0];
        }

        try {
            Class converterType;
            if (value instanceof String) {
                converterType = toType;
            } else {
                converterType = value.getClass();
            }

            Converter converter = Helper.getConverterFor(converterType, pico);

            Object ret = null;
            if (converter != null) {
                if (value instanceof String) {
                    return converter.fromString((String) value);
                }

                return converter.toString(value);
            }

            ret = super.convertValue(ctx, value, toType);

            if (ret == null) {
                log.info("It was not possible to perform a conversion from \"" + value + "\" to \"" + toType + "\".");
            }

            return ret;

        }
        finally {
            log.debug("Ending to perfor a conversion from \"" + value + "\" to \"" + toType + "\".");
        }
    }

    private Class getGenericType(Method member) {
        Type[] types = member.getGenericParameterTypes();
        if ((types.length != 1) || (!(types[0] instanceof ParameterizedType))) {
            return null;
        }

        ParameterizedType type = (ParameterizedType) types[0];
        return (Class) type.getActualTypeArguments()[0];
    }

    private void addValueIfNotNull(Collection collection, Map ctx, String value, Class toType) {
        Object realValue = convertValue(ctx, value, toType);

        if (realValue != null) {
            collection.add(realValue);
        }
    }

    private void removeValueIfNotNull(Collection collection, Map ctx, String value, Class toType) {
        Object realValue = convertValue(ctx, value, toType);

        if (realValue != null) {
            collection.remove(realValue);
        }
    }

    /**
     * Instaciate a new collection. It needs to be improved if not componetized. A good idea is to support annotation in
     * order to let the bean developer decide witch implementation to use. TODO Improve.
     */
    private Collection newCollectionInstance(Class type) {
        if (type.isInterface()) {
            return newCollectionInstanceFromInterface(type);
        }

        try {
            return (Collection) type.newInstance();
        } catch (Exception e) {
            return null;
        }
    }

    private Collection newCollectionInstanceFromInterface(Class type) {
        if (type.isAssignableFrom(SortedSet.class)) {
            return new TreeSet();
        }

        if (type.isAssignableFrom(Set.class)) {
            return new HashSet();
        }

        if (type.isAssignableFrom(List.class)) {
            return new ArrayList();
        }

        return null;
    }

}
