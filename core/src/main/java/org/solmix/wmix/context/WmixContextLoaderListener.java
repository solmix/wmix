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

package org.solmix.wmix.context;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.regex.PathNameWildcardCompiler;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.Files;
import org.solmix.commons.util.StringUtils;
import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerEvent;
import org.solmix.runtime.ContainerListener;
import org.solmix.runtime.bean.BeanConfigurer;
import org.solmix.runtime.bean.ConfiguredBeanProvider;
import org.solmix.runtime.support.spring.SpringContainerFactory;
import org.solmix.wmix.Component;
import org.solmix.wmix.Components;
import org.solmix.wmix.Controller;
import org.solmix.wmix.RootController;
import org.solmix.wmix.WmixConfiguration;
import org.solmix.wmix.WmixConfiguration.ComponentConfig;
import org.solmix.wmix.WmixConfiguration.ComponentsConfig;
import org.solmix.wmix.config.WmixConfigurationImpl.ComponentsConfigImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.ServletContextResourcePatternResolver;

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
    private String                configurationName;
    private WebApplicationContext parentSpringContext;
    public String getConfigurationName() {
        return configurationName == null ? "wmix-configuration" : configurationName;
    }
    public String getComponentContextAttributeName(String componentName) {
        return CONTAINER_KEY + componentName;
    }
    /** 设置context中<code>Configuration</code>的名称。 */
    public void setConfigurationName(String webxConfigurationName) {
        this.configurationName = StringUtils.trimToNull(webxConfigurationName);
    }
    @Override
    public void contextInitialized(ServletContextEvent event) {
        super.contextInitialized(event);
        servletContext = event.getServletContext();
        parentSpringContext = ContextLoader.getCurrentWebApplicationContext();
        if (parentSpringContext == null) {
            String msg = "Can't found spring WebApplicationContext";
            servletContext.log(msg);
            throw new IllegalStateException(msg);
        }
        SpringContainerFactory factory = new SpringContainerFactory(parentSpringContext);
        Container parent = factory.createContainer();
        String root = servletContext.getRealPath("/");
        if (LOG.isInfoEnabled()) {
             parent.setProperty("solmix.base", root);
            LOG.info("Initial Web context,used path:" + root + " As [solmix.base]");
        }
        servletContext.setAttribute(CONTAINER_KEY, parent);
        setServletContextInContainer(parent);
        
        WmixConfiguration wc=  parent.getExtension(WmixConfiguration.class);
        ComponentsImpl components = createComponents(wc,parent);
        //Components 放入Container中.
        parent.setExtension(components,Components.class);
        parent.addListener(components);
    }
    
    
    /**
     * @param parent
     */
    private void setServletContextInContainer(Container c) {
        ServletContext sc=  c.getExtension(ServletContext.class);
        if(sc==null){
            c.setExtension(getServletContext(), ServletContext.class);
        }
    }
    
    private ComponentsImpl createComponents(WmixConfiguration parentConfig, Container c) {
        ComponentsConfig componentsConfig = getComponentsConfig(parentConfig);
        Map<String, String> componentNamesAndLocations = findComponents(componentsConfig, getServletContext());
        Map<String, ComponentConfig> specifiedComponents = componentsConfig.getComponents();
       
        Set<String> componentNames = new TreeSet<String>();

        componentNames.addAll(componentNamesAndLocations.keySet());
        componentNames.addAll(specifiedComponents.keySet());
        
        RootController root = componentsConfig.getRootController();
        if(root==null){
            root = (RootController) BeanUtils.instantiateClass(componentsConfig.getRootControllerClass());
        }
        ComponentsImpl components = new ComponentsImpl(c,componentsConfig.getDefaultComponent(),root,parentConfig);
        configure(c,components);
        for (String componentName : componentNames) {
            ComponentConfig componentConfig = specifiedComponents.get(componentName);

            String componentPath = null;
            Controller controller = null;

            if (componentConfig != null) {
                componentPath = componentConfig.getPath();
                controller = componentConfig.getController();
            }

            if (controller == null) {
                controller = (Controller) BeanUtils.instantiateClass(componentsConfig.getDefaultControllerClass());
            }

           ComponentImpl component = new ComponentImpl(components, componentName, componentPath,
                                                                componentName.equals(componentsConfig.getDefaultComponent()), controller,
                                                                getConfigurationName());

            components.addComponent(component);

            prepareComponent(component, componentNamesAndLocations.get(componentName));
        }
        return components;
    }
    /**
     * 为每一个组件创建Container.
     * @param component
     * @param string
     */
    private void prepareComponent(ComponentImpl component, String location) {
        String componentName = component.getName();
        
        SpringContainerFactory factory = new SpringContainerFactory(parentSpringContext);
        Container componentContainer = factory.createContainer(location);
        setServletContextInContainer(componentContainer);
        componentContainer.setId(componentName);
        componentContainer.addListener(component);
        component.setContainer(componentContainer);
        // 将Container保存在servletContext中
        String attrName = getComponentContextAttributeName(componentName);
        getServletContext().setAttribute(attrName, componentContainer);

        //在Container中设置ServletContext.
        setServletContextInContainer(componentContainer);
        component.getController().init(component);
        LOG.debug("Published WebApplicationContext of component {} as ServletContext attribute with name [{}]",
                  componentName, attrName);
        
    }

    /**
     * @param componentsConfig
     * @param servletContext2
     * @return
     */
    private Map<String, String> findComponents(
        ComponentsConfig componentsConfig, ServletContext servletContext) {
        String locationPattern = componentsConfig.getDiscoveryLocationPattern();
        String[] prefixAndPattern = checkComponentConfigurationLocationPattern(locationPattern);
        String prefix = prefixAndPattern[0];
        String pathPattern = prefixAndPattern[1];

        Map<String, String> componentNamesAndLocations = new TreeMap<String, String>();
        if(componentsConfig.isAutoDiscovery()){
            try {
                ResourcePatternResolver resolver = new ServletContextResourcePatternResolver(servletContext);
                Resource[] componentConfigurations = resolver.getResources(locationPattern);
                Pattern pattern = PathNameWildcardCompiler.compilePathName(pathPattern);
                if (componentConfigurations != null) {
                    for (Resource resource : componentConfigurations) {
                        String path = resource.getURL().getPath();
                        Matcher matcher = pattern.matcher(path);

                        Assert.assertTrue(matcher.find(), "unknown component configuration file: %s", path);
                        String componentName = StringUtils.trimToNull(matcher.group(1));

                        if (componentName != null) {
                            componentNamesAndLocations.put(componentName,
                                                           prefix + pathPattern.replace("*", componentName));
                        }
                    }
                }
                
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            
        }
        return componentNamesAndLocations;
    }
    /**
     * 检查componentConfigurationLocationPattern是否符合以下要求：
     * <ol>
     * <li>非空。</li>
     * <li>包含且只包含一个<code>*</code>。</li>
     * <li>支持<code>classpath*:</code>前缀。</li>
     * </ol>
     * <p>
     * 返回数组：[前缀, 不包含<code>classpath*:</code>的路径]。
     * </p>
     */
    private String[] checkComponentConfigurationLocationPattern(String componentConfigurationLocationPattern) {
        if (componentConfigurationLocationPattern != null) {
            // 允许并剔除classpath*:前缀。
            boolean classpath = componentConfigurationLocationPattern.startsWith("classpath*:");
            String pathPattern = componentConfigurationLocationPattern;

            if (classpath) {
                pathPattern = componentConfigurationLocationPattern.substring("classpath*:".length()).trim();
            }

            // 检查路径。
            int index = pathPattern.indexOf("*");

            if (index >= 0) {
                index = pathPattern.indexOf("*", index + 1);

                if (index < 0) {
                    if (pathPattern.startsWith("/")) {
                        pathPattern = pathPattern.substring(1);
                    }

                    return new String[] { classpath ? "classpath:" : "", pathPattern };
                }
            }
        }

        throw new IllegalArgumentException("Invalid componentConfigurationLocationPattern: "
                                           + componentConfigurationLocationPattern);
    }
    protected void configure(Container container, Object bean) {
        configure(container, bean, null, null);
    }

    protected void configure(Container container, Object bean, String name,
        String extraName) {
        BeanConfigurer configurer = container.getExtension(BeanConfigurer.class);
        if (null != configurer) {
            configurer.configureBean(name, bean);
            if (extraName != null) {
                configurer.configureBean(extraName, bean);
            }
        }
    }
    public ServletContext getServletContext() {
        return servletContext;
    }
    private ComponentsConfig getComponentsConfig(WmixConfiguration parentConfig) {
        ComponentsConfig csc = parentConfig.getComponentsConfig();
        if (csc == null) {
            //DEFAULT CONFIG.
            csc = new ComponentsConfigImpl();
        }
        return csc;
    }

    private static class ComponentsImpl implements Components,ContainerListener{
        private final WmixConfiguration          parentConfiguration;
        private final Container      parentContainer;
        private final Map<String, Component> components;
        private final RootComponent              rootComponent;
        private final String                     defaultComponentName;
        private final RootController         rootController;
        public ComponentsImpl(Container c, String defaultComponent,
            RootController root, WmixConfiguration parentConfig) {
            this.parentConfiguration =Assert. assertNotNull(parentConfig, "no parent webx-configuration");
            this.parentContainer=c;
            this.components= new HashMap<String, Component>();
            this.rootComponent= new RootComponent();
            this.defaultComponentName=defaultComponent;
            this.rootController=Assert.assertNotNull(root,"No root Controller");
            rootController.init(this);
        }
        public WmixConfiguration getParentConfiguration() {
            return parentConfiguration;
        }
        /**
         * @param component
         */
        public void addComponent(ComponentImpl component) {
            components.put(component.getName(), component) ;
        }

        @Override
        public void handleEvent(ContainerEvent event) {
            // TODO Auto-generated method stub
            
        }
        
        @Override
        public String[] getComponentNames() {
            String[] names = components.keySet().toArray(new String[components.size()]);
            Arrays.sort(names);
            return names;
        }

        @Override
        public Component getComponent(String name) {
            if (name == null) {
                return rootComponent;
            } else {
                return components.get(name);
            }
        }

        @Override
        public Component getDefaultComponent() {
            return defaultComponentName == null ? null : components.get(defaultComponentName);
        }

        @Override
        public Component matchedComponent(String path) {
            if (!path.startsWith("/")) {
                path = "/" + path;
            }

            Component defaultComponent = getDefaultComponent();
            Component matched = null;

            // 前缀匹配componentPath。
            for (Component component : this) {
                if (component == defaultComponent) {
                    continue;
                }

                String componentPath = component.getComponentPath();

                if (!path.startsWith(componentPath)) {
                    continue;
                }

                // path刚好等于componentPath，或者path以componentPath/为前缀
                if (path.length() == componentPath.length() || path.charAt(componentPath.length()) == '/') {
                    matched = component;
                    break;
                }
            }

            // fallback to default component
            if (matched == null) {
                matched = defaultComponent;
            }

            return matched;
        }

        @Override
        public RootController getRootController() {
            return rootController;
        }

        @Override
        public WmixConfiguration getParentWmixConfiguration() {
            return parentConfiguration;
        }

        @Override
        public Container getParentContainer() {
            return parentContainer;
        }
        private class RootComponent implements Component {
            @Override
            public Components getComponents() {
                return ComponentsImpl.this;
            }

            @Override
            public String getName() {
                return null;
            }

            @Override
            public String getComponentPath() {
                return "";
            }

            @Override
            public WmixConfiguration getWmixConfiguration() {
                return getParentConfiguration();
            }

            @Override
            public Controller getController() {
                Assert.unsupportedOperation("RootComponent.getController()");
                return null;
            }

            @Override
            public Container getContainer() {
                return getParentContainer();
            }

            @Override
            public String toString() {
                return ComponentsImpl.this.toString();
            }

            @Override
            public void setContainer(Container container) {
                Assert.unsupportedOperation("RootComponent.setContainer()");
                
            }
        }
      
        @Override
        public Iterator<Component> iterator() {
            return components.values().iterator();
        }
    }
    private static class ComponentImpl implements Component,ContainerListener{

        private final Components        components;
        private final String                name;
        private final String                componentPath;
        private final Controller        controller;
        private final String                configurationName;
        private       Container container;

        public ComponentImpl(Components components, String name, String path,
            boolean defaultComponent, Controller controller,
            String configurationName) {
            this.components = components;
            this.name = name;
            this.controller = controller;
            this.configurationName = configurationName;
            path = StringUtils.trimToNull(Files.normalizeAbsolutePath(path, true));
            if (defaultComponent) {
                Assert.assertTrue(path == null, "default component \"%s\" should not have component path \"%s\"", name, path);
                this.componentPath = "";
            } else if (path != null) {
                this.componentPath = path;
            } else {
                this.componentPath = "/" + name;
            }
        }
        @Override
        public void handleEvent(ContainerEvent event) {
            // FIXME
            
        }

        @Override
        public Components getComponents() {
            return components;
        }
       
        @Override
        public String getName() {
            return name;
        }
       
        @Override
        public String getComponentPath() {
            return componentPath;
        }
      
        @Override
        public WmixConfiguration getWmixConfiguration() {
            ConfiguredBeanProvider provider = container.getExtension(ConfiguredBeanProvider.class);
            if (provider != null) {
                return provider.getBeanOfType(configurationName,
                    WmixConfiguration.class);
            } else {
                return null;
            }
        }
      
        @Override
        public Controller getController() {
            return controller;
        }

        @Override
        public void setContainer(Container container) {
            this.container=container;
            
        }

        @Override
        public Container getContainer() {
            return container;
        }
        
    }
}
