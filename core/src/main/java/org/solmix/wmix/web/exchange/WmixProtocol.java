package org.solmix.wmix.web.exchange;

import org.solmix.runtime.exchange.Message;
import org.solmix.runtime.exchange.Protocol;
import org.solmix.runtime.exchange.model.ProtocolInfo;
import org.solmix.runtime.interceptor.support.InterceptorProviderSupport;

public class WmixProtocol extends InterceptorProviderSupport implements Protocol {

	private static final long serialVersionUID = 2281575158621757145L;

	@Override
	public Message createMessage() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Message createMessage(Message m) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ProtocolInfo getProtocolInfo() {
		// TODO Auto-generated method stub
		return null;
	}

}
