/*
 * Copyright 2015 The Solmix Project
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
import java.util.concurrent.Executor;

import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Service;
import org.solmix.exchange.dataformat.DataFormat;
import org.solmix.exchange.interceptor.support.InterceptorProviderAttrSupport;
import org.solmix.exchange.invoker.Invoker;
import org.solmix.exchange.model.EndpointInfo;
import org.solmix.exchange.model.NamedID;
import org.solmix.exchange.model.ServiceInfo;


/**
 * 
 * @author solmix.f@gmail.com
 * @version $Id$  2015年8月14日
 */

public class TestService extends InterceptorProviderAttrSupport implements Service
{

    private static final long serialVersionUID = 6079290550980480351L;

    @Override
    public Invoker getInvoker() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public NamedID getServiceName() {
        // TODO Auto-generated method stub
        return null;
    }

 
    @Override
    public void setInvoker(Invoker invoker) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#getEndpoints()
     */
    @Override
    public Map<NamedID, Endpoint> getEndpoints() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#getEndpointInfo(org.solmix.exchange.model.NamedID)
     */
    @Override
    public EndpointInfo getEndpointInfo(NamedID eid) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#getDataFormat()
     */
    @Override
    public DataFormat getDataFormat() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#setDataFormat(org.solmix.exchange.dataformat.DataFormat)
     */
    @Override
    public void setDataFormat(DataFormat df) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#getExecutor()
     */
    @Override
    public Executor getExecutor() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#getServiceInfos()
     */
    @Override
    public List<ServiceInfo> getServiceInfos() {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#setExecutor(java.util.concurrent.Executor)
     */
    @Override
    public void setExecutor(Executor executor) {
        // TODO Auto-generated method stub
        
    }

    /**
     * {@inheritDoc}
     * 
     * @see org.solmix.exchange.Service#getServiceInfo()
     */
    @Override
    public ServiceInfo getServiceInfo() {
        // TODO Auto-generated method stub
        return null;
    }

}
