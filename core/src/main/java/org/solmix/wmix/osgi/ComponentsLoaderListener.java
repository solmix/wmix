
package org.solmix.wmix.osgi;

import java.util.Hashtable;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceRegistration;
import org.solmix.commons.util.StringUtils;
import org.solmix.wmix.Component;
import org.solmix.wmix.Components;
import org.solmix.wmix.RootController;
import org.solmix.wmix.support.DefaultComponents;
import org.solmix.wmix.support.RootControllerImpl;

/**
 * For OSGI web components loading.
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2016年1月28日
 */
public class ComponentsLoaderListener implements ServletContextListener
{


    private String componentsName;

    private ServletContext servletContext;

    private DefaultComponents components;
    
    private ComponentTracker tracker;
    private ServiceRegistration<Components> registration;
    
    private String defaultComponentName;
    
    public ComponentsLoaderListener(){
        
    }
    
    public ComponentsLoaderListener(String defaultComponetName){
        this.defaultComponentName=defaultComponetName;
    }
    
    public DefaultComponents getComponents(){
        return components;
    }

    public String getComponentsName() {
        return componentsName == null ? Components.DEFAULT_NAME : componentsName;
    }

    public static String getComponentsContextAttributeName(String componentName) {
        return Components.COMPONENTS_KEY + "_" + componentName;
    }

    public String getContainerContextAttributeName(String componentName) {
        return Components.CONTAINER_KEY + "_" + componentName;
    }

    /** 设置context中<code>ComponentsConfig</code>的名称。 */
    public void setComponentsName(String configurationName) {
        this.componentsName = StringUtils.trimToNull(configurationName);
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        servletContext = event.getServletContext();
        String name = servletContext.getInitParameter("componentsName");
        if (StringUtils.isEmpty(name)) {
            name = Components.DEFAULT_NAME;
        }
        setComponentsName(name);
        if (servletContext.getAttribute(getComponentsContextAttributeName(name)) != null) {
            throw new IllegalStateException("Cannot initialize context because there is already a root wmix components present - "
                + "check whether you have multiple ContextLoader* definitions in your web.xml!");
        }

        RootController root = new RootControllerImpl();
       
        DefaultComponents components = new DefaultComponents(name, defaultComponentName, root,servletContext);
        BundleContext bundleContext = getBundleContext();
        tracker = getComponentTracker(bundleContext, components);
        tracker.open();
        
        Hashtable<String, String> prop = new Hashtable<String, String>();
        prop.put(Component.COMP_BELONG_TO, components.getName());
        registration = bundleContext.registerService(Components.class, components, prop);
        components.getDefaultComponent();
        this.components = components;
        servletContext.setAttribute(getComponentsContextAttributeName(name), components);

    }
    
    protected BundleContext getBundleContext(){
        Bundle bundle = FrameworkUtil.getBundle(this.getClass());
        if(bundle!=null){
            return bundle.getBundleContext();
        }else{
            return null;
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        if(tracker!=null){
            tracker.close();
            tracker=null;
        }
        if(registration!=null){
            registration.unregister();
        }
        if (components != null) {
            components.destroy();
        }

    }
    
    protected ComponentTracker getComponentTracker(BundleContext context,DefaultComponents componets){
        return new ComponentTracker(context,componets);
    }

}
