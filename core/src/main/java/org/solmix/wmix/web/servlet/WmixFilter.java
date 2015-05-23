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

package org.solmix.wmix.web.servlet;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ServletUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.wmix.web.Components;
import org.solmix.wmix.web.RootController;
import org.solmix.wmix.web.context.WmixContextLoaderListener;
import org.solmix.wmix.web.util.RequestURIFilter;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月30日
 */

public class WmixFilter extends AbstractWmixFilter {

    private String servletContainerKey;

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
        if(container!=null){
        	components= container.getExtension(Components.class);
        	if(this.passThroughFilter!=null){
        		RootController root=components.getRootController();
            	if(root instanceof PassThroughSupport){
            		((PassThroughSupport)root).setPassThroughFilter(passThroughFilter);
            	}else{
            		LOG.warn("You have specified pass through Filter in /WEB-INF/web.xml.  "
                            + "It will not take effect because the implementation of WebxRootController ({}) does not support this feature.",
                            root.getClass().getName());
            	}
        	
        	}
    	}else{
    		throw new ServletException("No specified container");
    	}
    	
    }
    @Override
    protected void doFilter(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain) throws IOException,ServletException {
    	  String path = ServletUtils.getResourcePath(request);
          if (isExcluded(path)) {
        	  LOG.debug("Excluded request: {}", path);
              chain.doFilter(request, response);
              return;
          } else {
              LOG.debug("Accepted and started to process request: {}", path);
          }
          try {
			components.getRootController().service(request, response, chain);
          } catch (IOException e) {
            throw e;
          } catch (ServletException e) {
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
            WmixContextLoaderListener.CONTAINER_KEY
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
    	Assert.assertNotNull(container, "No solmix Container  \"%s\" found : not registered?",key);
        return container;
    }

    public void setServletContainerKey(String containerKey) {
        servletContainerKey = containerKey;
    }

    boolean isExcluded(String path) {
        // 如果指定了excludes，并且当前requestURI匹配任何一个exclude pattern，
        // 则立即放弃控制，将控制还给servlet engine。
        // 但对于internal path，不应该被排除掉，否则internal页面会无法正常显示。
        if (excludeFilter != null && excludeFilter.matches(path)) {
//            if (!isInternalRequest(path)) {
                return true;
//            }
        }
        return false;
    }

}
