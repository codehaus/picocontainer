package org.nanocontainer.webwork;

import com.opensymphony.xwork.ActionInvocation;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionProxyFactory;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chris
 * Date: 23.09.2003
 * Time: 16:54:48
 * @author csturm
 */
public class PicoActionProxyFactory extends DefaultActionProxyFactory {
    public ActionInvocation createActionInvocation(ActionProxy actionProxy) throws Exception {
        return new PicoActionInvocation(actionProxy);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext) throws Exception {
        return new PicoActionInvocation(actionProxy, extraContext);
    }

    public ActionInvocation createActionInvocation(ActionProxy actionProxy, Map extraContext, boolean pushAction) throws Exception {
        return new PicoActionInvocation(actionProxy, extraContext, pushAction);
    }
}
