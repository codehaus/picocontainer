package picocontainer.defaults;

import java.util.List;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public interface AggregateProxyFactory {
    Object createAggregateProxy(
            ClassLoader classLoader,
            List objectsToAggregateCallFor,
            boolean callInReverseOrder
    );
}
