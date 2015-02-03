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

import org.solmix.runtime.Container;
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
}
