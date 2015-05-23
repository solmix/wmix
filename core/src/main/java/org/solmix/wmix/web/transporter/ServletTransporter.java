package org.solmix.wmix.web.transporter;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Pipeline;
import org.solmix.runtime.exchange.Processor;
import org.solmix.runtime.exchange.Transporter;
import org.solmix.runtime.exchange.support.TransportDetectSupport;

public class ServletTransporter implements Transporter,TransportDetectSupport {

	@Override
	public Processor getProcessor() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setProcessor(Processor processor) {
		// TODO Auto-generated method stub

	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub

	}

	@Override
	public String getAddress() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Pipeline getBackPipeline(Message msg) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getDefaultPort() {
		throw new UnsupportedOperationException("Servlet transporter no need port");
	}

	@Override
	public Set<String> getUriPrefixes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getTransportTypes() {
		// TODO Auto-generated method stub
		return null;
	}

}
