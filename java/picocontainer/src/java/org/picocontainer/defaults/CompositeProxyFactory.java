package org.picocontainer.defaults;

import java.util.List;

/**
 * 
 * @author Aslak Helles&oslash;y
 * @version $Revision: 1.1 $
 */
public interface CompositeProxyFactory {
    Object createCompositeProxy(
            ClassLoader classLoader,
            List objectsToAggregateCallFor,
            boolean callInReverseOrder
    );
}
