package org.picocontainer.defaults;

import java.util.Map;
import java.util.HashMap;
import java.lang.reflect.Method;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class SetterIntrospector {

    public Map getSetters(Class clazz) {
        Map result = new HashMap();
        Method[] methods = clazz.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            if (isSetter(method)) {
                result.put(getPropertyName(method), method);
            }
        }
        return result;
    }

    private String getPropertyName(Method method) {
        final String name = method.getName();
        String result = name.substring(3);
        if(result.length() > 1 && !Character.isUpperCase(result.charAt(1))) {
            result = "" + Character.toLowerCase(result.charAt(0)) + result.substring(1);
        } else if(result.length() == 1) {
            result = result.toLowerCase();
        }
        return result;
    }

    private boolean isSetter(Method method) {
        final String name = method.getName();
        return name.length() > 3 &&
                name.startsWith("set") &&
                method.getParameterTypes().length == 1;
    }

}
