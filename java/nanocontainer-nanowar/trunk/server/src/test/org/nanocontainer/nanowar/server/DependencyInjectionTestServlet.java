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
    protected void doGet(HttpServletRequest httpServletRequest, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.getWriter().write("hello " + name);
    }
}