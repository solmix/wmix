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
package org.solmix.wmix.web;

import java.io.File;
import java.net.URI;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.solmix.commons.util.StringUtils;
import org.solmix.wmix.test.TestEnvStatic;

import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.javascript.JavaScript;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.PatchedServletRunner;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年1月30日
 */

public abstract class AbstractTests {
    protected ServletUnitClient client;
    protected WebResponse       clientResponse;
    protected int               clientResponseCode;
    protected String            clientResponseContent;
    protected final void prepareWebClient(String webXmlName, String contextPath) throws Exception {
        // Servlet container
        File webInf = new File(TestEnvStatic.srcdir, "WEB-INF");
        File webXml = new File(webInf, StringUtils.defaultIfEmpty(webXmlName, "web.xml"));

        ServletRunner servletRunner = new PatchedServletRunner(webXml, contextPath);

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
        } 

        InvocationContext ic = client.newInvocation(uri);
        ic.getFilter().doFilter(new MyHttpRequest(ic.getRequest(), uri), ic.getResponse(), ic.getFilterChain());

        clientResponse = client.getResponse(ic);
        clientResponseCode = clientResponse.getResponseCode();
        clientResponseContent = clientResponse.getText();
    }
    public static class MyHttpRequest extends HttpServletRequestWrapper {
        private String overrideQueryString;

        public MyHttpRequest(HttpServletRequest request, String uri) {
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
}
