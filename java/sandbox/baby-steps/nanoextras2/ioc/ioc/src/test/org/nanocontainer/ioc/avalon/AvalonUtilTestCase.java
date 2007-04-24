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

import junit.framework.TestCase;
import org.apache.avalon.framework.activity.Initializable;
import org.apache.avalon.framework.configuration.Configurable;
import org.apache.avalon.framework.configuration.Configuration;
import org.apache.avalon.framework.configuration.DefaultConfiguration;
import org.apache.avalon.framework.context.Context;
import org.apache.avalon.framework.context.Contextualizable;
import org.apache.avalon.framework.logger.*;
import org.apache.avalon.framework.service.ServiceManager;
import org.apache.avalon.framework.service.Serviceable;
import org.apache.commons.logging.impl.NoOpLog;
import org.easymock.AbstractMatcher;
import org.easymock.MockControl;
import org.nanocontainer.ioc.avalon.AvalonUtil;
import org.nanocontainer.ioc.avalon.CommonsLogger;
import org.nanocontainer.ioc.avalon.PicoLifecycleException;
import org.picocontainer.ComponentAdapter;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.InstanceComponentAdapter;

import java.io.Serializable;

/**
 * @author <a href="lsimons at jicarilla dot org">Leo Simons</a>
 * @version $Id$
 */
public class AvalonUtilTestCase extends TestCase {
    DefaultPicoContainer c;
    
    public void setUp() throws Exception {
        super.setUp();
        c = new DefaultPicoContainer();
        AvalonUtil.tryToEnableBasicAvalonSupport(c);
    }

    public void testTryToEnableBasicAvalonSupportOnNonMutableContainerIsANoop()
    {
        final MockControl mockControl = MockControl.createStrictControl(PicoContainer.class);
        final PicoContainer mock = (PicoContainer) mockControl.getMock();
        mockControl.replay();
        AvalonUtil.tryToEnableBasicAvalonSupport(mock);
        mockControl.verify();
    }
    
    public void testTryToEnableBasicAvalonSupport()
    {
        // it works twice....yeah yeah this needs better testing...
        AvalonUtil.tryToEnableBasicAvalonSupport(c);
        AvalonUtil.tryToEnableBasicAvalonSupport(c);
        AvalonUtil.tryToEnableBasicAvalonSupport(c);
    }
    
    public void testEnableBasicAvalonSupportRegistersLogger()
    {
        assertNotNull( c.getComponentAdapter(Logger.class) );
        assertEquals( Logger.class, c.getComponentAdapter(Logger.class).getComponentKey() );
        assertTrue(
                Logger.class.isAssignableFrom(
                        c.getComponentAdapter(Logger.class).getComponentImplementation()
                )
        );
    }

    public void testEnableBasicAvalonSupportRegistersServiceManager()
    {
        assertNotNull( c.getComponentAdapter(ServiceManager.class) );
        assertEquals( ServiceManager.class, c.getComponentAdapter(ServiceManager.class).getComponentKey() );
        assertTrue(
                ServiceManager.class.isAssignableFrom(
                        c.getComponentAdapter(ServiceManager.class).getComponentImplementation()
                )
        );
    }

    public void testEnableBasicAvalonSupportRegistersContext()
    {
        assertNotNull( c.getComponentAdapter(Context.class) );
        assertEquals( Context.class, c.getComponentAdapter(Context.class).getComponentKey() );
        assertTrue(
                Context.class.isAssignableFrom(
                        c.getComponentAdapter(Context.class).getComponentImplementation()
                )
        );
    }
    
    public void testHandleLogging() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(LogEnabled.class);
        final LogEnabled mock = (LogEnabled) mockControl.getMock();
        mock.enableLogging((Logger) c.getComponent(Logger.class));
        mockControl.replay();
        
        AvalonUtil.handleAvalonLifecycle(LogEnabled.class, mock, c);
        
