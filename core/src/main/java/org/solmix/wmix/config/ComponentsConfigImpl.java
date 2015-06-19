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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.solmix.commons.util.StringUtils;
import org.solmix.wmix.ComponentConfig;
import org.solmix.wmix.ComponentsConfig;
import org.solmix.wmix.RootController;
import org.solmix.wmix.support.RootControllerImpl;
import org.solmix.wmix.support.WmixDefaultController;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年6月15日
 */

public class ComponentsConfigImpl implements ComponentsConfig
{

    private Boolean autoDiscovery;

    private String discoveryLocationPattern;

    private String defaultComponent;

    private Class<?> defaultControllerClass;

    private Class<?> rootControllerClass;

    private Map<String, ComponentConfig> components;

    private RootController rootController;

    private boolean productionMode;

    private String internalPrefix;
    
    private final Map<String, Object> properties = new ConcurrentHashMap<String, Object>(16, 0.75f, 4);
    
    @Override
    public Boolean isAutoDiscovery() {
        return autoDiscovery == null ? true : autoDiscovery;
    }

    /**   */
    public void setAutoDiscovery(Boolean autoDiscover) {
        this.autoDiscovery = autoDiscover;
    }

    @Override
    public String getDiscoveryLocationPattern() {
        return discoveryLocationPattern == null ? "/WEB-INF/wmix-*.xml" : discoveryLocationPattern;
    }

    /**   */
    public void setDiscoveryLocationPattern(String discoveryLocationPattern) {
        this.discoveryLocationPattern = StringUtils.trimToNull(discoveryLocationPattern);
    }

    @Override
    public String getDefaultComponent() {
        return defaultComponent;
    }

    @Override
    public Map<String, ComponentConfig> getComponents() {
        return components;
    }

    @Override
    public RootController getRootController() {
        return rootController;
    }

    /**   */
    public void setDefaultComponent(String defaultComponent) {
        this.defaultComponent = defaultComponent;
    }

    /**   */
    public void setComponents(Map<String, ComponentConfig> components) {
        this.components = components;
    }
    
    public void setComponent(ComponentConfig component){
        if(this.components==null){
            this.components= new HashMap<String, ComponentConfig>();
        }
        this.components.put(component.getName(), component);
    }

    public Class<?> getDefaultControllerClass() {
        return defaultControllerClass == null ? WmixDefaultController.class : defaultControllerClass;
    }

    public void setDefaultControllerClass(Class<?> defaultControllerClass) {
        this.defaultControllerClass = defaultControllerClass;
    }

    public Class<?> getRootControllerClass() {
        return rootControllerClass == null ? RootControllerImpl.class : rootControllerClass;
    }

    public void setRootControllerClass(Class<?> rootControllerClass) {
        this.rootControllerClass = rootControllerClass;
    }

    /**   */
    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    public String getInternalPrefix() {
        return internalPrefix;
    }

    public void setInternalPrefix(String internalPrefix) {
        this.internalPrefix = internalPrefix;
    }

    @Override
    public boolean isProductionMode() {
        return productionMode;
    }

    public void setProductionMode(boolean productionMode) {
        this.productionMode = productionMode;
    }
    @Override
    public Map<String, Object> getProperties() {
        return properties;
    }

    @Override
    public void setProperties(Map<String, Object> map) {
        properties.clear();
        properties.putAll(map);
    }
}
