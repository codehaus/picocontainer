/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems.adapters;

import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.adapters.DecoratingComponentAdapter;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.AnnotationVisitor;

import java.lang.reflect.Method;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.objectweb.asm.*;


/**
 * This addComponent addAdapter makes it possible to hide the implementation of a real subject (behind a proxy). If the key of the
 * addComponent is of type {@link Class} and that class represents an interface, the proxy will only implement the interface
 * represented by that Class. Otherwise (if the key is something else), the proxy will implement all the interfaces of the
 * underlying subject. In any case, the proxy will also implement {@link com.thoughtworks.proxy.toys.hotswap.Swappable}, making
 * it possible to swap out the underlying subject at runtime. <p/> <em>
 * Note that this class doesn't cache instances. If you want caching,
 * use a {@link org.picocontainer.adapters.CachingComponentAdapter} around this one.
 * </em>
 *
 * @author Paul Hammant
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ImplementationHidingComponentAdapter extends DecoratingComponentAdapter implements Opcodes {

    public ImplementationHidingComponentAdapter(final ComponentAdapter delegate) {
        super(delegate);
    }

    public Object getComponentInstance(final PicoContainer container) {
        Object key = getComponentKey();
        Object o = getDelegate().getComponentInstance(container);
        if (key instanceof Class) {
            Class clazz = (Class) key;
            if (clazz.isInterface()) {
                byte[] bytes = makeProxy("XX", new Class[] {clazz});
                AsmClassLoader cl = new AsmClassLoader();
                Class pClazz = cl.defineClass("XX", bytes);
                try {
                    Constructor ctor = pClazz.getConstructor(clazz);
                    return ctor.newInstance(o);
                } catch (NoSuchMethodException e) {
                } catch (InstantiationException e) {
                } catch (IllegalAccessException e) {
                } catch (InvocationTargetException e) {
                }
            }
        }
        return o;
    }

    public byte[] makeProxy(String proxyName, Class[] interfaces) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;
        MethodVisitor mv;
        AnnotationVisitor av0;

        Class superclass = Object.class;

        String iName = getName(interfaces[0]);
        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, proxyName, null, getName(superclass), new String[]{iName});

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_TRANSIENT, "delegate", "L" + iName + ";", null, null);
            fv.visitEnd();
        }
        doConstructor(proxyName, cw, interfaces[0]);
        Method[] meths = interfaces[0].getMethods();
        for (Method meth : meths) {

            doMethod(proxyName, cw, interfaces[0], meth);
        }

        cw.visitEnd();

        return cw.toByteArray();
    }

    private void doConstructor(String proxyName, ClassWriter cw, Class iface) {
        MethodVisitor mv;
        String iName = getName(iface);
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L" + iName + ";)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, proxyName, "delegate", "L" + iName + ";");
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void doMethod(String proxyName, ClassWriter cw, Class iface, Method meth) {
        String signature = "(" + getParamTypes(meth) + ")" + getClassType(meth.getReturnType());
        String[] exceptions = getExceptionTypes(meth.getExceptionTypes());
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, meth.getName(), signature, null, exceptions);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, proxyName, "delegate", getClassType(iface));
        Class[] types = meth.getParameterTypes();
        int ix = 1;
        for (Class type : types) {
            int load = whichLoad(type);
            mv.visitVarInsn(load, ix);
            ix = indexOf(ix, load);
        }
        mv.visitMethodInsn(INVOKEINTERFACE, getName(iface), meth.getName(), signature);
        mv.visitInsn(whichReturn(meth.getReturnType()));
        mv.visitMaxs(ix, ix);
        mv.visitEnd();
    }

    private int indexOf(int ix, int loadType) {
        if (loadType == LLOAD) {
            return ix + 2;
        } else if (loadType == DLOAD) {
            return ix + 2;
        } else if (loadType == ILOAD) {
            return ix + 1;
        } else if (loadType == ALOAD) {
            return ix + 1;
        } else if (loadType == FLOAD) {
            return ix + 1;
        }
        return 0;
    }

    private String[] getExceptionTypes(Class[] exceptionTypes) {
        if (exceptionTypes.length == 0) {
            return null;
        }
        String[] retVal = new String[exceptionTypes.length];
        for (int i = 0; i < exceptionTypes.length; i++) {
            Class clazz = exceptionTypes[i];
            retVal[i] = getName(clazz);
        }
        return retVal;
    }

    private int whichReturn(Class clazz) {
        if (!clazz.isPrimitive()) {
            return ARETURN;
        } else if (clazz.isArray()) {
            return ARETURN;
        } else if (clazz == int.class) {
            return IRETURN;
        } else if (clazz == long.class) {
            return LRETURN;
        } else if (clazz == byte.class) {
            return IRETURN;
        } else if (clazz == float.class) {
            return FRETURN;
        } else if (clazz == double.class) {
            return DRETURN;
        } else if (clazz == char.class) {
            return IRETURN;
        } else if (clazz == short.class) {
            return IRETURN;
        } else if (clazz == boolean.class) {
            return IRETURN;
        } else if (clazz == void.class) {
            return RETURN;
        } else {
            return 0;
        }
    }

    private int whichLoad(Class clazz) {
        if (!clazz.isPrimitive()) {
            return ALOAD;
        } else if (clazz.isArray()) {
            return ALOAD;
        } else if (clazz == int.class) {
            return ILOAD;
        } else if (clazz == long.class) {
            return LLOAD;
        } else if (clazz == byte.class) {
            return ILOAD;
        } else if (clazz == float.class) {
            return FLOAD;
        } else if (clazz == double.class) {
            return DLOAD;
        } else if (clazz == char.class) {
            return ILOAD;
        } else if (clazz == short.class) {
            return ILOAD;
        } else if (clazz == boolean.class) {
            return ILOAD;
        } else {
            return 0;
        }
    }

    private String getClassType(Class clazz) {
        if (clazz.getName().startsWith("[")) {
            return getName(clazz);
        } else if (!clazz.isPrimitive()) {
            return "L" + getName(clazz) + ";";
        } else if (clazz == int.class) {
            return "I";
        } else if (clazz == long.class) {
            return "J";
        } else if (clazz == byte.class) {
            return "B";
        } else if (clazz == float.class) {
            return "F";
        } else if (clazz == double.class) {
            return "D";
        } else if (clazz == char.class) {
            return "C";
        } else if (clazz == short.class) {
            return "S";
        } else if (clazz == boolean.class) {
            return "Z";
        } else if (clazz == void.class) {
            return "V";
        } else {
            return null;
        }
    }

    private String getParamTypes(Method meth) {
        String retVal = "";
        for (Class type : meth.getParameterTypes()) {
            retVal += getClassType(type);
        }
        return retVal;
    }

    private String getName(Class type) {
        return type.getName().replace('.', '/');
    }

    private static class AsmClassLoader extends ClassLoader {

        public Class defineClass(String name, byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }



}