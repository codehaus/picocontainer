package nanocontainer;

import picocontainer.PicoStartException;
import picocontainer.Container;
import picocontainer.PicoStopException;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.Arrays;


public class AggregatingNanoContainer implements NanoContainer {

    private final Container containerToAggregateComponentsFor;
    private final InvocationHandler invocationHandler;
    /** Cached */
    private Object proxy;

    public AggregatingNanoContainer(Container containerToAggregateComponentsFor,
                                    InvocationHandler invocationHandler) {
        this.containerToAggregateComponentsFor = containerToAggregateComponentsFor;
        this.invocationHandler = invocationHandler;
    }

    public void start() throws PicoStartException {
    }

    public void stop() throws PicoStopException {
    }

    public boolean hasComponent(Class aClass) {
        return true;
    }

    public Object getComponent(Class aClass) {
        return proxy;
    }

    public Object[] getComponents() {
        return new Object[]{proxy};
    }

    public Object getProxy() {

        if (proxy == null) {

            // TODO should fail if container isn't started. Throw checked exception?
            Set interfaces = new HashSet();
            Object[] comps = containerToAggregateComponentsFor.getComponents();
            for (int i = 0; i < comps.length; i++) {
                Class componentClass = comps[i].getClass();
                Class[] implemeted = componentClass.getInterfaces();
                List implementedList = Arrays.asList(implemeted);
                interfaces.addAll(implementedList);
            }

            Class[] interfaceArray = (Class[]) interfaces.toArray(new Class[interfaces.size()]);
            proxy = Proxy.newProxyInstance(getClass().getClassLoader(), interfaceArray, invocationHandler);
            return proxy;

        } else {
            return proxy;
        }

    }


}
