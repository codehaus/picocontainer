package org.picoextras.nanning;

import junit.framework.TestCase;
import org.codehaus.nanning.*;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;
import org.picocontainer.*;
import org.picocontainer.defaults.AssignabilityRegistrationException;
import org.picocontainer.defaults.NotConcreteRegistrationException;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * Contains both unit-tests for the NanninNanoContainer and acceptance-tests outlining an example
 * of how to use it.
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class NanningNanoContainerTestCase extends TestCase {

    private MutablePicoContainer container;

    /**
     * RecordingAware2 very simplified interface to a TransactionManager.
     */
    public static interface TransactionManager {
        Transaction startTransaction();
    }

    public static interface Transaction {
        void commit();

        void rollback();
    }

    /**
     * RecordingAware2 very simplified "declarative" transaction-aspect.
     */
    public static class TransactionAspect implements Aspect {
        Pointcut transactionPointcut = P.all();
        TransactionManager transactionManager;

        public TransactionAspect(TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
        }

        public void introduce(AspectInstance instance) {
            // nothing to introduce
        }

        public void advise(AspectInstance instance) {
            transactionPointcut.advise(instance, new MethodInterceptor() {
                public Object invoke(Invocation invocation) throws Throwable {
                    Transaction transaction = transactionManager.startTransaction();
                    try {
                        Object o = invocation.invokeNext();
                        transaction.commit();
                        return o;
                    } catch (Exception e) {
                        transaction.rollback();
                        throw e;
                    }
                }
            });
        }
    }

    public static interface Component {
        void doSomethingRequiringATransaction() throws Exception;
    }

    public static class SucceedingComponent implements Component {
        public void doSomethingRequiringATransaction() {
            // success!!
        }
    }

    public static class FailingComponent implements Component {
        public void doSomethingRequiringATransaction() throws Exception {
            throw new Exception();
        }
    }

    public static class LoggingTransactionManager implements TransactionManager {
        public StringBuffer transactionLog = new StringBuffer();

        public Transaction startTransaction() {
            transactionLog.append("instantiateComponents ");

            return new Transaction() {
                public void commit() {
                    transactionLog.append("commit ");
                }

                public void rollback() {
                    transactionLog.append("rollback ");
                }
            };
        }
    }

    protected void setUp() throws Exception {
        super.setUp();
        container = new DefaultPicoContainer(new NanningComponentAdapterFactory());
    }

    public void testServiceIsInstantiated() throws PicoRegistrationException, PicoInitializationException {
        container.registerComponentImplementation(TransactionManager.class, LoggingTransactionManager.class);
        container.getComponentInstances();

        assertTrue(container.hasComponent(TransactionManager.class));
        assertNotNull(container.getComponentInstance(TransactionManager.class));
    }

    public void testAspectDependingOnServiceIsInstantiated() throws PicoRegistrationException, PicoInitializationException {
        container.registerComponentImplementation(TransactionManager.class, LoggingTransactionManager.class);
        container.registerComponentImplementation(TransactionAspect.class);
        container.getComponentInstances();

        TransactionAspect transactionAspect = (TransactionAspect) container.getComponentInstance(TransactionAspect.class);
        assertNotNull(transactionAspect);
        assertNotNull(transactionAspect.transactionManager);
    }

    public void testAspectifiedComponentIsInstantiatedWithProperAspects()
            throws PicoRegistrationException, PicoInitializationException {
        container.registerComponentImplementation(TransactionManager.class, LoggingTransactionManager.class);
        container.registerComponentImplementation(TransactionAspect.class);
        container.registerComponentImplementation(Component.class, SucceedingComponent.class);
        container.getComponentInstances();

        Component component = (Component) container.getComponentInstance(Component.class);
        assertNotNull(component);
        assertTrue(Aspects.isAspectObject(component));
        assertEquals(1, Aspects.getAspectInstance(component).getAllInterceptors().size());
    }

    /**
     * The 'acceptance-test' for the example.
     */
    public void testTransactionAspectStartsAndCommitsTransaction() throws PicoRegistrationException, PicoInstantiationException, Exception {
        container.registerComponentImplementation(TransactionManager.class, LoggingTransactionManager.class);
        container.registerComponentImplementation(TransactionAspect.class, TransactionAspect.class);
        container.registerComponentImplementation(Component.class, SucceedingComponent.class);
        container.getComponentInstances();

        Component component = (Component) container.getComponentInstance(Component.class);
        assertNotNull(component);
        component.doSomethingRequiringATransaction();

        LoggingTransactionManager transactionManager = getTransactionManagerTarget();
        assertEquals("instantiateComponents commit ", transactionManager.transactionLog.toString());
    }

    public void testTransactionAspectStartsAndRollsBackTransaction() throws PicoRegistrationException, PicoInstantiationException, Exception {
        container.registerComponentImplementation(TransactionManager.class, LoggingTransactionManager.class);
        container.registerComponentImplementation(TransactionAspect.class);
        container.registerComponentImplementation(Component.class, FailingComponent.class);
        container.getComponentInstances();

        Component component = (Component) container.getComponentInstance(Component.class);
        try {
            component.doSomethingRequiringATransaction();
            fail();
        } catch (Exception shouldHappen) {
        }

        LoggingTransactionManager transactionManager = getTransactionManagerTarget();
        assertEquals("instantiateComponents rollback ", transactionManager.transactionLog.toString());
    }

    private LoggingTransactionManager getTransactionManagerTarget() throws PicoInitializationException, PicoIntrospectionException, AssignabilityRegistrationException, NotConcreteRegistrationException {
        AspectInstance aspectInstance = Aspects.getAspectInstance(container.getComponentInstance(TransactionManager.class));
        Mixin mixin = aspectInstance.getMixinForInterface(TransactionManager.class);
        return (LoggingTransactionManager) mixin.getTarget();
    }

}
