package org.solmix.wmix.support;

import java.util.Arrays;
import java.util.List;

import org.solmix.runtime.Container;
import org.solmix.runtime.ContainerAware;
import org.solmix.wmix.Component;
import org.solmix.wmix.ComponentConfig;
import org.solmix.wmix.Components;
import org.solmix.wmix.Controller;
import org.solmix.wmix.WmixEndpoint;


public class DefaultComponent implements Component,ContainerAware
{
    private  Components               components;
    private  String                name;
    private  String                componentPath;
    private  Controller            controller;
    private       Container            container;
    private List<WmixEndpoint> endpoints;

    public DefaultComponent(){
        controller= new WmixDefaultController();
    }
    
    public DefaultComponent(String name,String path,Controller controller){
        this.controller=controller;
        this.name=name;
        this.componentPath=path;
    }
    public DefaultComponent(String name,Controller controller){
       this(name,null,controller);
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

    @Override
    public List<WmixEndpoint> getEndpoints() {
        return endpoints;
    }
    
    public void setEndpoint(WmixEndpoint endpoint){
        if(endpoints!=null){
            endpoints.add(endpoint);
        }else{
            endpoints=Arrays.asList(endpoint);
        }
    }
    
    
    public void setName(String name) {
        this.name = name;
    }

    
    public void setComponentPath(String componentPath) {
        this.componentPath = componentPath;
    }

    
    public void setController(Controller controller) {
        this.controller = controller;
    }

    
    public void setEndpoints(List<WmixEndpoint> endpoints) {
        this.endpoints = endpoints;
    }

    @Override
    public ComponentConfig getComponentConfig() {
        return new ComponentConfig(){

            @Override
            public String getName() {
                return DefaultComponent.this.getName();
            }

            @Override
            public String getPath() {
                return DefaultComponent.this.getComponentPath();
            }

            @Override
            public Controller getController() {
                return DefaultComponent.this.getController();
            }

            @Override
            public List<WmixEndpoint> getEndpoints() {
                return DefaultComponent.this.getEndpoints();
            }
            
        };
    }

    @Override
    public Components getComponents() {
        return components;
    }

    @Override
    public void setComponents(Components components) {
        this.components=components;
    }

}
