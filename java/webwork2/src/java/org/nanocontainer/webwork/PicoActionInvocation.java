package org.nanocontainer.webwork;

import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import org.nanocontainer.servlet.ObjectHolder;
import org.nanocontainer.servlet.ObjectInstantiator;
import org.nanocontainer.servlet.holder.RequestScopeObjectHolder;
import org.nanocontainer.servlet.lifecycle.BaseLifecycleListener;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 23.09.2003
 * Time: 16:56:37
 * @author csturm
 */
public class PicoActionInvocation extends DefaultActionInvocation {

    public PicoActionInvocation(ActionProxy proxy) throws Exception {
        super(proxy);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext) throws Exception {
        super(proxy, extraContext);
    }

    public PicoActionInvocation(ActionProxy proxy, Map extraContext, boolean pushAction) throws Exception {
        super(proxy, extraContext, pushAction);
    }

    protected void createAction() {
        // load action
        try {
            final Class actionClass = proxy.getConfig().getClazz();
//            action = (Action) actionClass.newInstance();
            ObjectInstantiator instantiater = findInstantiater();

            //TODO instantiater = null (wieso?)  (wird request filter nicht aufgerufen?)
            action = (Action) instantiater.newInstance(actionClass);
        } catch (Exception e) {
            throw new IllegalArgumentException("Unknown action name: " + e.getMessage());
        }
    }

    private ObjectInstantiator findInstantiater() {
//        HttpServletRequest req = request;
        HttpServletRequest req;
        req = (HttpServletRequest) extraContext.get(WebWorkStatics.HTTP_REQUEST);
        ObjectHolder instantiaterHolder = new RequestScopeObjectHolder(req, BaseLifecycleListener.INSTANTIATER_KEY);
        return (ObjectInstantiator) instantiaterHolder.get();
    }
}
