/*****************************************************************************
 * Copyright (c) PicoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 * Original code by Leo Simons                                               *
 *****************************************************************************/
package org.nanocontainer.ioc.avalon;

// This dependency might be worthwhile getting rid of. Only one method is used. AH.
import com.thoughtworks.proxy.kit.ReflectionUtils;
import com.thoughtworks.xstream.XStream;

import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.xml.sax.SAXException;

import org.apache.avalon.framework.activity.Disposable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.ConfigurationException;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.configuration.DefaultConfigurationBuilder;
import org.apache.avalon.framework.container.ContainerUtil;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.ContextException;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.Log4JLogger;
import org.apache.avalon.framework.logger.LogEnabled;
import org.apache.avalon.framework.logger.LogKitLogger;
import org.apache.avalon.framework.logger.Logger;
import org.apache.avalon.framework.logger.NullLogger;
import org.apache.avalon.framework.service.ServiceException;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.logging.Log;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Set;

/**
 * A utility class containing most of the logic to support handling of the Avalon-Framework semantics within a
 * {@link PicoContainer}.
 * 
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Revision$
 */
public class AvalonUtil {
    /**
     * This key is added onto the end of the key for a particular component to generate the key that will be used to
     * look up the Configuration of the component, if it needs one.
     */
    public final static String CONFIGURATION_POSTFIX = "Configuration";

    /**
     * Helper for the {@link #getConfiguration(Object,PicoContainer)} method.
     */ 
    private static final XStream xstream = new XStream();
    /**
     * Helper for the {@link #getConfiguration(Object,PicoContainer)} method.
     */ 
    private final static DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();

    /**
     * If the argument can be cast to a {@link MutablePicoContainer}, call {@link #enableBasicAvalonSupport(MutablePicoContainer)}
     * on that argument. Otherwise, do nothing.
     * 
     * @param container the argument to try and send to {@link #enableBasicAvalonSupport(MutablePicoContainer)}.
     */
    public static void tryToEnableBasicAvalonSupport(final PicoContainer container) {
        if (container instanceof MutablePicoContainer)
            enableBasicAvalonSupport((MutablePicoContainer) container);
    }

    /**
     * Add any core components required for Avalon-Framework support to the provided container if they do not exist
     * already. Components that are checked for and added if needed a {@link Logger}, a {@link org.apache.log.Logger
     * LogKit logger}, a {@link ServiceManager} and a {@link Context}.
     * 
     * @param target the container to add the core Avalon-Framework components to.
     * @return the container that was provided as an argument.
     */
    public static MutablePicoContainer enableBasicAvalonSupport(final MutablePicoContainer target) {
        if (target.getComponentAdapter(Logger.class) == null)
            addLogger(target);

        if (target.getComponentAdapter(ServiceManager.class) == null)
            addServiceManager(target);

        if (target.getComponentAdapter(Context.class) == null)
            addContext(target);

        return target;
    }

    /**
     * Run a newly created type1 component instance through the first stages of the type1 lifecycle, and wrap it with
     * a proxy object that will make sure that {@link org.apache.avalon.framework.activity.Startable#start()}, {@link org.apache.avalon.framework.activity.Startable#stop()} and {@link
     * Disposable#dispose()}, are correctly called when their counterparts from the {@link org.picocontainer.Startable}
     * and {@link org.picocontainer.Disposable} interfaces are called.
     * 
     * @param key       the key the component is registered with in the container.
     * @param instance  the newly created component.
     * @param container the container the component is registered with.
     * @return a proxy around the provided component that handles the {@link org.apache.avalon.framework.activity.Startable} and {@link org.apache.avalon.framework.activity.Disposable}
     *         contract.
     * @throws Exception if an error occurs initializing the component.
     */
    public static Object handleAvalonLifecycle(final Object key, final Object instance, final PicoContainer container) throws Exception {

        handleLogging(instance, container, key);
        handleContextualization(instance, container);
        handleServicing(instance, container);
        handleConfiguration(instance, key, container);
        handleInitialization(instance);

        return getActivityLifecycleProxy(instance);
    }

    /**
     * Run a newly created type1 component through the <code>enableLogging()</code> lifecycle stage.
     * 
     * @param instance  the newly created component.
     * @param container the container the component is registered with.
     * @param key       the key the component is registered with in the container.
     */ 
    public static void handleLogging(final Object instance, final PicoContainer container, final Object key) {
        if (instance instanceof LogEnabled) {
            Logger logger = getLogger(container);
            if (logger == null)
                logger = new NullLogger();

            String logCategory = key.toString();
            if (logCategory.indexOf('.') != -1)
                logCategory = logCategory.substring(logCategory.indexOf('.'));
            logger = logger.getChildLogger(logCategory);

            ContainerUtil.enableLogging(instance, logger);
        }
    }

