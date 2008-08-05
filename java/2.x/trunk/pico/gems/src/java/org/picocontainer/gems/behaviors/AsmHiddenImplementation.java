/*****************************************************************************
 * Copyright (C) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by                                                          *
 *****************************************************************************/
package org.picocontainer.gems.behaviors;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Future;

import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.PicoContainer;
import org.picocontainer.behaviors.AbstractBehavior;
import org.picocontainer.behaviors.Cached;


/**
 * This component adapter makes it possible to hide the implementation of a real subject (behind a proxy).
 * The proxy will implement all the interfaces of the
 * underlying subject. If you want caching,
 * use a {@link Cached} around this one.
 *
 * @author Paul Hammant
 */
@SuppressWarnings("serial")
public class AsmHiddenImplementation<T> extends AbstractBehavior<T> implements Opcodes {


	public AsmHiddenImplementation(final ComponentAdapter<T> delegate) {
        super(delegate);
    }

    @Override
	public T getComponentInstance(final PicoContainer container, final java.lang.reflect.Type into) {
        T o = getDelegate().getComponentInstance(container, into);
        Class[] interfaces = o.getClass().getInterfaces();
        if (interfaces.length != 0) {
            byte[] bytes = makeProxy("XX", interfaces, true);
            AsmClassLoader cl = new AsmClassLoader(HotSwappable.Swappable.class.getClassLoader());
            Class<?> pClazz = cl.defineClass("XX", bytes);
            try {
                Constructor<T> ctor = (Constructor<T>) pClazz.getConstructor(HotSwappable.Swappable.class);
                final HotSwappable.Swappable swappable = getSwappable();
                swappable.swap(o);
                return ctor.newInstance(swappable);
            } catch (NoSuchMethodException e) {
            } catch (InstantiationException e) {
            } catch (IllegalAccessException e) {
            } catch (InvocationTargetException e) {
            }
        }
        return o;
    }

    public String getDescriptor() {
        return "Hidden";
    }

    protected HotSwappable.Swappable getSwappable() {
        return new HotSwappable.Swappable();
    }

    public byte[] makeProxy(final String proxyName, final Class[] interfaces, final boolean setter) {

        ClassWriter cw = new ClassWriter(0);
        FieldVisitor fv;

        Class<Object> superclass = Object.class;

        cw.visit(V1_5, ACC_PUBLIC + ACC_SUPER, proxyName, null, Type.getInternalName(superclass), getNames(interfaces));

        {
            fv = cw.visitField(ACC_PRIVATE + ACC_TRANSIENT, "swappable", encodedClassName(HotSwappable.Swappable.class), null, null);
            fv.visitEnd();
        }
        doConstructor(proxyName, cw);
        Set<String> methodsDone = new HashSet<String>();
        for (Class<?> iface : interfaces) {
            Method[] meths = iface.getMethods();
            for (Method meth : meths) {
                if (!methodsDone.contains(meth.toString())) {
                    doMethod(proxyName, cw, iface, meth);
                    methodsDone.add(meth.toString());
                }
            }
        }

        cw.visitEnd();

        return cw.toByteArray();
    }

    private String[] getNames(final Class[] interfaces) {
        String[] retVal = new String[interfaces.length];
        for (int i = 0; i < interfaces.length; i++) {
            retVal[i] = Type.getInternalName(interfaces[i]);
        }
        return retVal;
    }

    private void doConstructor(final String proxyName, final ClassWriter cw) {
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, "<init>", "(L"+ Type.getInternalName(HotSwappable.Swappable.class)+";)V", null, null);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitFieldInsn(PUTFIELD, proxyName, "swappable", encodedClassName(HotSwappable.Swappable.class));
        mv.visitInsn(RETURN);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
    }

    private void doMethod(final String proxyName, final ClassWriter cw, final Class<?> iface, final Method meth) {

        String cn = Type.getInternalName(iface);
        String sig = Type.getMethodDescriptor(meth);

        String[] exceptions = encodedExceptionNames(meth.getExceptionTypes());
        MethodVisitor mv;
        mv = cw.visitMethod(ACC_PUBLIC, meth.getName(), sig, null, exceptions);
        mv.visitCode();
        mv.visitVarInsn(ALOAD, 0);
        mv.visitFieldInsn(GETFIELD, proxyName, "swappable", encodedClassName(HotSwappable.Swappable.class));
        mv.visitMethodInsn(INVOKEVIRTUAL, Type.getInternalName(HotSwappable.Swappable.class), "getInstance", "()Ljava/lang/Object;");
        mv.visitTypeInsn(CHECKCAST, cn);
        Class[] types = meth.getParameterTypes();
        int ix = 1;
        for (Class type : types) {
            int load = whichLoad(type);
            mv.visitVarInsn(load, ix);
            ix = indexOf(ix, load);
        }
        mv.visitMethodInsn(INVOKEINTERFACE, cn, meth.getName(), sig);
        mv.visitInsn(whichReturn(meth.getReturnType()));
        mv.visitMaxs(ix, ix);
        mv.visitEnd();
    }

    private int indexOf(final int ix, final int loadType) {
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

    private String[] encodedExceptionNames(final Class[] exceptionTypes) {
        if (exceptionTypes.length == 0) {
            return null;
        }
        String[] retVal = new String[exceptionTypes.length];
        for (int i = 0; i < exceptionTypes.length; i++) {
            Class clazz = exceptionTypes[i];
            retVal[i] = Type.getInternalName(clazz);
        }
        return retVal;
    }

    private int whichReturn(final Class<?> clazz) {
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

    private int whichLoad(final Class<?> clazz) {
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

    private String encodedClassName(final Class<?> clazz) {
        if (clazz.getName().startsWith("[")) {
            return Type.getInternalName(clazz);
        } else if (!clazz.isPrimitive()) {
            return "L" + Type.getInternalName(clazz) + ";";
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

    private String encodedParameterNames(final Method meth) {
        String retVal = "";
        for (Class<?> type : meth.getParameterTypes()) {
            retVal += encodedClassName(type);
        }
        return retVal;
    }

    private static class AsmClassLoader extends ClassLoader {

        public AsmClassLoader(final ClassLoader parent) {
            super(parent);
        }

        public Class<?> defineClass(final String name, final byte[] b) {
            return defineClass(name, b, 0, b.length);
        }
    }

}