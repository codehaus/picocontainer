package org.picocontainer.behaviors;

import org.picocontainer.behaviors.ImplementationHidingBehaviorFactory;
import org.picocontainer.behaviors.CachingBehaviorFactory;
import org.picocontainer.behaviors.SynchronizedBehaviorFactory;
import org.picocontainer.BehaviorFactory;

public class Behaviors {

    public static BehaviorFactory implHiding() {
        return new ImplementationHidingBehaviorFactory();
    }

    public static BehaviorFactory caching() {
        return new CachingBehaviorFactory();
    }

    public static BehaviorFactory threadSafe() {
        return new SynchronizedBehaviorFactory();
    }

}
