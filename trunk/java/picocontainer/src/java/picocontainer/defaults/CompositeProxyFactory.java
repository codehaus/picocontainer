package picocontainer.defaults;

import java.util.List;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface CompositeProxyFactory {
    Object createCompositeProxy(
            ClassLoader classLoader,
            List objectsToAggregateCallFor,
            boolean callInReverseOrder
    );
}
