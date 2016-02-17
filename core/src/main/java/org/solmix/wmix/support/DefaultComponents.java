
package org.solmix.wmix.support;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;

import org.solmix.commons.util.Assert;
import org.solmix.runtime.Container;
import org.solmix.wmix.Component;
import org.solmix.wmix.ComponentConfig;
import org.solmix.wmix.Components;
import org.solmix.wmix.ComponentsConfig;
import org.solmix.wmix.Controller;
import org.solmix.wmix.RootController;
import org.solmix.wmix.WmixEndpoint;

public class DefaultComponents implements Components
{

    private String name;

    private final String defaultComponentName;

    private final Map<String, Component> components;

    private final RootComponent rootComponent;

    private final RootController rootController;
    
    private final ServletContext servletContext;

    public DefaultComponents(String name, String defaultComponent, RootController root,ServletContext servletContext)
    {
        this.name = name;
        this.defaultComponentName = defaultComponent;
        this.components = new java.util.concurrent.ConcurrentHashMap<String, Component>(4);
        this.rootComponent = new RootComponent();
        this.servletContext=servletContext;
        this.rootController = Assert.assertNotNull(root, "No root Controller");
        rootController.init(this);
    }

    public void addComponent(Component component) {
        components.put(component.getName(), component) ;
    }
    
    public Component removeComponent(Component component) {
        return components.remove(component.getName());
    }
    @Override
    public Iterator<Component> iterator() {
        return components.values().iterator();
    }

    public void destroy() {
        components.clear();
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
    public ComponentsConfig getComponentsConfig() {
        return null;
    }

    @Override
    public Container getParentContainer() {
        return null;
    }

    @Override
    public String getName() {
        return name;
    }

    private class RootComponent implements Component
    {

        @Override
        public Components getComponents() {
            return DefaultComponents.this;
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
        public ComponentConfig getComponentConfig() {
            return null;
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
            return DefaultComponents.this.toString();
        }

        @Override
        public void setContainer(Container container) {
            Assert.unsupportedOperation("RootComponent.setContainer()");

        }

        @Override
        public List<WmixEndpoint> getEndpoints() {
            Assert.unsupportedOperation("RootComponent.getEndpoints()");
            return null;
        }

        @Override
        public void setComponents(Components components) {
        }
    }

    @Override
    public ServletContext getServletContext() {
        return servletContext;
    }
}
