package org.nanocontainer.nanowar.java5.nanoweb;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.nanocontainer.nanowar.nanoweb.CollectionTypeResolver;

/**
 * CollectionTypeResolver implementation for Java 5 generics.
 * 
 * @see org.nanocontainer.nanowar.nanoweb.CollectionTypeResolver
 * 
 * @version $Id$
 */
public class GenericCollectionTypeResolver implements CollectionTypeResolver {

    public Class getType(Member member) {
        if (member instanceof Method) {
            return getType((Method) member);
        }

        return null;
    }

    private Class getType(Method getter) {
        Type[] types = getter.getGenericParameterTypes();
        if ((types.length != 1) || (!(types[0] instanceof ParameterizedType))) {
            return null;
        }

        ParameterizedType type = (ParameterizedType) types[0];
        return (Class) type.getActualTypeArguments()[0];
    }

}
