package org.solmix.rest;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Service;
import org.solmix.exchange.data.DataProcessor;
import org.solmix.exchange.interceptor.support.InterceptorProviderAttrSupport;
import org.solmix.exchange.invoker.Invoker;
import org.solmix.exchange.model.EndpointInfo;
import org.solmix.exchange.model.NamedID;
import org.solmix.exchange.model.ServiceInfo;


public class RestService extends InterceptorProviderAttrSupport implements Service
{

    private static final long serialVersionUID = -6315846318409396395L;
    private Map<NamedID, Endpoint> endpoints = new HashMap<NamedID, Endpoint>();
    private Invoker invoker;
    private DataProcessor dataProcessor;
    
    private ServiceInfo info;
    public RestService(ServiceInfo info){
        this.info=info;
    }
    
    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public NamedID getServiceName() {
        return info.getName();
    }

    @Override
    public void setInvoker(Invoker invoker) {
        this.invoker=invoker;
    }

    @Override
    public Map<NamedID, Endpoint> getEndpoints() {
        
        return endpoints;
    }

    @Override
    public EndpointInfo getEndpointInfo(NamedID eid) {
        if (endpoints.get(eid) != null) {
            return endpoints.get(eid).getEndpointInfo();
        }
        return null;
    }

    @Override
    public DataProcessor getDataProcessor() {
        return dataProcessor;
    }

    @Override
    public void setDataProcessor(DataProcessor df) {
        this.dataProcessor=df;
    }

    @Override
    public Executor getExecutor() {
        return null;
    }

    @Override
    public List<ServiceInfo> getServiceInfos() {
        return Arrays.asList(info);
    }

    @Override
    public void setExecutor(Executor executor) {
        //NOT SET
    }

    @Override
    public ServiceInfo getServiceInfo() {
        return info;
    }

}
