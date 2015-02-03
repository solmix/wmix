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

package org.solmix.wmix.web.context;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerFactory;
import org.solmix.runtime.support.spring.SpringContainerFactory;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年1月30日
 */

public class WmixContextLoaderListener extends ContextLoaderListener {

    private static final Logger LOG = LoggerFactory.getLogger(WmixContextLoaderListener.class);

    public static final String CONTAINER_KEY = WmixContextLoaderListener.class.getName()
        + ".CONTAINER_KEY";

    private ServletContext servletContext;

    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        servletContext = event.getServletContext();
        String root = servletContext.getRealPath("/");
        if (LOG.isInfoEnabled()) {
            LOG.info("Initial Web context,used path:" + root
                + " As [solmix.base]");
        }
        System.setProperty("solmix.base", root);
        WebApplicationContext context = ContextLoader.getCurrentWebApplicationContext();
        if (context == null) {
            String msg = "Can't found spring WebApplicationContext";
            servletContext.log(msg);
            throw new IllegalStateException(msg);
        }
        SpringContainerFactory factory = new SpringContainerFactory(context);
        Container c = factory.createContainer();
        ContainerFactory.setThreadDefaultContainer(c);
        servletContext.setAttribute(CONTAINER_KEY, c);
    }
}
