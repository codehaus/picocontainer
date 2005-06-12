package org.nanocontainer.boot;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BrownBear {
    public BrownBear(Honey honey) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        System.out.println("BrownBear: I have eaten " + honey.eatSome() + " calories of Honey (of unknown type)");
        Class clazz = null;
        try {
            clazz = this.getClass().getClassLoader().loadClass("org.nanocontainer.boot.BeeHiveHoney");
        } catch (ClassNotFoundException cnfe) {
        }
        System.out.println("BrownBear: Can see class for BeeHiveHoney ? - " + (clazz != null));
        System.out.println("BrownBear: honey instance's class type - " + honey.getClass());
        Method nonInterfaceMethod = null;
        try {
            nonInterfaceMethod = honey.getClass().getMethod("nonInterfaceMethod", new Class[0]);
        } catch (NoSuchMethodException exception) {
        }
        System.out.println("BrownBear: Can see honey instance's 'nonInterfaceMethod'? - " + (nonInterfaceMethod != null));
        boolean invoked = false;
        try {
            nonInterfaceMethod.invoke(honey, new Object[0]);
            invoked = true;
        } catch (Exception e) {
        }
        System.out.println("BrownBear: Can invoke honey instance's 'nonInterfaceMethod'? - " + invoked);
        nonInterfaceMethod = null;
        if (clazz != null) {
            nonInterfaceMethod = clazz.getMethod("nonInterfaceMethod", new Class[0]);
        }
        System.out.println("BrownBear: Can see HoneyBeeHoney.nonInterfaceMethod()? - " + (nonInterfaceMethod != null));
        invoked = false;
        try {
            nonInterfaceMethod.invoke(honey, new Object[0]);
            invoked = true;
        } catch (Exception e) {
        }
        System.out.println("BrownBear: Can invoke HoneyBeeHoney class' 'nonInterfaceMethod' against honey's instance? - " + invoked);
        System.out.println("BrownBear: Can leverage any implementation detail from honey instance? - false");
    }
}