package org.nanocontainer.nanowar.server;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class DependencyInjectionTestFilter implements Filter {

    private String name;

    public DependencyInjectionTestFilter(String message) {
        this.name = message;
    }

    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        String servletPath = req.getServletPath();
        if (servletPath.equals("foo2")) {
            request.setAttribute("foo2", name);

        }
        chain.doFilter(request, response);
    }

    public void init(FilterConfig filterConfig) throws ServletException {
    }

    public void destroy() {
    }
}

