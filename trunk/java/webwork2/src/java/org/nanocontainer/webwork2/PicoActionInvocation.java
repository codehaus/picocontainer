/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.webwork2;

import com.opensymphony.webwork.WebWorkStatics;
import com.opensymphony.xwork.Action;
import com.opensymphony.xwork.ActionProxy;
import com.opensymphony.xwork.DefaultActionInvocation;
import org.nanocontainer.servlet.KeyConstants;
import org.nanocontainer.servlet.RequestScopeObjectReference;
import org.picocontainer.MutablePicoContainer;
import org.picocontainer.PicoContainer;
import org.picocontainer.defaults.DefaultPicoContainer;
import org.picocontainer.defaults.ObjectReference;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Chris Sturm
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class PicoActionInvocation extends DefaultActionInvocation implements KeyConstants {

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
        Class actionClass = proxy.getConfig().getClazz();

        PicoContainer requestContainer = getRequestContainer();
        action = (Action) requestContainer.getComponentInstance(actionClass);
        if (action == null) {
            // The action wasn't registered. Do it ad-hoc here.
            MutablePicoContainer tempContainer = new DefaultPicoContainer(requestContainer);
            tempContainer.registerComponentImplementation(actionClass);
            action = (Action) tempContainer.getComponentInstance(actionClass);
        }
    }

    private PicoContainer getRequestContainer() {
        HttpServletRequest request = (HttpServletRequest) getStack().getContext().get(WebWorkStatics.HTTP_REQUEST);
        ObjectReference ref = new RequestScopeObjectReference(request, REQUEST_CONTAINER);
        return (PicoContainer) ref.get();
    }
}
