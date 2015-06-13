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
package org.solmix.wmix.config;

import java.util.List;
import java.util.Map;

import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.exchange.Processor;
import org.solmix.wmix.Controller;
import org.solmix.wmix.RootController;
import org.solmix.wmix.WmixConfiguration;
import org.solmix.wmix.WmixEndpoint;
import org.solmix.wmix.support.RootControllerImpl;
import org.solmix.wmix.support.WmixDefaultController;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年2月2日
 */

public class WmixConfigurationImpl implements WmixConfiguration {

    private boolean productionMode;
    private String internalPrefix;
    private Processor processor;
    private Processor faultProcessor;
    private ComponentsConfig componentsConfig;
    
    @Override
    public Processor getProcessor() {
        return processor;
    }
    
    public void setProcessor(Processor p){
        processor=p;
    }

    
    @Override
    public Processor getFaultProcessor() {
        return faultProcessor;
    }
    
    public void setFaultProcessor(Processor p){
        this.faultProcessor=p;
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
        this.productionMode=productionMode;
    }

    
    @Override
    public ComponentsConfig getComponentsConfig() {
        return componentsConfig;
    }
    
    
    /**   */
    public void setComponentsConfig(ComponentsConfig componentsConfig) {
        this.componentsConfig = componentsConfig;
    }

    public static class ComponentsConfigImpl implements ComponentsConfig {
        private Boolean                      autoDiscovery;
        private String                       discoveryLocationPattern;
        private String                       defaultComponent;
        private Class<?>                     defaultControllerClass;
        private Class<?>                     rootControllerClass;
        private Map<String, ComponentConfig> components;
        private RootController           rootController;
        @Override
        public Boolean isAutoDiscovery() {
            return autoDiscovery==null?true:autoDiscovery;
        }
        
        /**   */
        public void setAutoDiscovery(Boolean autoDiscover) {
            this.autoDiscovery = autoDiscover;
        }


        @Override
        public String getDiscoveryLocationPattern() {
            return discoveryLocationPattern==null? "/WEB-INF/wmix-*.xml":discoveryLocationPattern;
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

    }
    public static class ComponentConfigImpl implements ComponentConfig {
        private String         name;
        private String         path;
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
        
        public void setEndpoints(List<WmixEndpoint> endpoints){
            this.endpoints=endpoints;
        }
    }
}
