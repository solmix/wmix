/*
 * Copyright 2014 The Solmix Project
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

package org.solmix.wmix.config;

import java.util.List;

import org.solmix.commons.util.StringUtils;
import org.solmix.wmix.ComponentConfig;
import org.solmix.wmix.Controller;
import org.solmix.wmix.WmixEndpoint;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月15日
 */

public class ComponentConfigImpl implements ComponentConfig
{

    private String name;

    private String path;

    private Controller controller;

    private List<WmixEndpoint> endpoints;

    @Override
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtils.trimToNull(name);
    }

    @Override
    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = StringUtils.trimToNull(path);
    }

    @Override
    public Controller getController() {
        return controller;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    @Override
    public List<WmixEndpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<WmixEndpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