    /**
     * Run a newly created type1 component through the <code>contextualize()</code> lifecycle stage.
     * 
     * @param instance  the newly created component.
     * @param container the container the component is registered with.
     * @throws ContextException if the call to {@link Contextualizable#contextualize(Context)} throws a ContextException.
     */ 
    public static void handleContextualization(final Object instance, final PicoContainer container) throws ContextException {
        if (instance instanceof Contextualizable) {
            Context context = getContext(container);
            if (context == null)
                context = new PicoContainerBasedContext(container);
            ContainerUtil.contextualize(instance, context);
        }
    }

    /**
     * Run a newly created type1 component through the <code>service()</code> lifecycle stage.
     * 
     * @param instance  the newly created component.
     * @param container the container the component is registered with.
     * @throws ServiceException if the call to {@link Serviceable#service(ServiceManager)} throws a ServiceException.
     */
    public static void handleServicing(final Object instance, final PicoContainer container) throws ServiceException {
        if (instance instanceof Serviceable) {
            ServiceManager manager = getServiceManager(container);
            if (manager == null)
                manager = new PicoContainerBasedServiceManager(container);
            ContainerUtil.service(instance, manager);
        }
    }

    /**
     * Run a newly created type1 component through the <code>configure()</code> lifecycle stage.
     * 
     * @param instance  the newly created component.
     * @param container the container the component is registered with.
     * @param key       the key the component is registered with in the container.
     * @throws ConfigurationException if the call to {@link Configurable#configure(Configuration)} throws a
     *                  ConfigurationException.
     */ 
    public static void handleConfiguration(final Object instance, final Object key, final PicoContainer container) throws ConfigurationException {
        if (instance instanceof Configurable) {
            ContainerUtil.configure(instance, getConfiguration(key, container));
        }
    }

    /**
     * Run a newly created type1 component through the <code>initialize()</code> lifecycle stage.
     * 
     * @param instance the component to initialize.
     * @throws Exception
     */ 
    public static void handleInitialization(final Object instance) throws Exception {
        ContainerUtil.initialize(instance);
    }

    /**
     * Add a {@link ServiceManager} to the provided container that delegates to the container itself.
     * 
     * @param target the container to which a {@link ServiceManager} should be added.
     */
    public static void addServiceManager(final MutablePicoContainer target) {
        target.registerComponent(ServiceManager.class, new PicoContainerBasedServiceManager(target));
    }

    /**
     * Add a {@link Context} to the provided container that delegates to the container itself.
     * 
     * @param target the container to which a {@link Context} should be added.
     */
    public static void addContext(final MutablePicoContainer target) {
        target.registerComponent(Context.class, new PicoContainerBasedContext(target));
    }

    /**
     * Add a {@link Logger} to the provided container. If the container already contains a {@link
     * org.apache.log4j.Logger Log4J Logger} a {@link org.apache.log.Logger LogKit Logger} or a {@link
     * org.apache.commons.logging.Log Commons-Log}, it will be used as a backend. Otherwise, a {@link NullLogger} will
     * be added.
     * 
     * @param target the container to which a {@link Logger} should be added.
     */
    public static void addLogger(final MutablePicoContainer target) {
        final Logger logger;
        if (target.getComponentAdapter(org.apache.log4j.Logger.class) != null) {
            logger = new Log4JLogger(
                    (org.apache.log4j.Logger) target.getComponent(org.apache.log4j.Logger.class)
            );
        } else if (target.getComponentAdapter(org.apache.log.Logger.class) != null) {
            logger = new LogKitLogger((org.apache.log.Logger) target.getComponent(org.apache.log.Logger.class));
        } else if (target.getComponentAdapter(Log.class) != null) {
            logger = new CommonsLogger((Log) target.getComponent(Log.class));
        } else {
            logger = new NullLogger();
        }
        target.registerComponent(Logger.class, logger);
    }

    /**
     * Retrieve a {@link Logger} from the container.
     * 
     * @param container the container to retrieve the logger from.
     * @return the retrieved logger.
     */
    public static Logger getLogger(final PicoContainer container) {
        return (Logger) container.getComponent(Logger.class);
    }

    /**
     * Retrieve a {@link Context} from the container.
     * 
     * @param container the container to retrieve the context from.
     * @return the retrieved context.
     */
    public static Context getContext(final PicoContainer container) {
        return (Context) container.getComponent(Context.class);
    }

    /**
     * Retrieve a {@link ServiceManager} from the container.
     * 
     * @param container the container to retrieve the service manager from.
     * @return the retrieved service manager.
     */
    public static ServiceManager getServiceManager(final PicoContainer container) {
        return (ServiceManager) container.getComponent(ServiceManager.class);
    }

