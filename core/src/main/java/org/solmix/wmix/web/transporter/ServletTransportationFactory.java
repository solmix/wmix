package org.solmix.wmix.web.transporter;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.solmix.runtime.Container;
import org.solmix.runtime.exchange.Pipeline;
import org.solmix.runtime.exchange.PipelineFactory;
import org.solmix.runtime.exchange.Transporter;
import org.solmix.runtime.exchange.TransporterFactory;
import org.solmix.runtime.exchange.model.EndpointInfo;
import org.solmix.runtime.exchange.support.TransportDetectSupport;

public class ServletTransportationFactory implements PipelineFactory,
		TransporterFactory,TransportDetectSupport {

	private static final Set<String> URI_PREFIXES = new HashSet<String>();
	static {
		URI_PREFIXES.add("http://");
		URI_PREFIXES.add("https://");
	}

	public static final List<String> TRANSPORT_TYPES = Arrays.asList("http://transport.solmix.org/http/servlet");
	
	@Override
	public Transporter getTransporter(EndpointInfo ei, Container container)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pipeline getPipeline(EndpointInfo info, Container c)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pipeline getPipeline(EndpointInfo info, String address, Container c)
			throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<String> getUriPrefixes() {
		return URI_PREFIXES;
	}

	@Override
	public List<String> getTransportTypes() {
		return TRANSPORT_TYPES;
	}

}
