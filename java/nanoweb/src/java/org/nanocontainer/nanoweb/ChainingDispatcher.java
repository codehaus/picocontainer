package org.nanocontainer.nanoweb;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ChainingDispatcher implements Dispatcher {
    public void dispatch(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String actionPath, String result) {
        String[] views = getViews(actionPath, result, ".vm");

        boolean didDispatch = false;
        int counter = 0;
        while(!didDispatch) {
            String view = views[counter];
            RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher(view);
            didDispatch = dispatch(httpServletRequest, requestDispatcher, httpServletResponse);
            counter++;
        }
    }

    private boolean dispatch(HttpServletRequest httpServletRequest, RequestDispatcher requestDispatcher, HttpServletResponse httpServletResponse) {
        try {
            if (httpServletRequest.getAttribute("javax.servlet.include.servlet_path") == null) {
                requestDispatcher.forward(httpServletRequest, httpServletResponse);
            } else {
                requestDispatcher.include(httpServletRequest, httpServletResponse);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    String[] getViews(String actionPath, String result, String extension) {
        String[] views = new String[3];

        views[0] = getActionNameUnderscoreResultView(actionPath, result, extension);
        views[1] = getActionFolderPathResultView(actionPath, result, extension);
        views[2] = getActionRootResultView(result, extension);

        return views;
    }

    private String getActionNameUnderscoreResultView(String actionPath, String result, String extension) {
        String actionName = actionPath.substring(0, actionPath.indexOf("."));
        String view = actionName + "_" + result + extension;
        return view;
    }

    private String getActionFolderPathResultView(String actionPath, String result, String extension) {
        String actionFolderPath = actionPath.substring(0, actionPath.lastIndexOf("/") + 1);
        String view = actionFolderPath + result + extension;
        return view;
    }

    private String getActionRootResultView(String result, String extension) {
        return "/" + result + extension;
    }

}