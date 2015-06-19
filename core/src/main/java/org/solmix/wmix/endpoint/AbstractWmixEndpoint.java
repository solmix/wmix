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
package org.solmix.wmix.endpoint;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Executor;

import org.slf4j.Logger;
import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassLoaderUtils.ClassLoaderHolder;
import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.EndpointException;
import org.solmix.runtime.exchange.Processor;
import org.solmix.runtime.exchange.Protocol;
import org.solmix.runtime.exchange.ProtocolFactory;
import org.solmix.runtime.exchange.ProtocolFactoryManager;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.ServiceCreateException;
import org.solmix.runtime.exchange.Transporter;
import org.solmix.runtime.exchange.TransporterFactory;
import org.solmix.runtime.exchange.TransporterFactoryManager;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.model.ProtocolInfo;
import org.solmix.runtime.interceptor.phase.PhasePolicy;
import org.solmix.runtime.interceptor.support.InterceptorProviderAttrSupport;
import org.solmix.wmix.Component;
import org.solmix.wmix.WmixEndpoint;
import org.solmix.wmix.condition.Condition;
import org.solmix.wmix.exchange.ServletTransportationFactory;
import org.solmix.wmix.exchange.ServletTransporter;
import org.solmix.wmix.exchange.WmixPhasePolicy;
import org.solmix.wmix.exchange.WmixProtocolFactory;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年6月13日
 */

public abstract class AbstractWmixEndpoint extends InterceptorProviderAttrSupport implements WmixEndpoint
{
    private static final long serialVersionUID = 3331770484397569065L;
    

    private  EndpointInfo endpointInfo;

    private Protocol protocol;
    
    private ProtocolFactory protocolFactory;

    private List<Closeable> cleanupHooks;

    private Container container;

    private  Service service;
    
    private ServletTransporter transporter;
    
    private Processor inFaultProcessor;

    private Processor outFaultProcessor;
    
    private  PhasePolicy phasePolicy;
    
    private Condition condition;
    
    private String rule;
    
    private String ruleType;
    
    private String address;
    
    public AbstractWmixEndpoint(){
        this(new WmixPhasePolicy());
    }
    
    public AbstractWmixEndpoint(PhasePolicy policy){
        this.phasePolicy=policy;
    }
    
    public void init(Component component){
        this.container=component.getContainer();
        Assert.isNotNull(this.container, "Endpoint container == null");
        ClassLoaderHolder origLoader = null;
        try{
             ClassLoader loader=container.getExtension(ClassLoader.class);
            if (loader != null) {
                origLoader = ClassLoaderUtils.setThreadContextClassloader(loader);
            }
            buildCondition();
            service =createService();
            endpointInfo=createEndpointInfo(service);
            initEndpoint();
            transporter=createTrasporter();
            //add listener and start up.
            protocolFactory.addListener(transporter, this);
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
  
    protected void initEndpoint() {
        
    }

    protected abstract Logger getLogger();
  
    /**
     * @return
     */
    protected EndpointInfo createEndpointInfo(Service service)throws EndpointException {
        EndpointInfo ei = new EndpointInfo();
        ei.setTransportor(ServletTransportationFactory.DEFAULT_TRANSPORT_ID);
        ei.setName(service.getServiceName());
        ei.setAddress(getAddress());
        ProtocolInfo ptl = createProtocolInfo();
        ei.setProtocol(ptl);
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
        protocolFactory= pfm.getProtocolFactory(WmixProtocolFactory.WMIX_PROTOCOL_ID);
        
        ProtocolInfo pi = protocolFactory.createProtocolInfo(service, WmixProtocolFactory.WMIX_PROTOCOL_ID, null);
        return pi;
    }


    protected abstract Service createService();
   
    protected ServletTransporter createTrasporter() throws IOException {
            TransporterFactoryManager  tf = this.container.getExtension(TransporterFactoryManager.class);
            TransporterFactory   transporterFactory=tf.getFactory(ServletTransportationFactory.DEFAULT_TRANSPORT_ID);
        if(this.transporter==null){
            Transporter ts= transporterFactory.getTransporter(endpointInfo, container);
            if(!(ts instanceof ServletTransporter)){
                throw new IllegalArgumentException();
            }
            this.transporter=(ServletTransporter)ts;
        }
        getLogger().info("create transporter on {}",endpointInfo.getAddress());
        return this.transporter;
    }

    protected void buildCondition(){
        if(condition==null){
            
        }
    }
    @Override
    public EndpointInfo getEndpointInfo() {
        return endpointInfo;
    }

   
    @Override
    public Protocol getProtocol() {
        return protocol;
    }

    @Override
    public Service getService() {
        return service;
    }

    @Override
    public void addCleanupHook(Closeable c) {
        if (cleanupHooks == null) {
            cleanupHooks = new CopyOnWriteArrayList<Closeable>();
        }
        cleanupHooks.add(c);
    }

    @Override
    public List<Closeable> getCleanupHooks() {
        if (cleanupHooks == null) {
            return Collections.emptyList();
        }
        return cleanupHooks;
    }

    @Override
    public PhasePolicy getPhasePolicy() {
        return phasePolicy;
    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public void setExecutor(Executor executor) {
        //DO NOTHING not support
    }

    @Override
    public Processor getOutFaultProcessor() {
        return outFaultProcessor;
    }

    @Override
    public void setOutFaultProcessor(Processor p) {
        this.outFaultProcessor=p;

    }


    @Override
    public Processor getInFaultProcessor() {
        return inFaultProcessor;
    }

    @Override
    public void setInFaultProcessor(Processor p) {
        this.inFaultProcessor=p;
    }

    
    @Override
    public Condition getCondition() {
        return condition;
    }

    
    @Override
    public void setCondition(Condition condition) {
        this.condition=condition;

    }
  
    public String getAddress() {
        return address;
    }

    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String toString(){
       
        return this.getClass().getName()+"@"+System.identityHashCode(this);
    }

}
