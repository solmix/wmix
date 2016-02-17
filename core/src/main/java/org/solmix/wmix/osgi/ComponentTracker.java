package org.solmix.wmix.osgi;

import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.solmix.wmix.Component;
import org.solmix.wmix.support.DefaultComponents;


public class ComponentTracker extends ServiceTracker<Component, Component>
{
    private final DefaultComponents components;

    public ComponentTracker(BundleContext context,DefaultComponents components)
    {
        super(context, Component.class, null);
        this.components=components;
    }

    @Override
    public Component addingService(ServiceReference<Component> reference){
       String componentsName= components.getName();
       Object o=reference.getProperty(Component.COMP_BELONG_TO);
       Component component = context.getService(reference);
       //加入components时初始化controller开始工作
       component.getController().init(component);
       if(o!=null&&o.equals(componentsName)){
           Iterator<Component> it= components.iterator();
           while(it.hasNext()){
               Component comp= it.next();
               if(comp.getName().equals(component.getName())){
                   components.removeComponent(comp);
               }
           }
           components.addComponent(component);
       }
        return component;
        
    }


    @Override
    public void removedService(ServiceReference<Component> reference, Component service){
        components.removeComponent(service);
    }
}
