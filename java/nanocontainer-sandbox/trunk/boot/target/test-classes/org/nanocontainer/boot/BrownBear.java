package org.nanocontainer.boot;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

public class BrownBear {
  public BrownBear(Honey honey) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
    System.out.println("BrownBear: I have eaten " + honey.eatSome() + " calories of Honey" );
    Class clazz = null;
    try {
      clazz = this.getClass().getClassLoader().loadClass("org.nanocontainer.boot.BeeHiveHoney");
    } catch (ClassNotFoundException cnfe) {
    }
    System.out.println("BrownBear: Can see class for BeeHiveHoney ? - " + clazz != null );
    Method nonInterfaceMethod = honey.getClass().getMethod("nonInterfaceMethod", new Class[0]);
    System.out.println("BrownBear: Can see honey instance's 'nonInterfaceMethod'? - " + nonInterfaceMethod != null );
    nonInterfaceMethod = clazz.getMethod("nonInterfaceMethod", new Class[0]);
    System.out.println("BrownBear: Can see HoneyBeeHoney class' 'nonInterfaceMethod'? - " + nonInterfaceMethod != null );
    nonInterfaceMethod.invoke(honey, new Object[0]);
    System.out.println("BrownBear: Can invoke HoneyBeeHoney class' 'nonInterfaceMethod' against honey instance? - " + true );
    BeeHiveHoney honey2 = (BeeHiveHoney) honey;
    System.out.println("BrownBear: Can cast honey instance to HoneyBeeHoney class? - " + true );
    System.out.println("BrownBear: Can leverage any implementation detail from honey instance? - false" );
  }
}