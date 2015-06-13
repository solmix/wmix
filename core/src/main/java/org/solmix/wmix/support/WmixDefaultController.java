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

package org.solmix.wmix.support;

import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassLoaderUtils.ClassLoaderHolder;
import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Endpoint;
import org.solmix.runtime.exchange.EndpointException;
import org.solmix.runtime.exchange.ProtocolFactory;
import org.solmix.runtime.exchange.ProtocolFactoryManager;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.ServiceCreateException;
import org.solmix.runtime.exchange.TransporterFactoryManager;
import org.solmix.runtime.exchange.event.ServiceFactoryEvent;
import org.solmix.runtime.exchange.invoker.Invoker;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.model.ProtocolInfo;
import org.solmix.runtime.exchange.support.AbstractEndpointFactory;
import org.solmix.runtime.exchange.support.AbstractServiceFactory;
import org.solmix.runtime.exchange.support.DefaultEndpoint;
import org.solmix.runtime.interceptor.phase.PhasePolicy;
import org.solmix.runtime.resource.ResourceManager;
import org.solmix.wmix.Component;
import org.solmix.wmix.Controller;
import org.solmix.wmix.WmixConfiguration;
import org.solmix.wmix.exchange.ServletTransporter;
import org.solmix.wmix.exchange.WmixPhasePolicy;
import org.solmix.wmix.exchange.WmixProtocolFactory;
import org.solmix.wmix.exchange.WmixServiceFactory;
import org.solmix.wmix.servlet.ServletContextResourceResolver;

/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$ 2015年2月21日
 */

public class WmixDefaultController extends AbstractEndpointFactory implements Controller
{

    private static final long serialVersionUID = 1644365003770339343L;

    private static final Logger LOG = LoggerFactory.getLogger(WmixDefaultController.class);
   
    private static final String TRANSPORTER_ID = "http://solmix.org/wmix/transport/servlet";

    private Component component;

    private AbstractServiceFactory serviceFactory;

    private PhasePolicy phasePolicy;
    
    private Endpoint endpoint;
    
    @Override
    public void init(Component component) {
        this.component = component;
//            serviceFactory= new WmixServiceFactory(component.getComponentPath());
        serviceFactory= initServiceFactory();
        setContainer(component.getContainer());
        if (this.container != null) {
            ClassLoaderHolder origLoader = null;
            try{
                 ClassLoader loader=container.getExtension(ClassLoader.class);
                if (loader != null) {
                    origLoader = ClassLoaderUtils.setThreadContextClassloader(loader);
                }
              
                initResourceRelove();
               
                serviceFactory.setContainer(container);
                endpoint = createEndpoint();
                initTransport();
                Invoker invoker = serviceFactory.getInvoker();
                if(invoker==null){
                    endpoint.getService().setInvoker(createInvoker());
                }else{
                    endpoint.getService().setInvoker(invoker);
                }
                protocolFactory.addListener(getServletTransporter(), endpoint);
            }catch(IOException e){
                throw new ServiceCreateException(e);
            } catch (EndpointException e) {
                throw new ServiceCreateException(e);
            }finally{
                if(origLoader!=null){
                    origLoader.reset();
                }
            }
        }
    }
    protected AbstractServiceFactory initServiceFactory(){
        return new WmixServiceFactory(component.getComponentPath());
    }

    private void initTransport() throws IOException {
        if(this.transporterFactory==null){
            TransporterFactoryManager  tf = this.container.getExtension(TransporterFactoryManager.class);
            transporterFactory=tf.getFactory(TRANSPORTER_ID);
        }
        if(this.transporter==null){
            this.transporter= transporterFactory.getTransporter(endpoint.getEndpointInfo(), container);
        }
        if(!(transporter instanceof ServletTransporter)){
            throw new IllegalArgumentException();
        }
        LOG.info("init controller on {}",endpoint.getEndpointInfo().getAddress());
        
    }
    
    public ServletTransporter getServletTransporter(){
        return (ServletTransporter)transporter;
    }

    private Invoker createInvoker() {
        return serviceFactory.createInvoker();
    }

