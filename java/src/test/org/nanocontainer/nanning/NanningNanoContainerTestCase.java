package org.nanocontainer.nanning;

import junit.framework.TestCase;
import org.codehaus.nanning.AspectInstance;
import org.codehaus.nanning.Aspects;
import org.codehaus.nanning.Invocation;
import org.codehaus.nanning.MethodInterceptor;
import org.codehaus.nanning.config.Aspect;
import org.codehaus.nanning.config.AspectSystem;
import org.codehaus.nanning.config.P;
import org.codehaus.nanning.config.Pointcut;
import org.picocontainer.PicoInitializationException;
import org.picocontainer.PicoInstantiationException;
import org.picocontainer.PicoRegistrationException;
import org.picocontainer.RegistrationPicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;

/**
 * Contains both unit-tests for the NanninNanoContainer and acceptance-tests outlining an example of how to use it.
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision: 1.10 $
 */
public class NanningNanoContainerTestCase extends TestCase {

    private NanningNanoContainer mainContainer;

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
        AspectSystem as = new AspectSystem();
        mainContainer = new NanningNanoContainer.Default(as);
    }

    public void testServiceIsInstantiated() throws PicoRegistrationException, PicoInitializationException {
        mainContainer.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        mainContainer.instantiateComponents();

        assertTrue(mainContainer.hasComponent(TransactionManager.class));
        assertNotNull(mainContainer.getComponent(TransactionManager.class));
    }

    public void testAspectDependingOnServiceIsInstantiated() throws PicoRegistrationException, PicoInitializationException {
        mainContainer.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        mainContainer.registerServiceOrAspect(TransactionAspect.class);
        mainContainer.instantiateComponents();

        TransactionAspect transactionAspect = (TransactionAspect) mainContainer.getComponent(TransactionAspect.class);
        assertNotNull(transactionAspect);
        assertNotNull(transactionAspect.transactionManager);
    }

    public void testAspectifiedComponentIsInstantiatedWithProperAspects()
            throws PicoRegistrationException, PicoInitializationException {
        mainContainer.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        mainContainer.registerServiceOrAspect(TransactionAspect.class);
        mainContainer.registerComponent(Component.class, SucceedingComponent.class);
        mainContainer.instantiateComponents();

        Component component = (Component) mainContainer.getComponent(Component.class);
        assertNotNull(component);
        assertTrue(Aspects.isAspectObject(component));
        assertEquals(1, Aspects.getAspectInstance(component).getAllInterceptors().size());
    }

    /**
     * The 'acceptance-test' for the example.
     */
    public void testTransactionAspectStartsAndCommitsTransaction() throws PicoRegistrationException, PicoInstantiationException, Exception {
        mainContainer.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        mainContainer.registerServiceOrAspect(TransactionAspect.class);
        mainContainer.registerComponent(Component.class, SucceedingComponent.class);
        mainContainer.instantiateComponents();

        Component component = (Component) mainContainer.getComponent(Component.class);
        assertNotNull(component);
        component.doSomethingRequiringATransaction();

        LoggingTransactionManager transactionManager = (LoggingTransactionManager) mainContainer.getComponent(TransactionManager.class);
        assertEquals("instantiateComponents commit ", transactionManager.transactionLog.toString());
    }

    public void testTransactionAspectStartsAndRollsBackTransaction() throws PicoRegistrationException, PicoInstantiationException, Exception {
        mainContainer.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        mainContainer.registerServiceOrAspect(TransactionAspect.class);
        mainContainer.registerComponent(Component.class, FailingComponent.class);
        mainContainer.instantiateComponents();

        Component component = (Component) mainContainer.getComponent(Component.class);
        try {
            component.doSomethingRequiringATransaction();
            fail();
        } catch (Exception shouldHappen) {
        }

        LoggingTransactionManager transactionManager = (LoggingTransactionManager) mainContainer.getComponent(TransactionManager.class);
        assertEquals("instantiateComponents rollback ", transactionManager.transactionLog.toString());
    }

}
