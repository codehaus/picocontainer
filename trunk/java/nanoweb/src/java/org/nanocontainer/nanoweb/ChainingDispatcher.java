package org.nanocontainer.nanoweb;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

/**
 * @author Aslak Helles&oslash;y
 * @version $Revision$
 */
public class ChainingDispatcher implements Dispatcher {
    public void dispatch(ServletContext servletContext, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, String scriptPathWithoutExtension, String actionMethod, String result) throws IOException, ServletException {
        String[] views = getViews(scriptPathWithoutExtension, actionMethod, result, ".vm");

        boolean didDispatch = false;
        for (int i = 0; i < views.length; i++) {
            String view = views[i];
            URL viewURL = servletContext.getResource(view);
            if(viewURL != null) {
                RequestDispatcher requestDispatcher = httpServletRequest.getRequestDispatcher(view);
                if (httpServletRequest.getAttribute("javax.servlet.include.servlet_path") == null) {
                    requestDispatcher.forward(httpServletRequest, httpServletResponse);
                } else {
                    requestDispatcher.include(httpServletRequest, httpServletResponse);
                }
                didDispatch = true;
                break;
            }
        }
        if (!didDispatch) {
            throw new ServletException("Couldn't dispatch to any of " + Arrays.asList(views).toString());
        }
    }

    String[] getViews(String scriptPathWithoutExtension, String actionMethod, String result, String extension) {
        String[] views = new String[4];

        views[0] = getScriptPathUnderscoreActionNameUnderscoreResultView(scriptPathWithoutExtension, actionMethod, result, extension);
        views[1] = getScriptPathUnderscoreResultView(scriptPathWithoutExtension, result, extension);
        views[2] = getActionFolderPathResultView(scriptPathWithoutExtension, result, extension);
        views[3] = getActionRootResultView(result, extension);

        return views;
    }

    private String getScriptPathUnderscoreResultView(String scriptPathWithoutExtension, String result, String extension) {
        return scriptPathWithoutExtension + "_" + result + extension;
    }

    private String getScriptPathUnderscoreActionNameUnderscoreResultView(String scriptPathWithoutExtension, String actionMethod, String result, String extension) {
        return scriptPathWithoutExtension + "_" + actionMethod + "_" + result + extension;
    }

    private String getActionFolderPathResultView(String scriptPathWithoutExtension, String result, String extension) {
        String actionFolderPath = scriptPathWithoutExtension.substring(0, scriptPathWithoutExtension.lastIndexOf("/") + 1);
        String view = actionFolderPath + result + extension;
        return view;
    }

    private String getActionRootResultView(String result, String extension) {
        return "/" + result + extension;
    }

}