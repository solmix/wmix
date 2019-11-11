/*
 * Copyright 2015 The Solmix Project
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

package org.solmix.rest;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Assert;
import org.solmix.commons.util.ServletUtils;
import org.solmix.runtime.Container;
import org.solmix.test.TestUtils;
import org.solmix.wmix.Component;
import org.solmix.wmix.Controller;
import org.solmix.wmix.servlet.WmixFilter;

import com.meterware.httpunit.PostMethodWebRequest;
import com.meterware.httpunit.WebResponse;
import com.meterware.httpunit.protocol.UploadFileSpec;
import com.meterware.servletunit.InvocationContext;
import com.meterware.servletunit.PatchedServletRunner;
import com.meterware.servletunit.ServletRunner;
import com.meterware.servletunit.ServletUnitClient;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年8月14日
 */

public abstract class AbstractWmixTests
{

    protected static ServletUnitClient client;

    protected static Component component;

    protected static Controller controller;

    protected static Container container;
    protected InvocationContext   invocationContext;
    protected HttpServletRequest  request;
    protected HttpServletResponse response;

    private WmixFilter filter;

    private ServletContext servletContext;

    /**
     * 启动servlet，并准备app1组件
     * @throws Exception
     */
    protected static void prepareServlet() throws Exception {
        prepareServlet("app1");
    }

    /**
     * 启动servlet，并准备组件
     * @param app
     * @throws Exception
     */
    protected static void prepareServlet(String app) throws Exception {
        // Servlet container
        File xml = new File(TestUtils.srcdir(), "WEB-INF/web.xml");
        ServletRunner servletRunner = new PatchedServletRunner(xml, "");

        // Servlet client
        client = servletRunner.newClient();
        client.setExceptionsThrownOnErrorStatus(false);
        client.getClientProperties().setAutoRedirect(false);

        // Filter
        WmixFilter filter = (WmixFilter) client.newInvocation("http://127.0.0.1/" + app).getFilter();

        //  Controller
        component = filter.getComponents().getComponent(app);

        controller = component.getController();
        Assert.assertNotNull(controller);

        // ApplicationContext
        container = component.getContainer();
        Assert.assertNotNull(container);
    }
    protected final void invoke(String uri)throws Exception{
        if (uri != null && uri.startsWith("http")) {
            uri = URI.create(uri).normalize().toString(); 
        }else{
            uri= URI.create("http://127.0.0.1/"+uri).normalize().toString(); 
        }

        invocationContext= client.newInvocation(uri);
        request = invocationContext.getRequest();
        response = invocationContext.getResponse();
        filter=(WmixFilter)invocationContext.getFilter();
        servletContext =filter.getServletContext();
    }
    protected final void invokePost(String uri,String content,String contentType)throws Exception{
        if (uri != null && uri.startsWith("http")) {
            uri = URI.create(uri).normalize().toString(); // full uri
        }else{
            uri= URI.create("http://127.0.0.1/"+uri).normalize().toString(); 
        }
        InputStream   in_nocode   =   new   ByteArrayInputStream(content.getBytes());   
        PostMethodWebRequest post = new PostMethodWebRequest(uri,in_nocode, contentType);
        invocationContext = client.newInvocation(post);
        request = invocationContext.getRequest();
        response = invocationContext.getResponse();
        filter=(WmixFilter)invocationContext.getFilter();
        servletContext =filter.getServletContext();
    }
    protected final void invokePost(String uri,String content)throws Exception{
    	invokePost(uri,content,"text/json;charset=utf-8");
    }
    
    protected final InvocationContext invokePostContext(String uri,String content)throws Exception{
        if (uri != null && uri.startsWith("http")) {
            uri = URI.create(uri).normalize().toString(); // full uri
        }else{
            uri= URI.create("http://127.0.0.1/"+uri).normalize().toString(); 
        }
        InputStream   in_nocode   =   new   ByteArrayInputStream(content.getBytes());   
        PostMethodWebRequest post = new PostMethodWebRequest(uri,in_nocode, "text/json;charset=utf-8");
        return client.newInvocation(post);
    }
    
    
    protected final void invokePost(String uri,Object... params)throws Exception{
        if (uri != null && uri.startsWith("http")) {
            uri = URI.create(uri).normalize().toString(); // full uri
        }else{
            uri= URI.create("http://127.0.0.1/"+uri).normalize().toString(); 
        }
        PostMethodWebRequest post = new PostMethodWebRequest(uri, true);
        Assert.assertTrue(params.length % 2 == 0);
        for (int i = 0; i < params.length; i += 2) {
            String key = (String) params[i];
            Object value = params[i + 1];

            if (value instanceof File) {
                post.selectFile(key, (File) value);
            } else if (value instanceof File[]) {
                UploadFileSpec[] specs = new UploadFileSpec[((File[]) value).length];

                for (int j = 0; j < ((File[]) value).length; j++) {
                    specs[j] = new UploadFileSpec(((File[]) value)[j]);
                }

                post.setParameter(key, specs);
            } else {
                post.setParameter(key, (String) value);
            }
        }

        invocationContext = client.newInvocation(post);
        request = invocationContext.getRequest();
        response = invocationContext.getResponse();
        filter=(WmixFilter)invocationContext.getFilter();
        servletContext =filter.getServletContext();
    }
    
    protected final WebResponse getResponse() throws Exception {
        return client.getResponse(invocationContext);
    }

    @After
    public void clearClient() {
        if (client != null) {
            client.clearContents();
        }
    }

    public static class ResourceServlet extends HttpServlet
    {

        private static final long serialVersionUID = -5288195741719029071L;

        @Override
        protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String path = ServletUtils.getResourcePath(req);
            URL resource = getServletContext().getResource(path);
            URLConnection conn = resource.openConnection();

            resp.setContentType(conn.getContentType());
            InputStream in = conn.getInputStream();
            IOUtils.copy(in, resp.getOutputStream());
            in.close();
        }
    }
}
