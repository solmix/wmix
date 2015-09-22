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

package org.solmix.wmix.support;

import static org.solmix.wmix.exchange.WmixMessage.HTTP_REQUEST;
import static org.solmix.wmix.exchange.WmixMessage.HTTP_REQUEST_METHOD;
import static org.solmix.wmix.exchange.WmixMessage.HTTP_RESPONSE;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ServletUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.support.AbstractServiceFactory;
import org.solmix.exchange.support.DefaultExchange;
import org.solmix.runtime.Container;
import org.solmix.runtime.helper.HttpHeaderHelper;
import org.solmix.runtime.io.DelegatingInputStream;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.wmix.Component;
import org.solmix.wmix.ComponentConfig;
import org.solmix.wmix.Controller;
import org.solmix.wmix.WmixEndpoint;
import org.solmix.wmix.exchange.WmixMessage;
import org.solmix.wmix.exchange.WmixServiceFactory;
import org.solmix.wmix.parser.CookieParser;
import org.solmix.wmix.parser.ParameterParser;
import org.solmix.wmix.parser.ParameterParserFilter;
import org.solmix.wmix.parser.support.DefaultCookieParser;
import org.solmix.wmix.parser.support.DefaultParameterParser;
import org.solmix.wmix.upload.UploadService;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月21日
 */

public class WmixDefaultController implements Controller
{


    private static final Logger LOG = LoggerFactory.getLogger(WmixDefaultController.class);
   

    private Component component;

    private Container container;
    private List<WmixEndpoint> endpoints;
    private UploadService upload;
    
    private  ParameterParserFilter[] filters;
    
    private boolean                 trimming;
    
    private String                  htmlFieldSuffix;
    @Override
    public void init(Component component) {
        this.component = component;
        this.container=component.getContainer();
        initResourceRelove();
        instanceEndpoints(component.getEndpoints());
        upload=container.getExtension(UploadService.class);
    }
    /**
     * @param endpoints
     */
    protected void instanceEndpoints(List<WmixEndpoint> endpoints) {
        if(endpoints!=null){
            for(WmixEndpoint wep:endpoints){
                wep.init(this.component);
            }
        }
        this.endpoints=endpoints;
    }
    
    protected AbstractServiceFactory initServiceFactory(){
        return new WmixServiceFactory(component.getComponentPath());
    }


    private void initResourceRelove() {
        ServletContext sc = this.container.getExtension(ServletContext.class);
        ResourceManager rm = this.container.getExtension(ResourceManager.class);
        // 注册ServletContext resource resolver.
//        rm.addResourceResolver(new ServletContextResourceResolver(sc));
    }

    public ComponentConfig getComponentConfig() {
        return getComponent().getComponentConfig();
    }

    @Override
    public boolean service(HttpServletRequest request, HttpServletResponse response) throws Exception {

        WmixMessage message = setupMessage(request,response);
        for(WmixEndpoint ep:endpoints){
            if(ep.getCondition().accept(message)){
                ep.service(message);
                return true;
            }
        }
        return false;
    }

    private WmixMessage setupMessage(HttpServletRequest request, HttpServletResponse response)throws IOException {
        
       
        WmixMessage msg = new WmixMessage();
        Exchange ex = new DefaultExchange();
        setupExchange(ex,request,response);
        ex.setIn(msg);
        DelegatingInputStream in= new DelegatingInputStream(request.getInputStream());
        msg.setContent(DelegatingInputStream.class, in);
        msg.setContent(InputStream.class, in);
        msg.put(HTTP_REQUEST, request);
        msg.put(HTTP_RESPONSE, response);
        msg.put(HTTP_REQUEST_METHOD, request.getMethod());
        String reqUri=request.getRequestURI();
        msg.put(Message.REQUEST_URI, reqUri);
        String reqUrl=request.getRequestURL().toString();
        msg.put(Message.REQUEST_URL, reqUrl);
       
        msg.put(Message.PATH_INFO,ServletUtils.getResourcePath(request) );
        
        String basePath = ServletUtils.getBaseURL(request);
        if (!StringUtils.isEmpty(basePath)) {
            msg.put(Message.BASE_PATH, basePath);
        }
        
        String contentType = request.getContentType();
        msg.put(Message.CONTENT_TYPE, contentType);
       
        setEncoding(msg,request,contentType);
        
        ex.put(Message.CONTENT_TYPE, contentType);
        ex.put(Message.ENCODING, msg.get(Message.ENCODING));
        ex.put(Message.ACCEPT_CONTENT_TYPE, request.getHeader("Accept"));
        
        msg.put(Message.QUERY_STRING, request.getQueryString());
        
      
        return msg;
    }
    
    private void setupExchange(Exchange ex, HttpServletRequest request, HttpServletResponse response) {
       ex.put(HttpServletRequest.class, request);
       ex.put(HttpServletResponse.class, response);
       ex.put(HttpSession.class, request.getSession());
       ex.put("request", request);
       ex.put("response", response);
       ex.put("session", request.getSession());
       ParameterParser parameterParser = new DefaultParameterParser(ex, request, upload, filters, trimming, htmlFieldSuffix);
       ex.put(ParameterParser.class, parameterParser);
       
       CookieParser cookie = new DefaultCookieParser(request, response);
       ex.put(CookieParser.class, cookie);
        
    }
    private String setEncoding(final Message inMessage, final HttpServletRequest req, final String contentType) throws IOException {

        String enc = HttpHeaderHelper.findCharset(contentType);
        if (enc == null) {
            enc = req.getCharacterEncoding();
        }
        
        if (enc != null && enc.endsWith("\"")) {
            enc = enc.substring(0, enc.length() - 1);
        }
        if (enc != null || "POST".equals(req.getMethod()) || "PUT".equals(req.getMethod())) {
            // allow gets/deletes/options to not specify an encoding
            String normalizedEncoding = HttpHeaderHelper.mapCharset(enc);
            if (normalizedEncoding == null) {
                LOG.warn("Invalid encoding {}",enc);
                throw new IOException("Invalid encoding "+enc);
            }
            inMessage.put(Message.ENCODING, normalizedEncoding);
        }
        return contentType;
    }
    

    public Component getComponent() {
        return component;
    }
    
    public ParameterParserFilter[] getFilters() {
        return filters;
    }
    
    public void setFilters(ParameterParserFilter[] filters) {
        this.filters = filters;
    }
    
    public boolean isTrimming() {
        return trimming;
    }
    
    public void setTrimming(boolean trimming) {
        this.trimming = trimming;
    }
    
    public String getHtmlFieldSuffix() {
        return htmlFieldSuffix;
    }
    
    public void setHtmlFieldSuffix(String htmlFieldSuffix) {
        this.htmlFieldSuffix = htmlFieldSuffix;
    }
    
}
