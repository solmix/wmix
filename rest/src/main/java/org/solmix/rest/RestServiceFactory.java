package org.solmix.rest;

import org.solmix.commons.util.PackageUtils;
import org.solmix.exchange.Service;
import org.solmix.exchange.data.DataProcessor;
import org.solmix.exchange.event.ServiceFactoryEvent;
import org.solmix.exchange.invoker.Invoker;
import org.solmix.exchange.model.NamedID;
import org.solmix.exchange.model.ServiceInfo;
import org.solmix.exchange.support.AbstractServiceFactory;
import org.solmix.service.jackson.JacksonDataProcessor;

public class RestServiceFactory extends AbstractServiceFactory {

    private Invoker invoker;
    private String address;
    
    public RestServiceFactory(){
    }
    public RestServiceFactory(String address){
        this();
        this.address=address;
    }
	@Override
	public Service create() {
		pulishEvent(ServiceFactoryEvent.START_CREATE);
		initService();
		initDataProcessor();
		setupInterceptor();
		if(this.invoker!=null){
		    service.setInvoker(getInvoker());
		}else{
		    service.setInvoker(createInvoker());
		}
		Service service =getService();
		pulishEvent(ServiceFactoryEvent.END_CREATE,service);
		return service;
	}
	
    protected void initDataProcessor() {
       getDataProcessor().initialize(getService());
       service.setDataProcessor(getDataProcessor());
       pulishEvent(ServiceFactoryEvent.DATAPROCESSOR_INITIALIZED, getDataProcessor());
    }
    @Override
    protected DataProcessor defaultDataProcessor() {
        return new JacksonDataProcessor();
    }
    
    protected void initService(){
	    ServiceInfo info = new ServiceInfo();
	    RestService service = new RestService(info);
	    setService(service);
	    info.setName(getServiceId());
	}
	
	protected NamedID getServiceId(){
	    if(address==null){
              String ns = PackageUtils.getNamespace(PackageUtils.getPackageName(RestService.class));
              return new NamedID(ns, "service");
          }else{
              return new NamedID(address, "service");
          }
	}
	
	protected void setupInterceptor(){
	    super.initDefaultInterceptor();
	}

    @Override
    public Invoker getInvoker() {
        return invoker;
    }

    @Override
    public Invoker createInvoker() {
        return new RestInvoker(getContainer());
    }

}
