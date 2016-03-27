/**
 * Copyright (c) 2015 The Solmix Project
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.gnu.org/licenses/ 
 * or see the FSF site: http://www.fsf.org. 
 */

package org.solmix.wmix;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.solmix.commons.util.ServletUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.wmix.test.TestUtils;

import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.javascript.JavaScript;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.PatchedServletRunner;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月30日
 */

public abstract class AbstractTests
{

    protected ServletUnitClient client;

    protected WebResponse clientResponse;

    protected int clientResponseCode;

    protected String clientResponseContent;

    protected final void prepareWebClient(String xmlName) throws Exception {
        prepareWebClient(xmlName, "");
    }

    protected final void prepareWebClient(String xmlName, String contextPath) throws Exception {
        // Servlet container
        File webInf = new File(TestUtils.srcdir, "WEB-INF");
        File xml = new File(webInf, StringUtils.defaultIfEmpty(xmlName, "web.xml"));

        ServletRunner servletRunner = new PatchedServletRunner(xml, contextPath);

        // Servlet client
        client = servletRunner.newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        client.getClientProperties().setAutoRedirect(false);

        // Ignore script error
        JavaScript.setThrowExceptionsOnError(false);
    }

    protected final void invokeServlet(String uri) throws Exception {
        if (uri != null && uri.startsWith("http")) {
            uri = URI.create(uri).normalize().toString(); // full uri
        }else{
            uri= URI.create("http://127.0.0.1/"+uri).normalize().toString(); 
        }

        InvocationContext ic = client.newInvocation(uri);
        ic.getFilter().doFilter(new MyHttpRequest(ic.getRequest(), uri), ic.getResponse(), ic.getFilterChain());

        clientResponse = client.getResponse(ic);
        clientResponseCode = clientResponse.getResponseCode();
        clientResponseContent = clientResponse.getText();
    }

    public static class MyHttpRequest extends HttpServletRequestWrapper
    {

        private String overrideQueryString;

        public MyHttpRequest(HttpServletRequest request, String uri)
        {
            super(request);

            if (uri != null) {
                int index = uri.indexOf("?");

                if (index >= 0) {
                    this.overrideQueryString = uri.substring(index + 1);
                }
            }
        }

        @Override
        public String getQueryString() {
            if (overrideQueryString == null) {
                return super.getQueryString();
            } else {
                return overrideQueryString;
            }
        }
    }

    public static class JavaScriptFilter implements Filter
    {

        @Override
        public void init(FilterConfig filterConfig) throws ServletException {
        }

        @Override
        public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
            if (!((HttpServletRequest) request).getRequestURI().endsWith("scriptaculous.js")) {
                chain.doFilter(request, response);
            }
        }

        @Override
        public void destroy() {
        }
    }

    public static class ResourceServlet extends HttpServlet
    {

        private static final long serialVersionUID = -5288195741719029071L;

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String path = ServletUtils.getResourcePath(req);

            if ("".equals(path) || "/".equals(path)) {
                resp.setContentType("text/plain");
                PrintWriter out = resp.getWriter();

                out.print("Homepage");
                out.flush();
            } else {
                URL resource = getServletContext().getResource(path);
                URLConnection conn = resource.openConnection();

                resp.setContentType(conn.getContentType());
                InputStream in = conn.getInputStream();
                IOUtils.copy(in, resp.getOutputStream());
                in.close();
            }
        }
    }
}
