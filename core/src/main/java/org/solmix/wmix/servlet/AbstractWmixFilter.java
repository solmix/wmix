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

package org.solmix.wmix.servlet;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月30日
 */

public abstract class AbstractWmixFilter implements Filter {

    protected final Logger LOG = LoggerFactory.getLogger(getClass());

    private FilterConfig filterConfig;

    protected final Set<String> requiredProperties = new HashSet<String>();

    protected final void addRequiredProperty(String name) {
        this.requiredProperties.add(name);
    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,  FilterChain chain) throws IOException, ServletException {
        if (request instanceof HttpServletRequest && response instanceof HttpServletResponse) {
            HttpServletRequest httpRequest = (HttpServletRequest) request;
            HttpServletResponse httpResponse = (HttpServletResponse) response;

            String method = httpRequest.getMethod();

            if ("HEAD".equalsIgnoreCase(method)) {
                httpResponse = new NoBodyResponse(httpResponse);
            }

            try {
                doFilter(httpRequest, httpResponse, chain);
            } finally {
                if (httpResponse instanceof NoBodyResponse) {
                    ((NoBodyResponse) httpResponse).setContentLength();
                }
            }
        } else {
            LOG.debug("Skipped filtering due to the unknown request/response types: {}, {}", request.getClass()
                                                                                                    .getName(), response.getClass().getName());

            chain.doFilter(request, response);
        }

    }

    protected abstract void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException;
    @Override
    public void init(FilterConfig config) throws ServletException {
        filterConfig = config;
        logInBothServletAndLoggingSystem("Initializing filter: "   + getFilterName());
        initContext(config);
        try {
            init();
        } catch (Exception e) {
            throw new ServletException("Failed to init filter: "
                + getFilterName(), e);
        }

        logInBothServletAndLoggingSystem(getClass().getSimpleName() + " - "
            + getFilterName() + ": initialization completed");
    }

  
    protected void initContext(FilterConfig config) throws ServletException {
        
    }

    public final FilterConfig getFilterConfig() {
        return filterConfig;
    }

    public final String getFilterName() {
        return filterConfig == null ? null : filterConfig.getFilterName();
    }

    /** 取得当前webapp的context。 */
    public final ServletContext getServletContext() {
        return filterConfig == null ? null : filterConfig.getServletContext();
    }

    protected void init() throws Exception {
    }

    protected final void logInBothServletAndLoggingSystem(String msg) {
        getServletContext().log(msg);
        LOG.info(msg);
    }

  
    private static class NoBodyResponse extends HttpServletResponseWrapper {
        private final NoBodyOutputStream noBody = new NoBodyOutputStream();
        private PrintWriter writer;
        private boolean     didSetContentLength;

        public NoBodyResponse(HttpServletResponse response) {
            super(response);
        }

        public void setContentLength() {
            if (!didSetContentLength) {
                super.setContentLength(noBody.getContentLength());
            }
        }

        @Override
        public void setContentLength(int len) {
            super.setContentLength(len);
            didSetContentLength = true;
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            return noBody;
        }

        @Override
        public PrintWriter getWriter() throws UnsupportedEncodingException {
            if (writer == null) {
                writer = new PrintWriter(new OutputStreamWriter(noBody, getCharacterEncoding()));
            }

            return writer;
        }
    }

    private static class NoBodyOutputStream extends ServletOutputStream {
        private int contentLength;

        public NoBodyOutputStream() {
            contentLength = 0;
        }

        public int getContentLength() {
            return contentLength;
        }

        @Override
        public void write(int b) {
            contentLength++;
        }

        @Override
        public void write(byte[] buf, int offset, int len) throws IOException {
            if (len >= 0) {
                contentLength += len;
            } else {
                throw new IOException("negative length");
            }
        }
    }
}
