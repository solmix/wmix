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
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.solmix.commons.util.DataUtils;
import org.solmix.commons.util.ServletUtils;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.wmix.Components;
import org.solmix.wmix.RootController;
import org.solmix.wmix.osgi.ComponentsLoaderListener;
import org.solmix.wmix.util.RequestURIFilter;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月30日
 */

public class WmixFilter extends AbstractWmixFilter {

    private String servletContainerKey;
    
    private String internalPathPrefix;

    private RequestURIFilter excludeFilter;

    private RequestURIFilter passThroughFilter;

    private Components components;
    /**
     * 设置需要排除掉的URL。
     * @param excludes
     */
    public void setExcludes(String excludes) {
        excludeFilter = new RequestURIFilter(excludes);
    }

    @Override
    protected void init() throws Exception {
        Container container = findContainer();
        if (container != null) {
            components = container.getExtension(Components.class);
            if (this.passThroughFilter != null) {
                RootController root = components.getRootController();
                if (root instanceof PassThroughSupport) {
                    ((PassThroughSupport) root).setPassThroughFilter(passThroughFilter);
                } else {
                    LOG.warn(
                        "You have specified pass through Filter in /WEB-INF/web.xml.  "
                            + "It will not take effect because the implementation of WmixRootController ({}) does not support this feature.",
                        root.getClass().getName());
                }

            }
        } else {
            
            String name =getServletContext().getInitParameter("componentsName");
            if(StringUtils.isEmpty(name)){
                name=Components.DEFAULT_NAME;
            }
            Object cobj = getServletContext().getAttribute(ComponentsLoaderListener.getComponentsContextAttributeName(name));
            if (cobj instanceof Components) {
                components = (Components) cobj;
            }

        }
        if (components == null) {
            throw new ServletException("No specified components");
        }

    }
    @Override
    protected void doFilter(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain) throws IOException,ServletException {
    	  String path = ServletUtils.getResourcePath(request);
          if (isExcluded(path)) {
              LOG.trace("Excluded request: {}", path);
              chain.doFilter(request, response);
              return;
          } else {
              LOG.trace("Accepted and started to process request: {}", path);
          }
          try {
			components.getRootController().service(request, response, chain);
          } catch (IOException e) {
            throw e;
          } catch (Exception e) {
            throw new ServletException(e);
          }
    }

    /**
     * 不经过Wmix提供的interceptor
     * @param passthru
     */
    public void setPassThrough(String passthru) {
    	passThroughFilter = new RequestURIFilter(passthru);
    }

    public String getServletContainerKey() {
        return servletContainerKey == null ? 
            Components.CONTAINER_KEY
            : servletContainerKey;
    }

    private Container findContainer() {
    	Container container=null;
    	String key =getServletContainerKey();
    	if(key==null){
    		container=ContainerFactory.getThreadDefaultContainer(false);
    	}else{
    		Object cobj=getServletContext().getAttribute(key);
    		if(cobj instanceof Container){
    			container=(Container)cobj;
    		}
    	}
        return container;
    }

    public void setServletContainerKey(String containerKey) {
        servletContainerKey = containerKey;
    }
    @Override
    protected void initContext(FilterConfig config) throws ServletException {
        Map<String,String> properties  = new HashMap<String,String>();
        for (Enumeration<?> e = config.getInitParameterNames(); e.hasMoreElements();) {
            String key = (String) e.nextElement();
            String value = config.getInitParameter(key);

            properties.put(key, value);
        }
        try {
            DataUtils.setProperties(properties, this);
        } catch (Exception e) {
           throw new ServletException("Auto set filter config to filter",e);
        }

    }

    boolean isExcluded(String path) {
        if (excludeFilter != null && excludeFilter.matches(path)) {
            if (!isInternalRequest(path)) {
                return true;
            }
        }
        return false;
    }
    private boolean isInternalRequest(String path) {
        if (internalPathPrefix == null) {
            return false;
        }

        if (path.equals(internalPathPrefix)) {
            return true;
        }

        if (path.startsWith(internalPathPrefix) && path.charAt(internalPathPrefix.length()) == '/') {
            return true;
        }

        return false;
    }
    /**
     * @return
     */
    public Components getComponents() {
        return components;
    }

}
