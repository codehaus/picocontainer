package org.microcontainer;

import org.codehaus.groovy.runtime.InvokerHelper;
import org.nanocontainer.NanoPicoContainer;
import org.nanocontainer.NanoContainer;
import org.nanocontainer.jmx.MBeanComponentAdapterFactory;
import org.nanocontainer.reflection.DefaultNanoPicoContainer;
import org.nanocontainer.script.groovy.NanoContainerBuilder;
import org.nanocontainer.script.NanoContainerMarkupException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.Parameter;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.ComponentAdapterFactory;
import org.picocontainer.defaults.ConstantParameter;

import groovy.lang.Closure;
import groovy.util.BuilderSupport;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * todo this class should be removed or refactored to extend NanoContainerBuilder.
 * Currently this exists just as an initial trial of nano-jmx and microcontainer.
 *
 */
public class MicroGroovyBuilder  extends BuilderSupport {

    public MicroGroovyBuilder() {
    }

    protected void setParent(Object parent, Object child) {
    }

    protected Object doInvokeMethod(String s, Object name, Object args) {
        //TODO use setDelegate() from Groovy beta 7
        Object answer = super.doInvokeMethod(s, name, args);
        List list = InvokerHelper.asList(args);
        if (!list.isEmpty()) {
            Object o = list.get(list.size() - 1);
            if (o instanceof Closure) {
                Closure closure = (Closure) o;
                closure.setDelegate(answer);
            }
        }
        return answer;
    }

    protected Object createNode(Object name) {
        return createNode(name, Collections.EMPTY_MAP);
    }

    protected Object createNode(Object name, Object value) {
        if (value instanceof Class) {
            Map attributes = new HashMap();
            attributes.put("class", value);
            return createNode(name, attributes);
        }
        return createNode(name);
    }

    protected Object createNode(Object name, Map attributes) {
        Object parent = getCurrent();
        if (name.equals("container")) {
            return createContainerNode(parent, attributes);
        } else if (parent instanceof MutablePicoContainer) {
            try {
                return createChildOfContainerNode(parent, name, attributes);
            } catch (ClassNotFoundException e) {
                throw new NanoContainerMarkupException("ClassNotFoundException:" + e.getMessage(), e);
            }
        }
        throw new NanoContainerMarkupException("Unknown method: '" + name + "'");
    }

    private Object createChildOfContainerNode(Object parent, Object name, Map attributes) throws ClassNotFoundException {
        NanoPicoContainer parentContainer = (NanoPicoContainer) parent;
        if (name.equals("component")) {
            return createComponentNode(attributes, parentContainer, name);
        } else if (name.equals("builder")) {
            return createBuilderNode(attributes, parentContainer);
        } else if (name.equals("bean")) {
            return createBeanNode(attributes, parentContainer);
        } else if (name.equals("classpathelement")) {
            return createClassPathElementNode(attributes, parentContainer);
        } else if (name.equals("management")) {
            return createJMXNode(attributes, parentContainer);
		} else if (name.equals("doCall")) {
            // TODO ????
            //BuilderSupport builder = (BuilderSupport) attributes.remove("class");
            return null;

        }
        throw new NanoContainerMarkupException("Method: '" + name + "' must be a child of a container element");
    }

	private Object createJMXNode(Map attributes, NanoPicoContainer parentContainer) throws ClassNotFoundException {
		Class clazz = Class.forName((String)attributes.remove("class"));
		String key = (String)attributes.get("key");
		List operations = (List)attributes.remove("operations");
		MBeanOperationInfo[] mBeanOperationInfos = new MBeanOperationInfo[operations.size()];

		for(int i = 0; i < operations.size(); i++) {
			mBeanOperationInfos[i] = (MBeanOperationInfo)operations.get(i);
		}

		// register the MBeanInfo
		MBeanInfo mBeanInfo = new MBeanInfo(clazz.getName(), "description", null, null, mBeanOperationInfos, null);
		parentContainer.registerComponentInstance(clazz.getName().concat("MBeanInfo"), mBeanInfo);

		// register the MBean
		ComponentAdapter ca = new MBeanComponentAdapterFactory().createComponentAdapter(key, clazz, null);
		parentContainer.registerComponent(ca);
		
		return parentContainer;
	}

	private Object createContainerNode(Object parent, Map attributes) {
        MutablePicoContainer parentContainer = null;
        if (parent instanceof MutablePicoContainer) {
            parentContainer = (MutablePicoContainer) parent;
        }
        if (parentContainer == null) {
            parentContainer = (MutablePicoContainer) attributes.remove("parent");
        }
        MutablePicoContainer answer = createContainer(attributes, parentContainer);
        return answer;
    }

    private Object createClassPathElementNode(Map attributes, NanoContainer reflectionContainerAdapter) {

        String path = (String) attributes.remove("path");
        URL pathURL = null;
        try {
            if (path.toLowerCase().startsWith("http://")) {
                pathURL = new URL(path);
            } else {
                pathURL = new File(path).toURL();
            }
        } catch (MalformedURLException e) {
            throw new NanoContainerMarkupException("classpath '" + path + "' malformed ", e);
        }
        reflectionContainerAdapter.addClassLoaderURL(pathURL);
        return pathURL;
    }

    private Object createBeanNode(Map attributes, MutablePicoContainer pico) {
        // lets create a bean
        Object answer = createBean(attributes);
        pico.registerComponentInstance(answer);
        return answer;
    }

    private Object createBuilderNode(Map attributes, MutablePicoContainer pico) throws ClassNotFoundException {
        // lets create a bean
        DefaultNanoPicoContainer dpc = new DefaultNanoPicoContainer();
        dpc.registerComponentInstance(MutablePicoContainer.class, pico);
        Object o = attributes.remove("class");
        Object answer = null;
        if (o instanceof Class) {
            answer = dpc.registerComponentImplementation((Class) o).getComponentInstance(pico);
        } else {
            answer = dpc.registerComponentImplementation((String) o).getComponentInstance(pico);
        }
        System.out.println("--> ? " + answer);

        return answer;
    }


