package org.solmix.wmix.datax;

import org.solmix.runtime.exchange.Service;
import org.solmix.runtime.exchange.event.ServiceFactoryEvent;
import org.solmix.runtime.exchange.invoker.Invoker;
import org.solmix.runtime.exchange.support.AbstractServiceFactory;
import org.solmix.runtime.interceptor.support.ServiceInvokerInterceptor;

public class DataxServiceFactory extends AbstractServiceFactory {

    private Invoker invoker;
    private String address;
    public DataxServiceFactory(String address){
        this.address=address;
    }
	@Override
	public Service create() {
		pulishEvent(ServiceFactoryEvent.START_CREATE);
		initService();
		setupInterceptor();
		if(this.invoker!=null){
		    service.setInvoker(getInvoker());
		}else{
		    service.setInvoker(createInvoker());
		}
		if(getDataProcessor()!=null){
		    service.setDataProcessor(getDataFormat());
		}
		Service service =getService();
		pulishEvent(ServiceFactoryEvent.END_CREATE,service);
		return service;
	}
	
	protected void initService(){
	    DataxService service = new DataxService(null,address);
	    setService(service);
	}
	
	protected void setupInterceptor(){
	    service.getInInterceptors().add(new ServiceInvokerInterceptor());
	}

    public Invoker getInvoker() {
        return invoker;
    }

    public Invoker createInvoker() {
        return new DataxInvoker();
    }

}
