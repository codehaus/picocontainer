package org.nanocontainer.nanowar.nanoweb;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotFoundServlet extends HttpServlet {

	private static final long serialVersionUID = 3257288019681031220L;

	protected void service(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
        res.sendError(HttpServletResponse.SC_NOT_FOUND);
        return;
    }

}
