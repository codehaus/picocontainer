package org.nanocontainer.nanowar.server;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;


public class DependencyInjectionTestServlet extends HttpServlet {
    private final String name;
    public DependencyInjectionTestServlet(String name) {
        this.name = name;
    }
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        String message = name;
        if (request.getAttribute("foo2") != null) {
            message = message + request.getAttribute("foo2");
        }
        response.getWriter().write("hello " + message);
    }
}