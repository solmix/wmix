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

import org.solmix.runtime.Container;
import org.solmix.wmix.web.WmixConfiguration;
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

    private RequestURIFilter passthruFilter;

    public void setExcludes(String excludes) {
        excludeFilter = new RequestURIFilter(excludes);
    }

    @Override
    protected void init() throws Exception {
        Container container = findContainer();
        if(container!=null){
            container.getExtension(WmixConfiguration.class);
        }
    }
    @Override
    protected void doFilter(HttpServletRequest request,
        HttpServletResponse response, FilterChain chain) throws IOException,
        ServletException {
        // TODO Auto-generated method stub
        
    }

    public void setPassthru(String passthru) {
        passthruFilter = new RequestURIFilter(passthru);
    }

    public String getServletContainerKey() {
        return servletContainerKey == null ? 
            WmixContextLoaderListener.CONTAINER_KEY
            : servletContainerKey;
    }

    private Container findContainer() {
        return null;
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
