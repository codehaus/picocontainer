package nanocontainer.nanning;

import com.tirsen.nanning.config.Aspect;
import com.tirsen.nanning.config.Pointcut;
import com.tirsen.nanning.config.AllPointcut;
import com.tirsen.nanning.config.AspectSystem;
import com.tirsen.nanning.AspectInstance;
import com.tirsen.nanning.MethodInterceptor;
import com.tirsen.nanning.Invocation;
import com.tirsen.nanning.Aspects;
import com.tirsen.nanning.MixinInstance;
import junit.framework.TestCase;
import picocontainer.PicoInstantiationException;
import picocontainer.PicoRegistrationException;
import picocontainer.PicoIntrospectionException;
import picocontainer.defaults.DefaultComponentFactory;
import picocontainer.defaults.DefaultPicoContainer;

/**
 * Contains both unit-tests for the NanninNanoContainer and acceptance-tests outlining an example of how to use it.
 * @author Jon Tirsen (tirsen@codehaus.org)
 * @version $Revision$
 */
public class NanningNanoContainerTestCase extends TestCase {
    private NanningNanoContainer container;

    /**
     * Aa very simplified interface to a TransactionManager.
     */
    public static interface TransactionManager {
        Transaction startTransaction();
    }

    public static interface Transaction {
        void commit();
        void rollback();
    }

    /**
     * Aa very simplified "declarative" transaction-aspect.
     */
    public static class TransactionAspect implements Aspect {
        Pointcut transactionPointcut = new AllPointcut();
        TransactionManager transactionManager;

        public TransactionAspect(TransactionManager transactionManager) {
            this.transactionManager = transactionManager;
        }

        public void introduce(AspectInstance instance) {
            // nothing to introduce
        }

        public void adviseMixin(AspectInstance aspectInstance, MixinInstance mixinInstance) {
            //TODO - Jon
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

    public static interface Component  {
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
        container = new NanningNanoContainer(new DefaultComponentFactory(), new DefaultPicoContainer.Default(),new AspectSystem());
    }

    public void testStartService() throws PicoInstantiationException, PicoRegistrationException, PicoIntrospectionException {
        container.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        container.instantiateComponents();

        assertNotNull(container.getComponent(TransactionManager.class));
    }

    public void testStartAspectDependingOnService() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {
        container.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        container.registerServiceOrAspect(TransactionAspect.class);
        container.instantiateComponents();

        TransactionAspect transactionAspect = (TransactionAspect) container.getComponent(TransactionAspect.class);
        assertNotNull(transactionAspect);
        assertNotNull(transactionAspect.transactionManager);
    }

    public void testStartAspectifiedComponent() throws PicoRegistrationException, PicoInstantiationException, PicoIntrospectionException {
        container.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        container.registerServiceOrAspect(TransactionAspect.class);
        container.registerComponent(Component.class, SucceedingComponent.class);
        container.instantiateComponents();

        Component component = (Component) container.getComponent(Component.class);
        assertNotNull(component);
        assertTrue(Aspects.isAspectObject(component));
        assertEquals(1, Aspects.getAspectInstance(component).getAllInterceptors().size());
    }

    /**
     * The 'acceptance-test' for the example.
     */
    public void testTransactionAspectStartsAndCommitsTransaction() throws PicoRegistrationException, PicoInstantiationException, Exception {
        container.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        container.registerServiceOrAspect(TransactionAspect.class);
        container.registerComponent(Component.class, SucceedingComponent.class);
        container.instantiateComponents();

        Component component = (Component) container.getComponent(Component.class);
        component.doSomethingRequiringATransaction();

        LoggingTransactionManager transactionManager = (LoggingTransactionManager) container.getComponent(TransactionManager.class);
        assertEquals("instantiateComponents commit ", transactionManager.transactionLog.toString());
    }

    public void testTransactionAspectStartsAndRollsBackTransaction() throws PicoRegistrationException, PicoInstantiationException, Exception {
        container.registerServiceOrAspect(TransactionManager.class, LoggingTransactionManager.class);
        container.registerServiceOrAspect(TransactionAspect.class);
        container.registerComponent(Component.class, FailingComponent.class);
        container.instantiateComponents();

        Component component = (Component) container.getComponent(Component.class);
        try {
            component.doSomethingRequiringATransaction();
            fail();
        } catch (Exception shouldHappen) {
        }

        LoggingTransactionManager transactionManager = (LoggingTransactionManager) container.getComponent(TransactionManager.class);
        assertEquals("instantiateComponents rollback ", transactionManager.transactionLog.toString());
    }

}