        mockControl.verify();
    }
    
    public void testHandleContextualization() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(Contextualizable.class);
        final Contextualizable mock = (Contextualizable) mockControl.getMock();
        mock.contextualize((Context) c.getComponent(Context.class));
        mockControl.replay();
        
        AvalonUtil.handleAvalonLifecycle(Contextualizable.class, mock, c);
        
        mockControl.verify();
    }
    
    public void testHandleServicing() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(Serviceable.class);
        final Serviceable mock = (Serviceable) mockControl.getMock();
        mock.service((ServiceManager) c.getComponent(ServiceManager.class));
        mockControl.replay();
        
        AvalonUtil.handleAvalonLifecycle(Contextualizable.class, mock, c);
        
        mockControl.verify();
    }
    
    public void testHandleConfiguration() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(Configurable.class);
        final Configurable mock = (Configurable) mockControl.getMock();
        
        mock.configure(new DefaultConfiguration("config")); // this tests a little of getConfiguration(), too...
        mockControl.replay();
        
        AvalonUtil.handleAvalonLifecycle(Configurable.class, mock, c);
        
        mockControl.verify();
    }
    
    public void testHandleInitialize() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(Initializable.class);
        final Initializable mock = (Initializable) mockControl.getMock();
        
        mock.initialize();
        mockControl.replay();
        
        AvalonUtil.handleAvalonLifecycle(Initializable.class, mock, c);
        
        mockControl.verify();
    }
    
    public void testHandleStartWrapsAPicoStartableProperly() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(org.apache.avalon.framework.activity.Startable.class);
        final org.apache.avalon.framework.activity.Startable mock = (org.apache.avalon.framework.activity.Startable) mockControl.getMock();
        mock.start();
        mock.stop();
        mockControl.replay();
        
        final Object result = AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Startable.class, mock, c);
        assertTrue(result instanceof org.picocontainer.Startable);
        
        final org.picocontainer.Startable s = (org.picocontainer.Startable)result;
        s.start();
        s.stop();
        
        mockControl.verify();
    }
    
    public void testHandleStartThrowsExceptionOnMultipleStarts() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(org.apache.avalon.framework.activity.Startable.class);
        final org.apache.avalon.framework.activity.Startable mock = (org.apache.avalon.framework.activity.Startable) mockControl.getMock();
        mock.start();
        mockControl.replay();
        
        final Object result = AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Startable.class, mock, c);
        assertTrue(result instanceof org.picocontainer.Startable);
        
        final org.picocontainer.Startable s = (org.picocontainer.Startable)result;
        s.start();
        try
        {
            s.start();
            fail( "Expected an exception!" );
        }
        catch( PicoLifecycleException th ) {}
        mockControl.verify();
    }
    
    public void testHandleStartThrowsExceptionOnMultipleStops() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(org.apache.avalon.framework.activity.Startable.class);
        final org.apache.avalon.framework.activity.Startable mock = (org.apache.avalon.framework.activity.Startable) mockControl.getMock();
        mock.start();
        mock.stop();
        mockControl.replay();
        
        final Object result = AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Startable.class, mock, c);
        assertTrue(result instanceof org.picocontainer.Startable);
        
        final org.picocontainer.Startable s = (org.picocontainer.Startable)result;
        s.start();
        s.stop();
        try
        {
            s.stop();
            fail( "Expected an exception!" );
        }
        catch( PicoLifecycleException th ) {}
        mockControl.verify();
    }
    
    public void testHandleStartThrowsExceptionOnStopBeforeStart() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(org.apache.avalon.framework.activity.Startable.class);
        final org.apache.avalon.framework.activity.Startable mock = (org.apache.avalon.framework.activity.Startable) mockControl.getMock();
        mockControl.replay();
        
        final Object result = AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Startable.class, mock, c);
        assertTrue(result instanceof org.picocontainer.Startable);
        
        final org.picocontainer.Startable s = (org.picocontainer.Startable)result;
        try
        {
            s.stop();
            fail( "Expected an exception!" );
        }
        catch( PicoLifecycleException th ) {}
        mockControl.verify();
    }
    
    public void testHandleDisposeWrapsAPicoDisposableProperly() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(org.apache.avalon.framework.activity.Disposable.class);
        final org.apache.avalon.framework.activity.Disposable mock = (org.apache.avalon.framework.activity.Disposable) mockControl.getMock();
        mock.dispose();
        mockControl.replay();
        
        final Object result = AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Disposable.class, mock, c);
        assertTrue(result instanceof org.picocontainer.Disposable);
        
        final org.picocontainer.Disposable d = (org.picocontainer.Disposable)result;
        d.dispose();
        
        mockControl.verify();
    }
    
    public void testHandleDispose() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(org.apache.avalon.framework.activity.Disposable.class);
        final org.apache.avalon.framework.activity.Disposable mock = (org.apache.avalon.framework.activity.Disposable) mockControl.getMock();
        mockControl.replay();
        
        AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Disposable.class, mock, c);
        
        mockControl.verify();
    }
    
    public void testHandleDisposeDoesNotThrowExceptionOnDisposeBeforeStop() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(StartAndDispose.class);
        final StartAndDispose mock = (StartAndDispose) mockControl.getMock();
        mock.start();
        mock.dispose();
        mockControl.replay();
        
        final Object d = AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Disposable.class, mock, c);
        ((org.picocontainer.Startable)d).start();
        ((org.picocontainer.Disposable)d).dispose();
        
        mockControl.verify();
    }
    
    public void testHandleDisposeDoesNotThrowExceptionOnDisposeBeforeStart() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(StartAndDispose.class);
        final StartAndDispose mock = (StartAndDispose) mockControl.getMock();
        mock.dispose();
        mockControl.replay();
        
        final org.picocontainer.Disposable d = (org.picocontainer.Disposable) AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Disposable.class, mock, c);
        d.dispose();
        
        mockControl.verify();
    }
    
    public void testHandleDisposeDoesNotThrowExceptionOnMultipleDisposes() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(org.apache.avalon.framework.activity.Disposable.class);
        final org.apache.avalon.framework.activity.Disposable mock = (org.apache.avalon.framework.activity.Disposable) mockControl.getMock();
        mock.dispose();
        mock.dispose();
        mockControl.replay();
        
        final org.picocontainer.Disposable d = (org.picocontainer.Disposable) AvalonUtil.handleAvalonLifecycle(org.apache.avalon.framework.activity.Disposable.class, mock, c);
        d.dispose();
        d.dispose();
        
        mockControl.verify();
    }
    
    public void testHandleAvalonLifecycle() throws Exception
    {
        final MockControl mockControl = MockControl.createStrictControl(AllLifecycles.class);
        final AllLifecycles mock = (AllLifecycles) mockControl.getMock();

        mock.enableLogging((Logger) c.getComponent(Logger.class));
        mock.contextualize((Context) c.getComponent(Context.class));
        mock.service((ServiceManager) c.getComponent(ServiceManager.class));
        mock.configure(new DefaultConfiguration("config")); // this tests a little of getConfiguration(), too...
        mock.initialize();
        mock.start();
        mock.stop();
        mock.dispose();
        mockControl.replay();
        
        final Object result = AvalonUtil.handleAvalonLifecycle(AllLifecycles.class, mock, c);
        assertTrue(result instanceof org.picocontainer.Startable);
        assertTrue(result instanceof org.picocontainer.Disposable);
        
        ((org.picocontainer.Startable)result).start();
        ((org.picocontainer.Startable)result).stop();
        ((org.picocontainer.Disposable)result).dispose();
        
        mockControl.verify();
    }
    
    public void testHandleAvalonLifecycleWithAnOpaqueKey() throws Exception
    {
        final MockControl mockControl = MockControl.createStrictControl(AllLifecycles.class);
        final AllLifecycles mock = (AllLifecycles) mockControl.getMock();

        mock.enableLogging((Logger) c.getComponent(Logger.class));
        mock.contextualize((Context) c.getComponent(Context.class));
        mock.service((ServiceManager) c.getComponent(ServiceManager.class));
        mock.configure(new DefaultConfiguration("config")); // this tests a little of getConfiguration(), too...
        mock.initialize();
        mock.start();
        mock.stop();
        mock.dispose();
        mockControl.replay();
        
        final Object result = AvalonUtil.handleAvalonLifecycle("blah", mock, c);
        assertTrue(result instanceof org.picocontainer.Startable);
        assertTrue(result instanceof org.picocontainer.Disposable);
        
        ((org.picocontainer.Startable)result).start();
        ((org.picocontainer.Startable)result).stop();
        ((org.picocontainer.Disposable)result).dispose();
        
        mockControl.verify();
    }
    
    public void testHandleAvalonLifecycleWithoutPreparation() throws Exception
    {
        final boolean[] initialized = new boolean[] { false };
        final boolean[] started = new boolean[] { false };
        final boolean[] stopped = new boolean[] { false };
        final boolean[] disposed = new boolean[] { false };
        
        final AllLifecycles mock = new AllLifecycles() {
            public void enableLogging(final Logger logger) {
                assertNotNull(logger);
            }

            public void contextualize(final Context context) {
                assertNotNull(context);
            }

            public void service(final ServiceManager serviceManager) {
                assertNotNull(serviceManager);
            }

            public void configure(final Configuration configuration) {
                assertNotNull(configuration);
            }

            public void initialize() throws Exception {
                initialized[0] = true;
            }

            public void start() throws Exception {
                started[0] = true;
            }

            public void stop() throws Exception {
                stopped[0] = true;
            }

            public void dispose() {
                disposed[0] = true;
            }

        };
        final DefaultPicoContainer container = new DefaultPicoContainer();
        
        final Object comp = AvalonUtil.handleAvalonLifecycle(AllLifecycles.class, mock, container);

        container.registerComponent(comp);
        container.start();
        container.stop();
        container.dispose();
        
        assertTrue(initialized[0]);
        assertTrue(started[0]);
        assertTrue(stopped[0]);
        assertTrue(disposed[0]);
    }
    
    public void testAddLoggerBasedOnLog4JLogger() throws Exception
    {
        final MockControl mockControl = MockControl.createStrictControl(MutablePicoContainer.class);
        final MutablePicoContainer mock = (MutablePicoContainer) mockControl.getMock();
        
        final org.apache.log4j.Logger logger = org.apache.log4j.LogManager.getLogger("blah");
        final ComponentAdapter adapter = new InstanceComponentAdapter(org.apache.log4j.Logger.class, logger);
        
        mock.getComponentAdapter(org.apache.log4j.Logger.class);
        mockControl.setReturnValue(adapter);
        mock.getComponent(org.apache.log4j.Logger.class);
        mockControl.setReturnValue(logger);
        mock.registerComponent(Logger.class, new Log4JLogger( logger ));
        
        // any Log4JLogger will have to do...
        mockControl.setMatcher(
                new AbstractMatcher() {
                    int currentCall = 0;
                    
                    protected boolean parameterMatches(final Object expected, final Object actual) {
                        if(currentCall == 0)
                        {
                            currentCall++;
                            return super.parameterMatches(expected,actual);
                        }
                        
                        if (!(actual instanceof Log4JLogger)) {
                            return false;
                        }
                        return true;
                    }
                }
        );
        mockControl.setReturnValue(adapter);
        
        mockControl.replay();
        AvalonUtil.addLogger(mock);
        mockControl.verify();
    }

    public void testAddLoggerBasedOnLogKitLogger() throws Exception
    {
        final MockControl mockControl = MockControl.createStrictControl(MutablePicoContainer.class);
        final MutablePicoContainer mock = (MutablePicoContainer) mockControl.getMock();
        
        final org.apache.log.Logger logger = org.apache.log.Hierarchy.getDefaultHierarchy().getRootLogger(); 
        final ComponentAdapter adapter = new InstanceComponentAdapter(org.apache.log.Logger.class, logger);
        
        mock.getComponentAdapter(org.apache.log4j.Logger.class);
        mockControl.setReturnValue(null);
        mock.getComponentAdapter(org.apache.log.Logger.class);
        mockControl.setReturnValue(adapter);
        mock.getComponent(org.apache.log.Logger.class);
        mockControl.setReturnValue(logger);
        mock.registerComponent(Logger.class, new LogKitLogger( logger ));
        
        // any LogKitLogger will have to do...
        mockControl.setMatcher(
                new AbstractMatcher() {
                    int currentCall = 0;
                    
                    protected boolean parameterMatches(final Object expected, final Object actual) {
                        if(currentCall == 0)
                        {
                            currentCall++;
                            return super.parameterMatches(expected,actual);
                        }
                        
                        if (!(actual instanceof LogKitLogger)) {
                            return false;
                        }
                        return true;
                    }
                }
        );
        mockControl.setReturnValue(adapter);
        
        mockControl.replay();
        AvalonUtil.addLogger(mock);
        mockControl.verify();
    }

    public void testAddLoggerBasedOnCommonsLogger() throws Exception
    {
        final MockControl mockControl = MockControl.createStrictControl(MutablePicoContainer.class);
        final MutablePicoContainer mock = (MutablePicoContainer) mockControl.getMock();
        
        final org.apache.commons.logging.Log logger = new NoOpLog(); 
        final ComponentAdapter adapter = new InstanceComponentAdapter(org.apache.commons.logging.Log.class, logger);
        
        mock.getComponentAdapter(org.apache.log4j.Logger.class);
        mockControl.setReturnValue(null);
        mock.getComponentAdapter(org.apache.log.Logger.class);
        mockControl.setReturnValue(null);
        mock.getComponentAdapter(org.apache.commons.logging.Log.class);
        mockControl.setReturnValue(adapter);
        mock.getComponent(org.apache.commons.logging.Log.class);
        mockControl.setReturnValue(logger);
        mock.registerComponent(Logger.class, new CommonsLogger( logger ));
        
        // any CommonsLogger will have to do...
        mockControl.setMatcher(
                new AbstractMatcher() {
                    int currentCall = 0;
                    
                    protected boolean parameterMatches(final Object expected, final Object actual) {
                        if(currentCall == 0)
                        {
                            currentCall++;
                            return super.parameterMatches(expected,actual);
                        }
                        
                        if (!(actual instanceof CommonsLogger)) {
                            return false;
                        }
                        return true;
                    }
                }
        );
        mockControl.setReturnValue(adapter);
        
        mockControl.replay();
        AvalonUtil.addLogger(mock);
        mockControl.verify();
    }
    
    public void testAddLoggerBasedOnNullLogger() throws Exception {
        final MockControl mockControl = MockControl.createStrictControl(MutablePicoContainer.class);
        final MutablePicoContainer mock = (MutablePicoContainer) mockControl.getMock();
        
        mock.getComponentAdapter(org.apache.log4j.Logger.class);
        mockControl.setReturnValue(null);
        mock.getComponentAdapter(org.apache.log.Logger.class);
        mockControl.setReturnValue(null);
        mock.getComponentAdapter(org.apache.commons.logging.Log.class);
        mockControl.setReturnValue(null);
        mock.registerComponent(Logger.class, new NullLogger());
        
        // any NullLogger will do...
        mockControl.setMatcher(
                new AbstractMatcher() {
                    int currentCall = 0;
                    
                    protected boolean parameterMatches(final Object expected, final Object actual) {
                        if(currentCall == 0)
                        {
                            currentCall++;
                            return super.parameterMatches(expected,actual);
                        }
                        
                        if (!(actual instanceof NullLogger)) {
                            return false;
                        }
                        return true;
                    }
                }
        );
        mockControl.setReturnValue(null);
        
        mockControl.replay();
        AvalonUtil.addLogger(mock);
        mockControl.verify();
    }
    
    public void testGetConfiguration() throws Exception {
        assertNotNull(AvalonUtil.getConfiguration("key", c));
    }
    
    public void testGetConfigurationDirectlyFromContainer() throws Exception {
        final DefaultConfiguration config = new DefaultConfiguration("blaat");
        
        c.registerComponent(this.getClass().getName()+AvalonUtil.CONFIGURATION_POSTFIX, config);
        assertEquals( config, AvalonUtil.getConfiguration(this.getClass(),c));
        assertEquals( config, AvalonUtil.getConfiguration(this.getClass().getName(),c));
    }
    
    public void testGetConfigurationUsingXStream() throws Exception {
        final SomeBean config = new SomeBean();
        config.setValue("blaat");
        
        c.registerComponent(this.getClass().getName()+AvalonUtil.CONFIGURATION_POSTFIX, config);
        assertNotNull( AvalonUtil.getConfiguration(this.getClass(),c) );
        
        assertEquals( "blaat", AvalonUtil.getConfiguration(this.getClass().getName(),c).getChild("value").getValue());
        assertEquals( "stuff", AvalonUtil.getConfiguration(this.getClass().getName(),c).getValue("stuff"));
    }
    
    public void testGetActivityLifecycleProxyRequiresAnInterface() throws Exception {
        try
        {
            AvalonUtil.getActivityLifecycleProxy(new Object());
            fail( "Expected an exception!" );
        }
        catch( PicoLifecycleException th ) {}
    }

    public void testGetActivityLifecycleProxyHandlesEqualsAndHashCodeProperly() throws Exception {
        final Object obj = new Serializable()
        {
            public String toString()
            {
                return "blaat";
            }
        };
        final Object proxy = AvalonUtil.getActivityLifecycleProxy(obj);
        
        assertFalse(proxy.equals(obj));
        assertTrue(proxy.equals(proxy));
        
        assertTrue( proxy.hashCode() == proxy.hashCode() );
        assertFalse( proxy.hashCode() == obj.hashCode() );
        
        assertFalse( "blaat".equals( proxy.toString() ) );
    }

    /* Xstream eats exceptions...
    public void testGetConfigurationUsingXStreamWrapsSaxException() throws Exception {
        SAXException se = new SAXException("blaat");
        ExceptionBean config = new ExceptionBean(se);
        
        c.registerComponent(this.getClass().getName()+AvalonUtil.CONFIGURATION_POSTFIX, config);
        
        try
        {
            AvalonUtil.getConfiguration(this.getClass().getName(),c);
            fail( "Expected an exception!" );
        }
        catch( ConfigurationException th )
        {
            assertEquals( se, th.getCause() );
        }
    }

    public void testGetConfigurationUsingXStreamWrapsIOException() throws Exception {
        IOException se = new IOException("blaat");
        ExceptionBean config = new ExceptionBean(se);
        
        c.registerComponent(this.getClass().getName()+AvalonUtil.CONFIGURATION_POSTFIX, config);
        
        try
        {
            AvalonUtil.getConfiguration(this.getClass().getName(),c);
            fail( "Expected an exception!" );
        }
        catch( ConfigurationException th )
        {
            assertEquals( se, th.getCause() );
        }
    }*/

    public interface AllLifecycles extends LogEnabled, Contextualizable, Serviceable, Configurable, Initializable, org.apache.avalon.framework.activity.Startable, org.apache.avalon.framework.activity.Disposable {}
    public interface StartAndDispose extends org.apache.avalon.framework.activity.Startable, org.apache.avalon.framework.activity.Disposable {}
    
    public class SomeBean
    {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /*public class ExceptionBean
    {
        private Exception ex;
        
        public ExceptionBean( Exception ex )
        {
            this.ex = ex;
        }

        public String getValue() throws Exception {
            throw ex;
        }
    }*/
}
