package org.solmix.rest;

import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.solmix.commons.util.ClassLoaderUtils;
import org.solmix.commons.util.ClassLoaderUtils.ClassLoaderHolder;
import org.solmix.exchange.Endpoint;
import org.solmix.exchange.Exchange;
import org.solmix.exchange.Message;
import org.solmix.exchange.Service;
import org.solmix.exchange.Transporter;
import org.solmix.exchange.data.DataProcessor;
import org.solmix.exchange.interceptor.support.MessageSenderInterceptor;
import org.solmix.exchange.processor.InFaultChainProcessor;
import org.solmix.exchange.processor.OutFaultChainProcessor;
import org.solmix.rest.interceptor.OutFaultInterceptor;
import org.solmix.rest.interceptor.RestOutInterceptor;
import org.solmix.rest.route.RouteRepository;
import org.solmix.rest.route.RouteRepositoryImpl;
import org.solmix.runtime.Container;
import org.solmix.wmix.Component;
import org.solmix.wmix.exchange.AbstractWmixEndpoint;
import org.solmix.wmix.exchange.WmixMessage;

public class RestEndpoint extends AbstractWmixEndpoint implements Endpoint {
	private static final Logger LOG = LoggerFactory.getLogger(RestEndpoint.class);

	private RestServiceFactory serviceFactory;
	private RouteRepository reposiotry;
	private Set<String> includePackages;
	private Set<String> excludePackages;
	private String encoding = Constant.encoding;

	public RestEndpoint() {
		serviceFactory = new RestServiceFactory();
	}
	
    @Override
    protected void setContainer(Container container) {
        super.setContainer(container);
        DataProcessor dataProcessor = container.getExtension(DataProcessor.class);
        serviceFactory.setDataProcessor(dataProcessor);
    }

	@Override
	public void init(Component component) {
		serviceFactory.setContainer(component.getContainer());
		super.init(component);
		ClassLoader classLoader = container.getExtension(ClassLoader.class);
		ClassLoaderHolder orig = null;
		if (classLoader != null) {
			orig = ClassLoaderUtils.setThreadContextClassloader(classLoader);
		}

		try {
			RouteRepositoryImpl reposiotry = new RouteRepositoryImpl();
			reposiotry.setIncludePackages(this.includePackages);
			reposiotry.setExcludePackages(this.excludePackages);
			reposiotry.setComponentPath(component.getComponentPath());
			reposiotry.setContainer(container);
			reposiotry.init();
			this.reposiotry=reposiotry;
		} finally {
			if (orig != null) {
				orig.reset();
			}
		}

	}

	@Override
	protected void prepareInterceptors() {
		setInFaultProcessor(new InFaultChainProcessor(container, getPhasePolicy()));
		setOutFaultProcessor(new OutFaultChainProcessor(container, getPhasePolicy()));
		getOutInterceptors().add(new MessageSenderInterceptor());
		getOutFaultInterceptors().add(new MessageSenderInterceptor());
		prepareInInterceptors();
		prepareOutInterceptors();
		prepareOutFaultInterceptors();
	}

	protected void prepareOutFaultInterceptors() {
		getOutFaultInterceptors().add(new OutFaultInterceptor());
	}

	protected void prepareOutInterceptors() {
		getOutInterceptors().add(new RestOutInterceptor());
	}

	protected void prepareInInterceptors() {
//		SgtInInterceptor in = new SgtInInterceptor();
//		MapperService mapperService = container.getExtension(MapperService.class);
//		in.setMapperService(mapperService);
//		getInInterceptors().add(in);
	}

	@Override
	public void service(WmixMessage message) throws Exception {
		Exchange ex = message.getExchange();
		ex.put(RouteRepository.class, reposiotry);
		ex.put(Transporter.class, getTransporter());
		if (ex.get(Message.ENCODING) == null) {
			ex.put(Message.ENCODING, encoding);
		}
		getTransporter().invoke(message);
	}

	@Override
	protected Logger getLogger() {
		return LOG;
	}

	@Override
	protected Service createService() {
		return serviceFactory.create();
	}

	public Set<String> getIncludePackages() {
		return includePackages;
	}

	public void setIncludePackages(Set<String> includePackages) {
		this.includePackages = includePackages;
	}

	public Set<String> getExcludePackages() {
		return excludePackages;
	}

	public void setExcludePackages(Set<String> excludePackages) {
		this.excludePackages = excludePackages;
	}

}
