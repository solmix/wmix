package org.solmix.wmix.exchange;

import org.solmix.runtime.exchange.Exchange;
import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Protocol;
import org.solmix.runtime.exchange.model.ProtocolInfo;
import org.solmix.runtime.exchange.support.DefaultMessage;
import org.solmix.runtime.interceptor.support.InterceptorProviderSupport;

public class WmixProtocol extends InterceptorProviderSupport implements Protocol {

	private static final long serialVersionUID = 2281575158621757145L;

	private ProtocolInfo protocolInfo;
	
	public WmixProtocol(ProtocolInfo info){
	    protocolInfo=info;
	}
	@Override
	public Message createMessage() {
		return createMessage(new DefaultMessage());
	}

	@Override
	public Message createMessage(Message m) {
	    if(!m.containsKey(Message.CONTENT_TYPE)){
	        String ct = null;
	            
	            Exchange exchange = m.getExchange();
	            if (exchange != null) {
	                ct = (String)exchange.get(Message.CONTENT_TYPE);
	            }
	            m.put(Message.CONTENT_TYPE, ct);
	    }
		return m;
	}

	@Override
	public ProtocolInfo getProtocolInfo() {
		return protocolInfo;
	}

}
