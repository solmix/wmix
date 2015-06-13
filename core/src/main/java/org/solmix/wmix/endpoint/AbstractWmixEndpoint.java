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

import org.solmix.commons.util.Assert;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassLoaderUtils.ClassLoaderHolder;
import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.EndpointException;
import org.solmix.runtime.exchange.Processor;
import org.solmix.runtime.exchange.Protocol;
import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.ServiceCreateException;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.interceptor.phase.PhasePolicy;
import org.solmix.runtime.interceptor.support.InterceptorProviderAttrSupport;
import org.solmix.wmix.Component;
import org.solmix.wmix.WmixEndpoint;
import org.solmix.wmix.condition.Condition;
import org.solmix.wmix.exchange.ServletTransporter;
import org.solmix.wmix.exchange.WmixPhasePolicy;


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

    private List<Closeable> cleanupHooks;

    private Container container;

    private  Service service;
    
    private Processor inFaultProcessor;

    private Processor outFaultProcessor;
    
    private  PhasePolicy phasePolicy;
    
    private Condition condition;
    
    private String rule;
    
    private String ruleType;
    
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
//            initResourceRelove();
            service =createService();
            createEndpoint(container,service,createEndpointInfo());
            initTransport();
           
            
            protocolFactory.addListener(getServletTransporter(), this);
        }catch(IOException e){
            throw new ServiceCreateException(e);
        } catch (EndpointException e) {
            throw new ServiceCreateException(e);
        }finally{
            if(origLoader!=null){
                origLoader.reset();
            }
        }
        buildCondition();
    }
    
  
    /**
     * @return
     */
    private EndpointInfo createEndpointInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    protected abstract Service createService();
    /**
     * @return
     */
    protected ServletTransporter getServletTransporter() {
        // TODO Auto-generated method stub
        return null;
    }

   
    /**
     * 
     */
    protected void initTransport()throws IOException {
        // TODO Auto-generated method stub
        
    }

    protected void createEndpoint(Container container2, Service service2, EndpointInfo endpointInfo2) throws EndpointException{
        // TODO Auto-generated method stub
        
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
  
    @Override
    public String getRule() {
        return rule;
    }
    
    @Override
    public void setRule(String rule) {
       this.rule=rule;
    }
    
    @Override
    public String getRuleType() {
        return ruleType;
    }
    
    @Override
    public void setRuleType(String ruleType) {
        this.ruleType=ruleType;
    }

}