    /**
     * Retrieve a {@link Configuration} from the container. The key used to locate the configuration will be the
     * provided key, converted to a string, with the {@link #CONFIGURATION_POSTFIX} appended to it. If the result of that
     * retrieval is <code>null</code>, an empty configuration object will be created and returned. If the result of the
     * retrieval is a {@link Configuration} instance, that instance will be returned. If neither is the case, an attempt
     * will be made to convert the object to a <code>Configuration</code> by making use of the {@plainlink XStream xstream
     * library}/
     * 
     * @param key       the key under which the component is registered.
     * @param container the container to retrieve the configuration from.
     * @return the retrieved configuration.
     */
    public static Configuration getConfiguration(final Object key, final PicoContainer container) {
        Configuration configuration = null;
        
        final String usedKey;
        if(key instanceof Class)
            usedKey = ((Class)key).getName();
        else
            usedKey = key.toString();

        final Object containerProvidedConfig = container.getComponent(usedKey + CONFIGURATION_POSTFIX);
        
        if (containerProvidedConfig == null) {
            configuration = new DefaultConfiguration("config");
        } else if (containerProvidedConfig instanceof Configuration) {
            configuration = (Configuration) containerProvidedConfig;
        } else {
            try {
                final String configString = xstream.toXML(containerProvidedConfig);
                final InputStream stream = new ByteArrayInputStream(configString.getBytes());

                configuration = builder.build(stream);
            } catch (SAXException e) {
                // never happens!
                //throw new ConfigurationException("Unable to convert the bean " + containerProvidedConfig +
                //        " to a Configuration object using XStream.", e);
            } catch (IOException e) {
                // never happens!
                //throw new ConfigurationException("Unable to convert the bean " + containerProvidedConfig +
                //        " to a Configuration object using XStream.", e);
            } catch (ConfigurationException e) {
                // does this happen? -PH
            } catch (RuntimeException e) {
                // never happens!
                //if (e instanceof ConfigurationException)
                //    throw (ConfigurationException) e;
                //if (e instanceof RuntimeException)
                //    throw (RuntimeException) e;

                //throw new PicoConfigurationException(e);
            }
        }

        return configuration;
    }