    private Object createComponentNode(Map attributes, NanoPicoContainer pico, Object name) throws ClassNotFoundException {
        Object key = attributes.remove("key");
        Object type = attributes.remove("class");
        Object instance = attributes.remove("instance");
        List parameters = (List) attributes.remove("parameters");

        if (type != null) {
            registerComponentImplementation(pico, key, type, parameters);
        } else if (instance != null) {
            registerComponentInstance(pico, key, instance);
        } else {
            throw new NanoContainerMarkupException("Must specify a class attribute for a component");
        }

        return name;
    }


    protected Object createNode(Object name, Map attributes, Object value) {
        return createNode(name, attributes);
    }

    protected MutablePicoContainer createContainer(Map attributes, MutablePicoContainer parent) {
        ComponentAdapterFactory adapterFactory = (ComponentAdapterFactory) attributes.remove("adapterFactory");
        Class containerImpl = (Class) attributes.remove("class");
        String name = (String) attributes.remove("name");
        NanoPicoContainer softPico = null;

        if (containerImpl != null) {
            NanoPicoContainer scpc = null;
            if (parent != null) {
                ClassLoader cl = null;
                if (parent instanceof NanoPicoContainer) {
                    cl = ((NanoPicoContainer) parent).getComponentClassLoader();
                } else {
                    cl = this.getClass().getClassLoader();
                }
                scpc = new DefaultNanoPicoContainer(cl);
                scpc.registerComponentInstance(ClassLoader.class, cl);
                scpc.registerComponentInstance(PicoContainer.class, parent);
            } else {
                scpc = new DefaultNanoPicoContainer(NanoContainerBuilder.class.getClassLoader());
                scpc.registerComponentInstance(ClassLoader.class, NanoContainerBuilder.class.getClassLoader());
            }
            if (adapterFactory != null) {
                scpc.registerComponentInstance(ComponentAdapterFactory.class, adapterFactory);
            }
            scpc.registerComponentImplementation(NanoPicoContainer.class, containerImpl);
            softPico = (NanoPicoContainer) scpc.getComponentInstance(NanoPicoContainer.class);
        } else {

            if (parent != null) {
                ClassLoader cl = null;
                if (parent instanceof NanoPicoContainer) {
                    cl = ((NanoPicoContainer) parent).getComponentClassLoader();
                } else {
                    cl = this.getClass().getClassLoader();
                }
                if (adapterFactory != null) {
                    softPico = new DefaultNanoPicoContainer(cl, adapterFactory, parent);
                } else {
                    softPico = new DefaultNanoPicoContainer(cl, parent);
                }
                if (parent instanceof NanoPicoContainer) {
                    ((NanoPicoContainer) parent).addChildContainer(name, softPico);
                } else {
                    parent.addChildContainer(softPico);
                }
                
            } else {
                if (adapterFactory != null) {
                    softPico = new DefaultNanoPicoContainer(NanoContainerBuilder.class.getClassLoader(), adapterFactory, null);
                } else {
                    softPico = new DefaultNanoPicoContainer();
                }
            }
        }

        return softPico;
    }

    protected Object createBean(Map attributes) {
        Class type = (Class) attributes.remove("beanClass");
        if (type == null) {
            throw new NanoContainerMarkupException("Bean must have a beanClass attribute");
        }
        try {
            Object bean = type.newInstance();
            // now let's set the properties on the bean
            for (Iterator iter = attributes.entrySet().iterator(); iter.hasNext();) {
                Map.Entry entry = (Map.Entry) iter.next();
                String name = entry.getKey().toString();
                Object value = entry.getValue();
                InvokerHelper.setProperty(bean, name, value);
            }
            return bean;
        } catch (IllegalAccessException e) {
            throw new NanoContainerMarkupException("Failed to create bean of type '" + type + "'. Reason: " + e, e);
        } catch (InstantiationException e) {
            throw new NanoContainerMarkupException("Failed to create bean of type " + type + "'. Reason: " + e, e);
        }
    }

    private void registerComponentImplementation(NanoPicoContainer pico,
                                                 Object key, Object type, List paramsList) throws ClassNotFoundException {

        Parameter[] parameters = getParameters(paramsList);
        if (key != null) {
            if (type instanceof String) {
                if (parameters != null) {
                    pico.registerComponentImplementation(key, (String) type, parameters);
                } else {
                    pico.registerComponentImplementation(key, (String) type);
                }
            } else {
                if (parameters != null) {
                    pico.registerComponentImplementation(key, (Class) type, parameters);
                } else {
                    pico.registerComponentImplementation(key, (Class) type);
                }
            }
        } else {
            if (type instanceof String) {
                pico.registerComponentImplementation((String) type);
            } else {
                pico.registerComponentImplementation((Class) type);
            }
        }
    }

    private Parameter[] getParameters(List paramsList) {
        if (paramsList == null) {
            return null;
        }
        int n = paramsList.size();
        Parameter[] parameters = new Parameter[n];
        for (int i = 0; i < n; ++i) {
            parameters[i] = toParameter(paramsList.get(i));
        }
        return parameters;
    }

    private Parameter toParameter(Object obj) {
        return obj instanceof Parameter ? (Parameter) obj : new ConstantParameter(obj);
    }

    private void registerComponentInstance(NanoPicoContainer pico, Object key, Object instance) {
        if (key != null) {
            pico.registerComponentInstance(key, instance);
        } else {
            pico.registerComponentInstance(instance);
        }
    }
}

