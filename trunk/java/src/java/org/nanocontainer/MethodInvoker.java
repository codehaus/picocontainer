package org.nanocontainer;

import org.apache.tools.ant.BuildException;
import org.picocontainer.internals.ComponentRegistry;

import java.util.Collection;
import java.util.Iterator;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class MethodInvoker {
    public void invokeMethod(String methodName, ComponentRegistry componentRegistry) {
        Collection instantiationOrderedComponents = componentRegistry.getOrderedComponents();
        for (Iterator iterator = instantiationOrderedComponents.iterator(); iterator.hasNext();) {
            Object component = iterator.next();
            Class componentClass = component.getClass();
            try {
                Method execute = componentClass.getMethod("execute", null);
                execute.invoke(component, null);
            } catch (NoSuchMethodException e) {
                // ignore
            } catch (SecurityException e) {
                throw new BuildException(e);
            } catch (IllegalAccessException e) {
                // ignore
            } catch (IllegalArgumentException e) {
                // ignore
            } catch (InvocationTargetException e) {
                throw new BuildException(e.getTargetException());
            }
        }
    }
}
