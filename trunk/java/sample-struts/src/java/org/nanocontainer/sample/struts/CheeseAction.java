/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.sample.struts;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.nanocontainer.sample.struts.dao.CheeseDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * @author Stephen Molitor
 */
// TODO this should be in src/test ?
public class CheeseAction extends Action {

    private final CheeseDao dao;

    public CheeseAction(CheeseDao dao) {
        this.dao = dao;
    }

    public ActionForward execute(ActionMapping mapping,
                                 ActionForm form,
                                 HttpServletRequest request,
                                 HttpServletResponse response) throws Exception {
        CheeseForm cheeseForm = (CheeseForm) form;

        if (!isEmpty(cheeseForm.getName())) {
            Cheese cheese = new Cheese();
            cheese.setName(cheeseForm.getName());
            cheese.setCountry(cheeseForm.getCountry());
            dao.save(cheese);
        }

        Collection cheeses = dao.all();
        request.setAttribute("cheesesOfTheWord", cheeses);

        return mapping.findForward("next");
    }

    private boolean isEmpty(String s) {
        return s == null || "".equals(s.trim());
    }

}