    @Override
    public void setContainer(Container container) {
        super.setContainer(container);
        serviceFactory.setContainer(container);
    }

    private void initResourceRelove() {
        ServletContext sc = this.container.getExtension(ServletContext.class);
        ResourceManager rm = this.container.getExtension(ResourceManager.class);
        // 注册ServletContext resource resolver.
        rm.addResourceResolver(new ServletContextResourceResolver(sc));
    }

    public WmixConfiguration getWmixConfiguration() {
        return getComponent().getWmixConfiguration();
    }

    @Override
    public boolean service(HttpServletRequest request, HttpServletResponse response) throws Exception {

        invokeTransporter(request,response);
        return false;
    }

   
    private void invokeTransporter(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ServletTransporter transporter= getServletTransporter();
        //TODO 在invoke之前创建message,供codition判断分发。endpoint可以调用protocol重组message。
        transporter.invoke(request,response);
    }

    public Component getComponent() {
        return component;
    }

    @Override
    protected Endpoint createEndpoint() throws EndpointException {
        Service service = serviceFactory.getService();
        if (service == null) {
            service = serviceFactory.create();
        }

        EndpointInfo ei = createEndpointInfo(service);
        Endpoint ep = new DefaultEndpoint(container, service, ei, getPhasePolicy());
        if (properties != null) {
            ep.putAll(properties);
        }
        if (getInFaultInterceptors() != null) {
            ep.getInFaultInterceptors().addAll(getInFaultInterceptors());
        }
        if (getOutFaultInterceptors() != null) {
            ep.getOutFaultInterceptors().addAll(getOutFaultInterceptors());
        }
        if (getInInterceptors() != null) {
            ep.getInInterceptors().addAll(getInInterceptors());
        }
        if (getOutInterceptors() != null) {
            ep.getOutInterceptors().addAll(getOutInterceptors());
        }
        ep.put(AbstractServiceFactory.class.getName(), serviceFactory);
        return ep;
    }

    protected EndpointInfo createEndpointInfo(Service service) {

        EndpointInfo ei = new EndpointInfo();
        ei.setTransportor(TRANSPORTER_ID);
        ei.setName(serviceFactory.getService().getServiceName());
        ei.setAddress(getAddress());
        ProtocolInfo ptl = createProtocolInfo();
        ei.setProtocol(ptl);
        serviceFactory.pulishEvent(ServiceFactoryEvent.ENDPOINT_CREATED, ei);
        return ei;
    }

    protected ProtocolInfo createProtocolInfo() {
        ProtocolFactoryManager pfm = container.getExtension(ProtocolFactoryManager.class);
        try {
            pfm.getProtocolFactory(WmixProtocolFactory.WMIX_PROTOCOL_ID);
        } catch (Exception e) { 
            //不存在，就注册一个.
            pfm.register(WmixProtocolFactory.WMIX_PROTOCOL_ID, new WmixProtocolFactory(container));
        }
        ProtocolFactory pf = pfm.getProtocolFactory(WmixProtocolFactory.WMIX_PROTOCOL_ID);
        setProtocolFactory(pf);
        ProtocolInfo pi = pf.createProtocolInfo(serviceFactory.getService(), WmixProtocolFactory.WMIX_PROTOCOL_ID, null);
        serviceFactory.pulishEvent(ServiceFactoryEvent.PROTOCOL_CREATED, pi);
        return pi;
    }

    public AbstractServiceFactory getServiceFactory() {
        return serviceFactory;
    }

    public void setServiceFactory(AbstractServiceFactory serviceFactory) {
        this.serviceFactory = serviceFactory;
    }

    @Override
    public String getAddress() {
        String addr = super.getAddress();
        if (addr == null) {
            return component.getComponentPath();
        } else {
            return addr;
        }
    }

    public PhasePolicy getPhasePolicy() {
        if (phasePolicy == null) {
            phasePolicy = new WmixPhasePolicy();
        }
        return phasePolicy;
    }

    public void setPhasePolicy(PhasePolicy phasePolicy) {
        this.phasePolicy = phasePolicy;
    }

}