    /**
     * <p>Creates and returns a proxy that redirects calls to the {@link org.picocontainer.Startable Startable} and
     * {@link org.picocontainer.Disposable Disposable} interfaces from PicoContainer to the appropriate calls of the
     * provided type1 component (ie, to the {@link org.apache.avalon.framework.activity.Startable} and {@link org.apache.avalon.framework.activity.Disposable} interfaces implemented by that
     * component, if applicable).</p>
     * 
     * <p>Note that a proxy will be created even if the instance does not implement {@link org.apache.avalon.framework.activity.Startable} or {@link
     * Disposable}, so this method will also always perform implementation hiding, much like {@link
     * org.picocontainer.componentadapters.ImplementationHidingComponentAdapter}.</p>
     * 
     * <p>Also note that the semantics of <code>start()</code> and <code>stop()</code> differ slightly between
     * Avalon-Framework and PicoContainer. In particular, PicoContainer allows multiple calls to <code>start()</code>
     * and <code>stop()</code>, while Avalon-Framework does not. If multiple calls to either of these methods are
     * made on the proxy, a {@link PicoLifecycleException} will be thrown, instead of the methods being called again
     * on the type1 component.</p>
     * 
     * @param instance the instance to wrap with a proxy.
     * @return the proxy wrapped around the provided instance.
     */
    public static Object getActivityLifecycleProxy(final Object instance) {
        final Set allInterfaces = ReflectionUtils.getAllInterfaces(instance.getClass());
        if (allInterfaces.isEmpty())
            throw new PicoLifecycleException("Can't support the type1 lifecycle needs of " + instance.getClass() +
                    ". The class doesn't implement any interfaces so proxying it is not possible.");

         if (instance instanceof org.apache.avalon.framework.activity.Startable)
        {
            allInterfaces.add(org.picocontainer.Startable.class);
            
            // the JVM doesn't like having two near-identical interfaces. It gets confused.
            allInterfaces.remove(org.apache.avalon.framework.activity.Startable.class);
        }
        if (instance instanceof org.apache.avalon.framework.activity.Disposable)
        {
            allInterfaces.add(org.picocontainer.Disposable.class);
            
            // the JVM doesn't like having two near-identical interfaces. It gets confused.
            allInterfaces.remove(org.apache.avalon.framework.activity.Disposable.class);
        }
        
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                (Class[]) allInterfaces.toArray(new Class[allInterfaces.size()]),
                new DelegatingInvocationHandler(instance));
    }

    /**
     * An {@link InvocationHandler} that is used from the {@link AvalonUtil#getActivityLifecycleProxy(Object)} method as
     * the recipient of all calls on the proxy. It will delegate most proxy calls back to the object it wraps. The only
     * exception are calls on the {@link org.picocontainer.Startable Startable} and {@link org.picocontainer.Disposable
     * Disposable} PicoContainer interfaces, which will be redirect to their Avalon-Framework counterparts.
     */
    private static class DelegatingInvocationHandler implements InvocationHandler {
        private static Method START;
        private static Method STOP;
        private static Method DISPOSE;

        static {
            try {
                START = org.picocontainer.Startable.class.getMethod("start", new Class[0]);
                STOP = org.picocontainer.Startable.class.getMethod("stop", new Class[0]);
                DISPOSE = org.picocontainer.Disposable.class.getMethod("dispose", new Class[0]);
            } catch (NoSuchMethodException e) {
                // wont happen throw new InternalError();
            }
        }

        private final Object delegate;
        private boolean started = false;
        private boolean stopped = false;
        //private boolean disposed = false;
        //private boolean isStartable = false;
        //private boolean isDisposable = false;

        /**
         * Construct a new handler delegating to the provided instance.
         * 
         * @param delegate the instance to delegate to.
         */ 
        public DelegatingInvocationHandler(final Object delegate) {
            this.delegate = delegate;
            //isStartable = (delegate instanceof org.apache.type1.framework.activity.Startable);
            //isDisposable = (delegate instanceof org.apache.type1.framework.activity.Disposable);
        }

        /**
         * {@inheritDoc}
         * 
         * @param proxy {@inheritDoc}
         * @param method {@inheritDoc}
         * @param args {@inheritDoc}
         * @return {@inheritDoc}
         * @throws Throwable {@inheritDoc}
         */ 
        public Object invoke(final Object proxy, final Method method, final Object[] args) throws Throwable {
            Object result = null;

            final Class declaringClass = method.getDeclaringClass();
            if (declaringClass.equals(Object.class)) {
                if (method.equals(ReflectionUtils.hashCode)) {
                    // Return the hashCode of ourself, as Proxy.newProxyInstance() may
                    // return cached proxies. We want a unique hashCode for each created proxy!
                    result = new Integer(System.identityHashCode(this));
                } else if (method.equals(ReflectionUtils.equals)) {
                    result = new Boolean(proxy == args[0]);
                } else {
                    // If it's any other method defined by Object, call on ourself.
                    return method.invoke(this, args);
                }
            } else if (START.equals(method)) {
                start();
            } else if (STOP.equals(method)) {
                stop();
            } else if (DISPOSE.equals(method)) {
                dispose();
            } else {
                result = method.invoke(delegate, args);
            }

            return result;
        }

        /**
         * Helper method which handles redirection of <code>start()</code> semantics.
         * 
         * @throws Exception if the call to {@link org.apache.avalon.framework.activity.Startable#start()} throws an exception.
         */ 
        private void start() throws Exception {
            //if(!isStartable) return;
            if (started)
                throw new PicoLifecycleException("Can't start " + delegate.getClass().getName() +
                        " because it was started before and doesn't support starting multiple times!");

            ContainerUtil.start(delegate);
            started = true;
        }

        /**
         * Helper method which handles redirection of <code>stop()</code> semantics.
         * 
         * @throws Exception if the call to {@link org.apache.avalon.framework.activity.Startable#stop()} throws an exception.
         */ 
        private void stop() throws Exception {
            //if(!isStartable) return;
            if (!started)
                throw new PicoLifecycleException("Can't stop " + delegate.getClass().getName() +
                        " because it wasn't started!");
            if (stopped)
                throw new PicoLifecycleException("Can't stop " + delegate.getClass().getName() +
                        " because it was stopped before and doesn't support stopping multiple times!");

            ContainerUtil.stop(delegate);
            stopped = true;
        }

        /**
         * Helper method which handles redirection of <code>dispose()</code> semantics.
         */ 
        private void dispose() {
            //if(!isDisposable) return;
            /* UndeclaredThrowableException will always be thrown, unfortunately, so this is not much use...
            if (disposed)
                throw new PicoLifecycleException("Can't dispose " + delegate.getClass().getName() +
                        " because it was already disposed!");
            if (isStartable && !started)
                throw new PicoLifecycleException("Can't dispose " + delegate.getClass().getName() +
                        " because it was not started!");
            if (isStartable && !stopped)
                throw new PicoLifecycleException("Can't dispose " + delegate.getClass().getName() +
                        " because it was not stopped!");*/

            ContainerUtil.dispose(delegate);
            //disposed = true;
        }
    }
}
