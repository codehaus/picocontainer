/*****************************************************************************
 * Copyright (C) NanoContainer Organization. All rights reserved.            *
 * ------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the BSD      *
 * style license a copy of which has been included with this distribution in *
 * the LICENSE.txt file.                                                     *
 *                                                                           *
 *****************************************************************************/
package org.nanocontainer.nanowar;

import com.meterware.httpunit.GetMethodWebRequest;
import com.meterware.httpunit.WebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;
import junit.framework.TestCase;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * 
 * @author Gr�gory Joseph
 */
public class AbstractServletTestCase extends TestCase {
    private ServletRunner sr;

    public void testTheTest() throws Exception {
        FilterDef filterDef = new FilterDef("test", DummyFilter.class, null, null, null, false);
        initTest("", "", "", filterDef);
        String res = doTest();
        assertEquals("<-empty->", res);
    }

    protected void initTest(String ctxScope, String sesScope, String reqScope, FilterDef[] filters) throws Exception {
        InputStream webXml = getWebXml(filters, ctxScope, sesScope, reqScope);
        sr = new ServletRunner(webXml);
        sr.registerServlet("empty", EmptyServlet.class.getName());
    }

    protected void initTest(String ctxScope, String sesScope, String reqScope, FilterDef filterDef) throws Exception {
        initTest(ctxScope, sesScope, reqScope, new FilterDef[]{filterDef});
    }

    protected String doTest() throws Exception {
        ServletUnitClient sc = sr.newClient();
        WebRequest request = new GetMethodWebRequest("http://incongru.net/empty");
        WebResponse response = sc.getResponse(request);
        return response.getText();
    }

    private InputStream getWebXml(FilterDef[] filters, String ctxScope, String sesScope, String reqScope) {
        StringBuffer sb = new StringBuffer();
        sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        sb.append("\n");
        sb.append("<web-app version=\"2.4\"\n");
        sb.append("    xmlns=\"http://java.sun.com/xml/ns/j2ee\"\n");
        sb.append("    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
        sb.append("    xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\">\n");
        sb.append("\n");
        sb.append("<context-param>\n" +
                "  <param-name>nanocontainer.groovy</param-name>\n" +
                "  <param-value>" + //<![CDATA[\n" +
                "    caf = new org.picocontainer.defaults.DefaultComponentAdapterFactory()\n" +
                "    pico = new org.picocontainer.defaults.DefaultPicoContainer(caf, parent)\n" +
                "    if(assemblyScope instanceof javax.servlet.ServletContext) {\n" +
                ctxScope +
                "    } else if(assemblyScope instanceof javax.servlet.http.HttpSession) {\n" +
                sesScope +
                "    } else if(assemblyScope instanceof javax.servlet.ServletRequest) {\n" +
                reqScope +
                "    }\n" +
                //"  ]]>" +
                "</param-value>\n" +
                "</context-param>" +
                "    <listener>\n" +
                "        <listener-class>org.nanocontainer.nanowar.ServletContainerListener</listener-class>\n" +
                "    </listener>\n" +
                "\n" +
                "    <filter>\n" +
                "        <filter-name>NanoWar</filter-name>\n" +
                "        <filter-class>org.nanocontainer.nanowar.ServletRequestContainerFilter</filter-class>\n" +
                "    </filter>" +
                "    <filter-mapping>\n" +
                "        <filter-name>NanoWar</filter-name>\n" +
                "        <url-pattern>/*</url-pattern>\n" +
                "    </filter-mapping>");
        for (int i = 0; i < filters.length; i++) {
            sb.append(filters[i].toString());
        }
        sb.append("</web-app>");
        return new ByteArrayInputStream(sb.toString().getBytes());
    }

    static class FilterDef {
        private String filterName;
        private Class filterClass;
        private Class delegateClass;
        private String delegateKey;
        private String initType;
        private boolean lookupOnlyOnce;

        public FilterDef(String filterName, Class filterClass, Class delegateClass, String delegateKey, String initType, boolean lookupOnlyOnce) {
            this.filterName = filterName;
            this.filterClass = filterClass;
            this.delegateClass = delegateClass;
            this.delegateKey = delegateKey;
            this.initType = initType;
            this.lookupOnlyOnce = lookupOnlyOnce;
        }

        public String toString() {
            return "    <filter>\n" +
                    "         <filter-name>" + filterName + "</filter-name>\n" +
                    "         <filter-class>" + filterClass.getName() + "</filter-class>\n" +
                    (delegateClass != null ?
                    "         <init-param>\n" +
                    "             <param-name>delegate-class</param-name>\n" +
                    "             <param-value>" + delegateClass.getName() + "</param-value>\n" +
                    "         </init-param>\n"
                    : "") +
                    (delegateKey != null ?
                    "         <init-param>\n" +
                    "             <param-name>delegate-key</param-name>\n" +
                    "             <param-value>" + delegateKey + "</param-value>\n" +
                    "         </init-param>\n"
                    : "") +
                    (initType != null ?
                    "         <init-param>\n" +
                    "             <param-name>init-type</param-name>\n" +
                    "             <param-value>" + initType + "</param-value>\n" +
                    "         </init-param>\n"
                    : "") +
                    (lookupOnlyOnce ?
                    "         <init-param>\n" +
                    "             <param-name>lookup-only-once</param-name>\n" +
                    "             <param-value>true</param-value>\n" +
                    "         </init-param>\n"
                    : "") +
                    "     </filter>\n" +
                    "\n" +
                    "    <filter-mapping>\n" +
                    "        <filter-name>" + filterName + "</filter-name>\n" +
                    "        <url-pattern>/*</url-pattern>\n" +
                    "    </filter-mapping>\n" +
                    "\n";
        }
    }

    public static class EmptyServlet extends HttpServlet {
        protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
            res.getWriter().write("-empty-");
        }
    }

    public static class DummyFilter implements Filter {
        public void destroy() {
        }

        public void doFilter(ServletRequest req, ServletResponse res, FilterChain filterChain) throws IOException, ServletException {
            res.getWriter().print('<');
            filterChain.doFilter(req, res);
            res.getWriter().print('>');

        }

        public void init(FilterConfig filterConfig) throws ServletException {
        }
    }
}
